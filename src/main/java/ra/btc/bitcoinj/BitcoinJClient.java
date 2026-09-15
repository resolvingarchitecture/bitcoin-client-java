package ra.btc.bitcoinj;

import org.bitcoinj.base.BitcoinNetwork;
import org.bitcoinj.base.Coin;
import org.bitcoinj.base.ScriptType;
import org.bitcoinj.base.Sha256Hash;
import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.ECKey;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.net.BlockingClientManager;
import org.bitcoinj.net.ClientConnectionManager;
import org.bitcoinj.net.discovery.PeerDiscovery;
import org.bitcoinj.net.discovery.PeerDiscoveryException;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroupStructure;
import ra.btc.BTCEscrow;
import ra.btc.BitcoinClient;
import ra.btc.BitcoinService;
import ra.common.Envelope;
import ra.common.network.NetworkPeer;
import ra.common.route.Route;
import ra.common.service.Service;

import javax.net.SocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * BitcoinJ-backed {@link BitcoinClient}, used when no local Bitcoin Core node is detected
 * ({@link ra.btc.BitcoinService#localNodeRunning()}). Runs an SPV wallet via
 * {@link WalletAppKit} (bitcoinj 0.17.1 - package layout and constructors matching the working
 * reference in {@code 1m5-android}'s {@code WalletService}).
 *
 * <p>BitcoinJ must never connect to peers directly - all P2P connections should be routable
 * through whichever network this node is currently using (Tor/I2P/1M5), so BitcoinJ's own DNS
 * peer discovery is disabled ({@link #getPeers(long, Duration)} always returns {@code null};
 * peer addresses are meant to arrive from the RA network manager instead - see
 * {@link #getBitcoinPeers()}, still a TODO wire-up) and, when {@code ra.btc.socks.host}/
 * {@code ra.btc.socks.port} are configured, the peer group's connection manager is swapped for a
 * {@link BlockingClientManager} routed through that SOCKS proxy (bitcoinj's own
 * {@code BlockingClient} javadoc names this as the supported way to connect over a proxy - the
 * newer NIO transport it uses by default cannot).
 */
public class BitcoinJClient implements BitcoinClient, PeerDiscovery {

    private static final Logger LOG = Logger.getLogger(BitcoinJClient.class.getName());

    private final BitcoinService service;

    private DeterministicSeed restoreFromSeed;
    private DeterministicKey restoreFromKey;

    private NetworkParameters params;
    private WalletAppKit kit;

    private final Map<UUID, BTCEscrow> escrows = new HashMap<>();

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
        File directory = service.getServiceDirectory();

        String env = config.getProperty("ra.env");
        final BitcoinNetwork network;
        if ("test".equalsIgnoreCase(env) || "qa".equalsIgnoreCase(env)) {
            network = BitcoinNetwork.TESTNET;
            LOG.info("BitcoinJ Client in Test mode.");
        } else if ("prod".equalsIgnoreCase(env)) {
            network = BitcoinNetwork.MAINNET;
            LOG.info("BitcoinJ Client in Production mode.");
        } else {
            network = BitcoinNetwork.REGTEST;
            LOG.info("BitcoinJ Client in RegTest mode.");
        }
        params = NetworkParameters.of(network);
        Context.propagate(new Context(params));

        final String socksHost = config.getProperty("ra.btc.socks.host");
        final int socksPort = parseIntOrDefault(config.getProperty("ra.btc.socks.port"), 0);
        final boolean useSocks = socksHost != null && !socksHost.isEmpty() && socksPort > 0;

        kit = new WalletAppKit(network, ScriptType.P2WPKH, KeyChainGroupStructure.BIP32, directory, "bitcoinj") {
            @Override
            protected PeerGroup createPeerGroup() {
                if (!useSocks) {
                    return super.createPeerGroup();
                }
                LOG.info("Routing Bitcoin P2P connections through SOCKS proxy " + socksHost + ":" + socksPort);
                SocketFactory socksSocketFactory = socksSocketFactory(socksHost, socksPort);
                ClientConnectionManager connectionManager = new BlockingClientManager(socksSocketFactory);
                return new SocksRoutedPeerGroup(network, vChain, connectionManager);
            }
        };

        if (restoreFromSeed != null) {
            kit.restoreWalletFromSeed(restoreFromSeed);
        } else if (restoreFromKey != null) {
            kit.restoreWalletFromKey(restoreFromKey);
        }

        if (kit.isChainFileLocked()) {
            LOG.severe("This service is already running and cannot be started twice.");
            return false;
        }

        // We never want BitcoinJ to discover/connect to peers on its own (DNS or otherwise) -
        // all peer addresses should be routed through the RA network manager, see getPeers().
        if (network == BitcoinNetwork.REGTEST) {
            kit.connectToLocalHost();
        } else {
            kit.setDiscovery(this);
        }
        kit.setBlockingStartup(false);

        kit.startAsync();
        try {
            kit.awaitRunning();
        } catch (RuntimeException e) {
            LOG.severe("BitcoinJ WalletAppKit failed to start: " + e.getMessage());
            return false;
        }
        LOG.info("BitcoinJ WalletAppKit is running.");
        return true;
    }

    private static int parseIntOrDefault(String value, int def) {
        if (value == null || value.isEmpty()) return def;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static SocketFactory socksSocketFactory(String socksHost, int socksPort) {
        final Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(socksHost, socksPort));
        return new SocketFactory() {
            @Override
            public Socket createSocket() {
                // Unconnected, proxy-bound socket - BlockingClient calls socket.connect(address, timeout) itself.
                return new Socket(proxy);
            }
            @Override
            public Socket createSocket(String host, int port) throws IOException {
                throw new UnsupportedOperationException("only createSocket() is used by BlockingClient");
            }
            @Override
            public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
                throw new UnsupportedOperationException("only createSocket() is used by BlockingClient");
            }
            @Override
            public Socket createSocket(InetAddress host, int port) throws IOException {
                throw new UnsupportedOperationException("only createSocket() is used by BlockingClient");
            }
            @Override
            public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
                throw new UnsupportedOperationException("only createSocket() is used by BlockingClient");
            }
        };
    }

    /**
     * {@link PeerGroup}'s connection-manager constructor is {@code protected}; this subclass
     * exists only to reach it from {@link BitcoinJClient}'s {@code createPeerGroup()} override.
     */
    private static final class SocksRoutedPeerGroup extends PeerGroup {
        SocksRoutedPeerGroup(BitcoinNetwork network, AbstractBlockChain chain, ClientConnectionManager connectionManager) {
            super(network, chain, connectionManager);
        }
    }

    @Override
    public void handleDocument(Envelope e) {
        Route route = e.getRoute();
        String operation = route.getOperation();
        switch(operation) {
            case OPERATION_BITCOIN_PEERS: {
                // Incoming Response from Bitcoin Peers request
                Object nPeersObj = e.getValue(NetworkPeer.class.getName());
                if(nPeersObj instanceof List) {
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
    public List<InetSocketAddress> getPeers(long services, Duration timeout) throws PeerDiscoveryException {
        // We never want BitcoinJ to attempt to make the connections directly to Peers,
        // we want it to go through Network Manager in case it gets blocked (e.g. can switch to Tor)
        LOG.info("BitcoinJ requesting peers...ignoring.");
        return null;
    }

    @Override
    public void shutdown() {
        // Just ignore
        LOG.info("BitcoinJ indicating it is shutting down.");
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
        kit.peerGroup().broadcastTransaction(escrow.tx);
        return escrow;
    }

    /**
     * Step 6A - Complete Escrow
     */
    private void completeEscrow(BTCEscrow escrow) {
        TransactionOutput multisigOutput = escrow.tx.getOutput(0);
        escrow.tx = new Transaction(params);
        escrow.tx.addOutput(multisigOutput.getValue(), escrow.sendingParty);

        kit.peerGroup().broadcastTransaction(escrow.tx);
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

        kit.peerGroup().broadcastTransaction(escrow.tx);
    }

    @Override
    public boolean destroy() throws Exception {
        // WalletAppKit auto-saves the wallet (default) and persists the SPV chain file itself;
        // stopAsync()/awaitTerminated() flushes both to disk.
        if (kit != null) {
            kit.stopAsync();
            kit.awaitTerminated();
        }
        return true;
    }

    @Override
    public boolean destroyGracefully() throws Exception {
        return destroy();
    }
}
