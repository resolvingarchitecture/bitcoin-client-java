package ra.btc.bitcoinj;

import javafx.application.Platform;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.net.discovery.PeerDiscovery;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.*;
import ra.btc.BitcoinClient;
import ra.btc.BitcoinService;
import ra.common.Envelope;
import ra.common.tasks.TaskRunner;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class BitcoinJClient implements BitcoinClient {

    private static final Logger LOG = Logger.getLogger(BitcoinJClient.class.getName());

    private final BitcoinService service;
    private final TaskRunner taskRunner;

    private NetworkParameters params;
    private File walletFile;
    private Wallet wallet;
    private PeerDiscovery peerDiscovery;
    private List<PeerAddress> seedPeerAddresses = new ArrayList<>();

    protected volatile Context context;

    public BitcoinJClient(BitcoinService service, TaskRunner taskRunner) {
        this.service = service;
        this.taskRunner = taskRunner;
    }

    @Override
    public boolean init(Properties config) throws Exception {
        Context.propagate(context);
        File directory = service.getServiceDirectory();
        File chainFile = new File(directory, "bitcoinj.spvchain");
        boolean chainFileExists = chainFile.exists();
        if (chainFileExists && isChainFileLocked()) {
            LOG.severe("This service is already running and cannot be started twice.");
            return false;
        }
        // init wallet
        walletFile = new File(directory, "bitcoinj.wallet");
        if(walletFile.exists()) {
            try {
                wallet = Wallet.loadFromFile(walletFile);
            } catch (UnreadableWalletException e) {
                LOG.info("Unable to load wallet from file.");
            }
        } else {
            KeyChainGroup.Builder kcg = KeyChainGroup.builder(params, KeyChainGroupStructure.DEFAULT);
            wallet = new Wallet(params, kcg.build()); // default
            wallet.saveToFile(walletFile);
        }
        // init network
        String env = config.getProperty("ra.env");
        if("test".equalsIgnoreCase(env) || "qa".equalsIgnoreCase(env)) {
            params = TestNet3Params.get();
            LOG.info("BitcoinJ Client in Test mode.");
        } else if("prod".equalsIgnoreCase(env)) {
            params = MainNetParams.get();
            LOG.info("BitcoinJ Client in Production mode.");
        } else {
            params = RegTestParams.get();
            LOG.info("BitcoinJ Client in RegTest mode.");
        }
        // init peers
        String peersStr = config.getProperty("ra.bitcoin.bitcoinj.peers.addresses");
        if(peersStr!=null) {
            String[] addresses = peersStr.split(",");
            for(String addr : addresses) {
                seedPeerAddresses.add(new PeerAddress(params, addr, params.getPort()));
            }
        }
        // init discovery if provided
        String discoProp = config.getProperty("ra.bitcoin.bitcoinj.peers.discovery");
        if(discoProp!=null) {
            peerDiscovery = (PeerDiscovery) Class.forName(discoProp).newInstance();
            if(peerDiscovery instanceof NetworkManagerPeerDiscovery) {
                NetworkManagerPeerDiscovery netMgrDisco = (NetworkManagerPeerDiscovery)peerDiscovery;
                netMgrDisco.init(service, this, config, seedPeerAddresses, taskRunner);
            }
        }
        return true;
    }

    @Override
    public void handleDocument(Envelope e) {

    }

    @Override
    public boolean destroy() throws Exception {
        if(wallet!=null && walletFile!=null && (walletFile.exists() || walletFile.createNewFile()) && walletFile.canWrite()) {
            wallet.saveToFile(walletFile);
        } else {
            LOG.warning("Unable to save wallet.");
        }
        return true;
    }

    @Override
    public boolean destroyGracefully() throws Exception {
        return destroy();
    }

    /**
     * Tests to see if the spvchain file has an operating system file lock on it. Useful for checking if your app
     * is already running. If another copy of your app is running and you start the appkit anyway, an exception will
     * be thrown during the startup process. Returns false if the chain file does not exist or is a directory.
     */
    public boolean isChainFileLocked() throws IOException {
        RandomAccessFile file2 = null;
        try {
            File file = new File(service.getServiceDirectory(), "bitcoinj.spvchain");
            if (!file.exists())
                return false;
            if (file.isDirectory())
                return false;
            file2 = new RandomAccessFile(file, "rw");
            FileLock lock = file2.getChannel().tryLock();
            if (lock == null)
                return true;
            lock.release();
            return false;
        } finally {
            if (file2 != null)
                file2.close();
        }
    }
}
