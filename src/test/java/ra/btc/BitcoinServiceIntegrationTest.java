package ra.btc;

import org.bitcoinj.base.Address;
import org.bitcoinj.base.BitcoinNetwork;
import org.bitcoinj.base.Coin;
import org.bitcoinj.base.Sha256Hash;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.script.ScriptBuilder;
import org.junit.Test;
import ra.common.Client;
import ra.common.Envelope;
import ra.common.messaging.MessageProducer;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Exercises {@link BitcoinService} end-to-end against bitcoinj's RegTest network (no local
 * "bitcoind -regtest" instance required - the peer group's connection attempts happen
 * asynchronously and failing to connect does not block reaching RUNNING/SHUTDOWN).
 */
public class BitcoinServiceIntegrationTest {

    private static BitcoinService startedService() {
        return startedService(new Properties());
    }

    private static BitcoinService startedService(Properties p) {
        BitcoinService service = new BitcoinService(new MessageProducer() {
            @Override
            public boolean send(Envelope envelope) {
                return true;
            }
            @Override
            public boolean send(Envelope envelope, Client client) {
                return true;
            }
            @Override
            public boolean deadLetter(Envelope envelope) {
                return true;
            }
        }, new ServiceStatusObserver() {
            @Override
            public void serviceStatusChanged(String service, ServiceStatus serviceStatus) {
            }
        });
        // No ra.env -> BitcoinJClient defaults to RegTestParams/BitcoinNetwork.REGTEST.
        assertTrue("BitcoinService failed to start", service.start(p));
        assertEquals(ServiceStatus.RUNNING, service.getServiceStatus());
        return service;
    }

    @Test
    public void startsAndShutsDownCleanly() {
        BitcoinService service = startedService();
        assertTrue("BitcoinService failed to gracefully shut down", service.gracefulShutdown());
        assertEquals(ServiceStatus.GRACEFULLY_SHUTDOWN, service.getServiceStatus());
    }

    /**
     * {@code handleDocument} mutates the same {@link Envelope} it's given (there's no separate
     * reply object for these unit tests to inspect) - the exact envelope-round-trip shape
     * {@code network.onemfive.core.client.MsgTranslator} relies on in {@code 1m5-core-java}.
     */
    @Test
    public void getBalanceOfFreshWalletIsZero() {
        BitcoinService service = startedService();
        try {
            Envelope e = Envelope.documentFactory();
            e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_GET_BALANCE);
            service.handleDocument(e);
            assertEquals("0", e.getHeader(BitcoinClient.HEADER_AVAILABLE_SATS));
            assertEquals("0", e.getHeader(BitcoinClient.HEADER_BALANCE_SATS));
        } finally {
            service.gracefulShutdown();
        }
    }

    @Test
    public void getReceiveAddressReturnsANonEmptyAddress() {
        BitcoinService service = startedService();
        try {
            Envelope e = Envelope.documentFactory();
            e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_GET_RECEIVE_ADDRESS);
            service.handleDocument(e);
            Object address = e.getHeader(BitcoinClient.HEADER_ADDRESS);
            assertNotNull(address);
            assertFalse(String.valueOf(address).isEmpty());
        } finally {
            service.gracefulShutdown();
        }
    }

    @Test
    public void listTransactionsOfFreshWalletIsEmpty() {
        BitcoinService service = startedService();
        try {
            Envelope e = Envelope.documentFactory();
            e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_LIST_TRANSACTIONS);
            service.handleDocument(e);
            Object content = e.getContent();
            assertTrue(content == null || ((byte[]) content).length == 0);
        } finally {
            service.gracefulShutdown();
        }
    }

    @Test
    public void syncStatusReportsAChainHeight() {
        BitcoinService service = startedService();
        try {
            Envelope e = Envelope.documentFactory();
            e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_SYNC_STATUS);
            service.handleDocument(e);
            assertNotNull(e.getHeader(BitcoinClient.HEADER_SYNCING));
            assertNotNull(e.getHeader(BitcoinClient.HEADER_BEST_HEIGHT));
        } finally {
            service.gracefulShutdown();
        }
    }

    @Test
    public void sendToAnInvalidAddressFailsWithAnErrorMessage() {
        BitcoinService service = startedService();
        try {
            Envelope e = Envelope.documentFactory();
            e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_SEND);
            e.setHeader(BitcoinClient.HEADER_ADDRESS, "not-a-real-address");
            e.setHeader(BitcoinClient.HEADER_AMOUNT_SATS, "1000");
            service.handleDocument(e);
            assertNull(e.getHeader(BitcoinClient.HEADER_TXID));
            assertFalse(Envelope.getErrorMessages(e).isEmpty());
        } finally {
            service.gracefulShutdown();
        }
    }

    @Test
    public void sendWithInsufficientBalanceFailsWithAnErrorMessage() {
        BitcoinService service = startedService();
        try {
            Envelope e = Envelope.documentFactory();
            e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_SEND);
            // A structurally valid RegTest address (this service's own fresh receive address -
            // it has zero balance, so the point under test is the InsufficientMoneyException path,
            // not address validity).
            Envelope addrEnv = Envelope.documentFactory();
            addrEnv.addRoute(BitcoinService.class, BitcoinClient.OPERATION_GET_RECEIVE_ADDRESS);
            service.handleDocument(addrEnv);
            e.setHeader(BitcoinClient.HEADER_ADDRESS, addrEnv.getHeader(BitcoinClient.HEADER_ADDRESS));
            e.setHeader(BitcoinClient.HEADER_AMOUNT_SATS, "1000");
            service.handleDocument(e);
            assertNull(e.getHeader(BitcoinClient.HEADER_TXID));
            assertFalse(Envelope.getErrorMessages(e).isEmpty());
        } finally {
            service.gracefulShutdown();
        }
    }

    @Test
    public void broadcastWithMissingRawTxHexFailsWithAnErrorMessage() {
        BitcoinService service = startedService();
        try {
            Envelope e = Envelope.documentFactory();
            e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_BROADCAST_TRANSACTION);
            service.handleDocument(e);
            assertNull(e.getHeader(BitcoinClient.HEADER_TXID));
            assertFalse(Envelope.getErrorMessages(e).isEmpty());
        } finally {
            service.gracefulShutdown();
        }
    }

    @Test
    public void broadcastWithMalformedHexFailsWithAnErrorMessage() {
        BitcoinService service = startedService();
        try {
            Envelope e = Envelope.documentFactory();
            e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_BROADCAST_TRANSACTION);
            e.setHeader(BitcoinClient.HEADER_RAW_TX_HEX, "not-hex-at-all!!");
            service.handleDocument(e);
            assertNull(e.getHeader(BitcoinClient.HEADER_TXID));
            assertFalse(Envelope.getErrorMessages(e).isEmpty());
        } finally {
            service.gracefulShutdown();
        }
    }

    @Test
    public void broadcastWithValidHexButUnparseableTransactionFailsWithAnErrorMessage() {
        BitcoinService service = startedService();
        try {
            Envelope e = Envelope.documentFactory();
            e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_BROADCAST_TRANSACTION);
            // Valid hex, but nowhere near enough bytes to be a real transaction structure.
            e.setHeader(BitcoinClient.HEADER_RAW_TX_HEX, "deadbeef");
            service.handleDocument(e);
            assertNull(e.getHeader(BitcoinClient.HEADER_TXID));
            assertFalse(Envelope.getErrorMessages(e).isEmpty());
        } finally {
            service.gracefulShutdown();
        }
    }

    /**
     * No local RegTest node is listening in this test environment (same "no bitcoind -regtest
     * instance required" reasoning as every other test in this class) - {@code broadcastTransaction}
     * has no real peer to actually relay to, so this exercises {@code OPERATION_BROADCAST_TRANSACTION}'s
     * own timeout-is-not-failure path: the operation still parses the transaction, still attempts
     * the broadcast, and still reports the txid once the bounded {@code awaitRelayed()} wait times
     * out, rather than treating "not yet confirmed relayed" as an error.
     */
    @Test
    public void broadcastOfAWellFormedTransactionReturnsItsTxid() {
        Properties p = new Properties();
        // No real peer ever connects in this test environment (see class javadoc) - keep the
        // bounded awaitRelayed() wait short rather than the 15s production default, since this
        // test's whole point is exercising the "timeout is not failure" path, not measuring it.
        p.setProperty("ra.btc.broadcastTimeoutMs", "500");
        BitcoinService service = startedService(p);
        try {
            Envelope addrEnv = Envelope.documentFactory();
            addrEnv.addRoute(BitcoinService.class, BitcoinClient.OPERATION_GET_RECEIVE_ADDRESS);
            service.handleDocument(addrEnv);
            NetworkParameters params = NetworkParameters.of(BitcoinNetwork.REGTEST);
            Address address = Address.fromString(params, String.valueOf(addrEnv.getHeader(BitcoinClient.HEADER_ADDRESS)));

            Transaction tx = new Transaction(params);
            // A transaction needs at least one input to be a structurally valid, round-trippable
            // wire shape - a fabricated previous-output reference is fine here, since this only
            // exercises parsing/broadcast plumbing, not real network acceptance.
            tx.addInput(Sha256Hash.wrap(new byte[32]), 0, ScriptBuilder.createEmpty());
            tx.addOutput(Coin.valueOf(1_000L), address);
            String rawTxHex = toHex(tx.bitcoinSerialize());

            Envelope e = Envelope.documentFactory();
            e.addRoute(BitcoinService.class, BitcoinClient.OPERATION_BROADCAST_TRANSACTION);
            e.setHeader(BitcoinClient.HEADER_RAW_TX_HEX, rawTxHex);
            service.handleDocument(e);

            assertEquals(tx.getTxId().toString(), e.getHeader(BitcoinClient.HEADER_TXID));
            assertTrue(Envelope.getErrorMessages(e).isEmpty());
        } finally {
            service.gracefulShutdown();
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
