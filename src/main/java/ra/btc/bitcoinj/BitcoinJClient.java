package ra.btc.bitcoinj;

import org.bitcoinj.base.Address;
import org.bitcoinj.base.BitcoinNetwork;
import org.bitcoinj.base.Coin;
import org.bitcoinj.base.ScriptType;
import org.bitcoinj.base.Sha256Hash;
import org.bitcoinj.base.exceptions.AddressFormatException;
import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.BitcoinSerializer;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionBroadcast;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.ECKey;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.net.BlockingClientManager;
import org.bitcoinj.net.ClientConnectionManager;
import org.bitcoinj.net.discovery.PeerDiscovery;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptException;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.KeyChainGroupStructure;
import org.bitcoinj.wallet.Wallet;
import ra.btc.BTCEscrow;
import ra.btc.BitcoinClient;
import ra.btc.BitcoinService;
import ra.common.Envelope;
import ra.common.route.Route;

import javax.net.SocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * BitcoinJ-backed {@link BitcoinClient}, used when no local Bitcoin Core node is detected
 * ({@link ra.btc.BitcoinService#localNodeRunning()}). Runs an SPV wallet via
 * {@link WalletAppKit} (bitcoinj 0.17.1 - package layout and constructors matching the working
 * reference in {@code 1m5-android}'s {@code WalletService}).
 *
 * <p>Peer connectivity matches what {@code 1m5-android}'s {@code WalletService} actually does
 * (checked directly against its current source 2026-09-19, not assumed from this class's own
 * older comment, which claimed peer discovery was unconditionally disabled pending a
 * {@code ra.networkmanager.NetworkManagerService} that was never built - dead code, removed):
 * an explicit peer list (config's {@code ra.btc.peers}, comma-separated {@code host:port})
 * is used when configured, via {@link StaticBitcoinPeerDiscovery}; otherwise
 * {@link WalletAppKit}'s own default discovery (bitcoinj's built-in DNS-seed
 * {@code MultiplexingDiscovery}) is left untouched, exactly {@code WalletService}'s own
 * fallback. When {@code ra.btc.socks.host}/{@code ra.btc.socks.port} are configured (either
 * way), the peer group's connection manager is swapped for a {@link BlockingClientManager}
 * routed through that SOCKS proxy (bitcoinj's own {@code BlockingClient} javadoc names this as
 * the supported way to connect over a proxy - the newer NIO transport it uses by default
 * cannot) - this is how a deployment routes its Bitcoin P2P traffic over Tor/I2P.
 */
public class BitcoinJClient implements BitcoinClient {

    private static final Logger LOG = Logger.getLogger(BitcoinJClient.class.getName());

    private final BitcoinService service;

    private DeterministicSeed restoreFromSeed;
    private DeterministicKey restoreFromKey;

    private NetworkParameters params;
    private WalletAppKit kit;
    private long broadcastTimeoutMs = 15_000;

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

        broadcastTimeoutMs = parseLongOrDefault(config.getProperty("ra.btc.broadcastTimeoutMs"), 15_000);

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

        if (network == BitcoinNetwork.REGTEST) {
            kit.connectToLocalHost();
        } else {
            List<InetSocketAddress> explicitPeers = parsePeers(config.getProperty("ra.btc.peers"));
            if (!explicitPeers.isEmpty()) {
                LOG.info("Using explicit Bitcoin peers: " + explicitPeers);
                kit.setDiscovery(new StaticBitcoinPeerDiscovery(explicitPeers));
            } else {
                LOG.info("No explicit Bitcoin peers configured (ra.btc.peers) - using BitcoinJ's "
                        + "own default peer discovery, same fallback 1m5-android's WalletService uses.");
                // Deliberately not calling kit.setDiscovery(...) here - leaving WalletAppKit's
                // own PeerGroup default discovery (bitcoinj's built-in DNS-seed
                // MultiplexingDiscovery) in place.
            }
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
    
    private static long parseLongOrDefault(String value, long def) {
        if (value == null || value.isEmpty()) return def;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /** Java 8 target (Android) - no {@code java.util.HexFormat} (Java 17+) available. */
    private static byte[] decodeHex(String hex) {
        if ((hex.length() & 1) != 0) {
            throw new IllegalArgumentException("odd-length hex string");
        }
        byte[] out = new byte[hex.length() / 2];
        for (int i = 0; i < out.length; i++) {
            int hi = Character.digit(hex.charAt(i * 2), 16);
            int lo = Character.digit(hex.charAt(i * 2 + 1), 16);
            if (hi < 0 || lo < 0) {
                throw new IllegalArgumentException("invalid hex character at position " + (i * 2));
            }
            out[i] = (byte) ((hi << 4) | lo);
        }
        return out;
    }

    /** {@code ra.btc.peers}: comma-separated {@code host:port} entries. Unparseable entries are skipped, logged, not fatal. */
    private static List<InetSocketAddress> parsePeers(String value) {
        List<InetSocketAddress> peers = new ArrayList<>();
        if (value == null || value.isBlank()) return peers;
        for (String entry : value.split(",")) {
            String hostPort = entry.trim();
            if (hostPort.isEmpty()) continue;
            int colon = hostPort.lastIndexOf(':');
            if (colon <= 0 || colon == hostPort.length() - 1) {
                LOG.warning("skipping malformed ra.btc.peers entry (expected host:port): " + hostPort);
                continue;
            }
            try {
                String host = hostPort.substring(0, colon);
                int port = Integer.parseInt(hostPort.substring(colon + 1));
                peers.add(InetSocketAddress.createUnresolved(host, port));
            } catch (NumberFormatException e) {
                LOG.warning("skipping malformed ra.btc.peers entry (bad port): " + hostPort);
            }
        }
        return peers;
    }

    /**
     * Matches {@code 1m5-android}'s own {@code WalletService.StaticBitcoinPeerDiscovery} exactly
     * - a fixed, operator-supplied peer list, no DNS lookups of its own.
     */
    private static final class StaticBitcoinPeerDiscovery implements PeerDiscovery {
        private final List<InetSocketAddress> peers;

        StaticBitcoinPeerDiscovery(List<InetSocketAddress> peers) {
            this.peers = new ArrayList<>(peers);
        }

        @Override
        public List<InetSocketAddress> getPeers(long services, Duration timeout) {
            return new ArrayList<>(peers);
        }

        @Override
        public void shutdown() {}
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
            case OPERATION_GET_BALANCE: {
                Coin available = kit.wallet().getBalance(Wallet.BalanceType.AVAILABLE);
                Coin estimated = kit.wallet().getBalance(Wallet.BalanceType.ESTIMATED);
                e.setHeader(HEADER_AVAILABLE_SATS, String.valueOf(available.value));
                e.setHeader(HEADER_BALANCE_SATS, String.valueOf(estimated.value));
                break;
            }
            case OPERATION_GET_RECEIVE_ADDRESS: {
                e.setHeader(HEADER_ADDRESS, kit.wallet().currentReceiveAddress().toString());
                break;
            }
            case OPERATION_LIST_TRANSACTIONS: {
                // Dependency-free encoding, not org.json: this module runs on plain-JVM hosts
                // (the 1m5-core-java desktop daemon) as well as Android (via 1m5-remnant), and
                // org.json is bundled on Android's boot classpath - adding the standalone
                // org.json:json artifact here would duplicate those classes on the Android side,
                // the exact `mergeDebugJavaResource`/checkDebugDuplicateClasses failure shape
                // 1m5-remnant's step 1 already hit once with BouncyCastle (see its TODO.md).
                // One record per line: txid|valueSats|confirmations|updateTimeMillis - none of
                // these fields can themselves contain '|' or '\n'.
                StringBuilder sb = new StringBuilder();
                try {
                    for (Transaction tx : kit.wallet().getTransactions(false)) {
                        long valueSats = tx.getValue(kit.wallet()).value;
                        int confirmations = tx.getConfidence().getDepthInBlocks();
                        long updateTimeMillis = tx.getUpdateTime().getTime();
                        sb.append(tx.getTxId()).append('|').append(valueSats).append('|')
                                .append(confirmations).append('|').append(updateTimeMillis).append('\n');
                    }
                    e.addContent(sb.toString().getBytes(StandardCharsets.UTF_8));
                } catch (ScriptException ex) {
                    e.addErrorMessage("could not list transactions: " + ex.getMessage());
                }
                break;
            }
            case OPERATION_SEND: {
                String addressStr = stringHeaderOrNull(e, HEADER_ADDRESS);
                String amountStr = stringHeaderOrNull(e, HEADER_AMOUNT_SATS);
                if (addressStr == null) {
                    e.addErrorMessage("missing " + HEADER_ADDRESS);
                    break;
                }
                try {
                    Address address = Address.fromString(params, addressStr);
                    long sats = Long.parseLong(amountStr);
                    Wallet.SendResult result = kit.wallet().sendCoins(kit.peerGroup(), address, Coin.valueOf(sats));
                    e.setHeader(HEADER_TXID, result.transaction().getTxId().toString());
                } catch (AddressFormatException ex) {
                    e.addErrorMessage("invalid address: " + addressStr);
                } catch (NumberFormatException ex) {
                    e.addErrorMessage("invalid amount: " + amountStr);
                } catch (InsufficientMoneyException ex) {
                    e.addErrorMessage("insufficient balance: need " + ex.missing + " more sats");
                } catch (RuntimeException ex) {
                    // Covers Wallet.TransactionCompletionException (itself a RuntimeException) and
                    // anything else sendCoins/Address.fromString can throw unchecked.
                    e.addErrorMessage("send failed: " + ex.getMessage());
                }
                break;
            }
            case OPERATION_SYNC_STATUS: {
                boolean syncing = kit.peerGroup() != null && kit.peerGroup().getDownloadPeer() != null;
                int bestHeight = kit.chain() != null ? kit.chain().getBestChainHeight() : -1;
                int connectedPeers = kit.peerGroup() != null ? kit.peerGroup().getConnectedPeers().size() : 0;
                e.setHeader(HEADER_SYNCING, String.valueOf(syncing));
                e.setHeader(HEADER_BEST_HEIGHT, String.valueOf(bestHeight));
                e.setHeader(HEADER_CONNECTED_PEERS, String.valueOf(connectedPeers));
                break;
            }
            case OPERATION_BROADCAST_TRANSACTION: {
                String rawTxHex = stringHeaderOrNull(e, HEADER_RAW_TX_HEX);
                if (rawTxHex == null) {
                    e.addErrorMessage("missing " + HEADER_RAW_TX_HEX);
                    break;
                }
                byte[] raw;
                try {
                    raw = decodeHex(rawTxHex.trim());
                } catch (IllegalArgumentException ex) {
                    e.addErrorMessage("invalid raw transaction hex: " + ex.getMessage());
                    break;
                }
                Transaction tx;
                try {
                    tx = new BitcoinSerializer(params).makeTransaction(ByteBuffer.wrap(raw));
                } catch (RuntimeException ex) {
                    // Transaction.read() throws bitcoinj's own ProtocolException for some
                    // malformed inputs and a raw java.nio.BufferUnderflowException for others
                    // (confirmed by actually triggering both, not assumed) - either way, the
                    // hex decoded fine but the bytes aren't a valid transaction structure.
                    e.addErrorMessage("could not parse raw transaction: " + ex.getMessage());
                    break;
                }
                try {
                    TransactionBroadcast broadcast = kit.peerGroup().broadcastTransaction(tx);
                    try {
                        // Bounded wait for real propagation confirmation, not just "we tried" -
                        // still returns the txid on a timeout below, since the broadcast itself
                        // was already sent and propagation simply takes time.
                        broadcast.awaitRelayed().get(broadcastTimeoutMs, TimeUnit.MILLISECONDS);
                    } catch (TimeoutException ignored) {
                        LOG.info("broadcast of " + tx.getTxId() + " sent, not yet confirmed relayed after "
                                + broadcastTimeoutMs + "ms - propagation continues in the background");
                    }
                    e.setHeader(HEADER_TXID, tx.getTxId().toString());
                } catch (ExecutionException ex) {
                    String reason = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    e.addErrorMessage("broadcast rejected: " + reason);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    e.addErrorMessage("broadcast interrupted");
                }
                break;
            }
        }
    }

    /** {@code null} if the header is absent - {@link Envelope#getHeader} returns {@code Object}, no plain string accessor. */
    private static String stringHeaderOrNull(Envelope e, String name) {
        Object v = e.getHeader(name);
        return v != null ? String.valueOf(v) : null;
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
