package ra.btc.bitcoinj;

import org.bitcoinj.core.*;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.net.discovery.PeerDiscovery;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.*;
import ra.btc.BTCEscrow;
import ra.btc.BitcoinClient;
import ra.btc.BitcoinService;
import ra.common.Envelope;
import ra.common.network.NetworkPeer;
import ra.common.route.Route;
import ra.common.service.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileLock;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BitcoinJClient implements BitcoinClient, PeerDiscovery {

    private static final Logger LOG = Logger.getLogger(BitcoinJClient.class.getName());

    private final BitcoinService service;

    private final String userAgent = "RA-Bitcoin-Service";
    private final String version = "1";
    private NetworkParameters params;
    private SPVBlockStore store;
    private BlockChain chain;
    private PeerGroup peerGroup;
    private InputStream checkpoints;
    private DeterministicSeed restoreFromSeed;
    private DeterministicKey restoreFromKey;
    private PeerDiscovery discovery;

    private File walletFile;
    private Wallet wallet;

    private Map<UUID, BTCEscrow> escrows = new HashMap<>();

    protected volatile Context context;

    public BitcoinJClient(BitcoinService service) {
        this.service = service;
    }

    public BitcoinJClient(BitcoinService service, DeterministicSeed deterministicSeed) {
        this.service = service;
        this.restoreFromSeed = deterministicSeed;
    }

    public BitcoinJClient(BitcoinService service, DeterministicKey deterministicKey) {
        this.service = service;
        this.restoreFromKey = deterministicKey;
    }

    @Override
    public boolean init(Properties config) throws Exception {

        Context.propagate(context);
        File directory = service.getServiceDirectory();
        try {
            File chainFile = new File(directory, "bitcoinj.spvchain");
            boolean chainFileExists = chainFile.exists();
            if (chainFileExists && isChainFileLocked()) {
                LOG.severe("This service is already running and cannot be started twice.");
                return false;
            }
            // init wallet
            walletFile = new File(directory, "bitcoinj.wallet");
            if (walletFile.exists()) {
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
            if ("test".equalsIgnoreCase(env) || "qa".equalsIgnoreCase(env)) {
                params = TestNet3Params.get();
                LOG.info("BitcoinJ Client in Test mode.");
            } else if ("prod".equalsIgnoreCase(env)) {
                params = MainNetParams.get();
                LOG.info("BitcoinJ Client in Production mode.");
            } else {
                params = RegTestParams.get();
                LOG.info("BitcoinJ Client in RegTest mode.");
            }

            // Initiate Bitcoin network objects (block store, blockchain and peer group)
            store = new SPVBlockStore(params, chainFile);
            if (!chainFileExists || restoreFromSeed != null || restoreFromKey != null) {
                if (checkpoints == null && !Utils.isAndroidRuntime()) {
                    checkpoints = CheckpointManager.openStream(params);
                }
                if (checkpoints != null) {
                    // Initialize the chain file with a checkpoint to speed up first-run sync.
                    long time;
                    if (restoreFromSeed != null) {
                        time = restoreFromSeed.getCreationTimeSeconds();
                        if (chainFileExists) {
                            LOG.info("Clearing the chain file in preparation for restore.");
                            store.clear();
                        }
                    } else if (restoreFromKey != null) {
                        time = restoreFromKey.getCreationTimeSeconds();
                        if (chainFileExists) {
                            LOG.info("Clearing the chain file in preparation for restore.");
                            store.clear();
                        }
                    } else {
                        time = wallet.getEarliestKeyCreationTime();
                    }
                    if (time > 0)
                        CheckpointManager.checkpoint(params, checkpoints, store, time);
                    else
                        LOG.warning("Creating a new un-check-pointed block store due to a wallet with a creation time of zero: this will result in a very slow chain sync");
                } else if (chainFileExists) {
                    LOG.info("Clearing the chain file in preparation for restore.");
                    store.clear();
                }
            }
            chain = new BlockChain(params, store);
            peerGroup = new PeerGroup(params, chain);
            peerGroup.setUserAgent(userAgent, version);

            // Set up peer addresses or discovery first, so if wallet extensions try to broadcast a transaction
            // before we're actually connected the broadcast waits for an appropriate number of connections.
            if (peerAddresses != null) {
                for (PeerAddress addr : peerAddresses) peerGroup.addAddress(addr);
                peerGroup.setMaxConnections(peerAddresses.length);
                peerAddresses = null;
            } else if (!params.getId().equals(NetworkParameters.ID_REGTEST)) {
                peerGroup.addPeerDiscovery(this);
            }
            chain.addWallet(wallet);
            peerGroup.addWallet(wallet);
            onSetupCompleted();

            if (blockingStartup) {
                peerGroup.start();
                // Make sure we shut down cleanly.
                installShutdownHook();

                // TODO: Be able to use the provided download listener when doing a blocking startup.
                final DownloadProgressTracker listener = new DownloadProgressTracker();
                peerGroup.startBlockChainDownload(listener);
                listener.await();
            } else {
                peerGroup.startAsync().whenComplete((result, t) -> {
                    if (t == null) {
                        final DownloadProgressTracker l = downloadListener == null ? new DownloadProgressTracker() : downloadListener;
                        peerGroup.startBlockChainDownload(l);
                    } else {
                        throw new RuntimeException(t);
                    }
                });
            }
        } catch (BlockStoreException e) {
            LOG.severe(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    @Override
    public void handleDocument(Envelope e) {
        Route route = e.getRoute();
        String operation = route.getOperation();
        switch(operation) {
            case OPERATION_BITCOIN_PEERS: {
                // Incoming Response from Bitcoin Peers request
                Object nPeersObj = e.getValue(NetworkPeer.class.getName());
                if(nPeersObj!=null && nPeersObj instanceof List) {
                    List<NetworkPeer> nPeers = (List<NetworkPeer>)nPeersObj;
                    for(NetworkPeer np : nPeers) {

                    }
                }
                break;
            }
        }
    }

    private void getBitcoinPeers() {
        Envelope e = Envelope.documentFactory();
        e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_BITCOIN_PEERS);
        e.addRoute("ra.networkmanager.NetworkManagerService", "PEERS_BY_SERVICE");
        e.addNVP(Service.class.getName(), BitcoinService.class.getName());
        service.send(e);
    }

    @Override
    public List<InetSocketAddress> getPeers(long l, long l1, TimeUnit timeUnit) {
        // We never want BitcoinJ to attempt to make the connections directly to Peers,
        // we want it to go through Network Manager in case it gets blocked (e.g. can switch to Tor)
        LOG.info("BitcoinJ requesting peers...ignoring.");
        return null;
    }

    /**
     * Sending party (of BTC) creates an Escrow with a selected Receiving party
     * by sending BTC to an Escrow address controlled by both the Sending and
     * Receiving party.
     *
     * Once the Sending party is satisfied conditions have been met, they release
     * the Bitcoin to be sent to the Receiving party.
     *
     * If the Sending party is not satisfied conditions have been met and they
     * wish for a refund, they ask the Receiving party to release the funds back
     * to the Sending party.
     *
     * For this service, the peer with the Bitcoin always initiates the escrow as the Sending party.
     * A fee for the service is paid up-front first using the Lightning Network to keep chain fees down.
     */

    /**
     * Step 1 - Obtain Receiving Party's Public Key
     */

    /**
     * Step 2 - Request Fee Address from Dev Group Peer
     */

    /**
     * Step 3 - Send Fee to Dev Group Peer
     */

    /**
     * Step 4 - Receive Signature from Dev Group Peer
     */

    /**
     * Step 5 - Initiate Escrow Transaction
     * @param sendingParty
     * @param receivingParty
     * @param satsToSend
     * @param estimatedNetworkFeeSats
     * @param devGroupFeeSats
     * @return
     */
    private BTCEscrow initiateEscrow(ECKey sendingParty, ECKey receivingParty, Long satsToSend, Long estimatedNetworkFeeSats, Long devGroupFeeSats) {
        BTCEscrow escrow = new BTCEscrow();
        escrow.id = UUID.randomUUID();
        escrow.sendingParty = sendingParty;
        escrow.receivingParty = receivingParty;
        escrow.satsToSend = satsToSend;
        escrow.estimatedNetworkFeeSats = estimatedNetworkFeeSats;
        escrow.devGroupFeeSats = devGroupFeeSats;
        escrow.tx = new Transaction(params);
        Script script = ScriptBuilder.createMultiSigOutputScript(2, Arrays.asList(escrow.sendingParty, escrow.receivingParty));
        Coin amount = Coin.valueOf(escrow.satsToSend);
        escrow.tx.addOutput(amount, script);
        escrows.put(escrow.id, escrow);
        peerGroup.broadcastTransaction(escrow.tx);
        return escrow;
    }

    /**
     * Step 6A - Complete Escrow
     */
    private void completeEscrow(BTCEscrow escrow) {
        TransactionOutput multisigOutput = escrow.tx.getOutput(0);
        escrow.tx = new Transaction(params);
        escrow.tx.addOutput(multisigOutput.getValue(), escrow.sendingParty);

        peerGroup.broadcastTransaction(escrow.tx);
    }

    /**
     * Step 6B - Refund Escrow
     */
    private void refundEscrow(BTCEscrow escrow) {
        TransactionOutput multisigOutput = escrow.tx.getOutput(0);
        Script multisigScript = multisigOutput.getScriptPubKey();
        Coin value = multisigOutput.getValue();
        escrow.tx = new Transaction(params);
        escrow.tx.addOutput(value, escrow.sendingParty);
        escrow.tx.addInput(multisigOutput);
        Sha256Hash sigHash = escrow.tx.hashForSignature(0, multisigScript, Transaction.SigHash.ALL, false);
        escrow.receivingPartySig = escrow.receivingParty.sign(sigHash);

        peerGroup.broadcastTransaction(escrow.tx);
    }

    @Override
    public void shutdown() {
        // Just ignore
        LOG.info("BitcoinJ indicating it is shutting down.");
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
