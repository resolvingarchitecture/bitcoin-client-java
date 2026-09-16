package ra.btc;

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
        Properties p = new Properties();
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
}
