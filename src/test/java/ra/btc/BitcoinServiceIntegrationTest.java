package ra.btc;

import org.junit.Test;
import ra.common.Client;
import ra.common.Envelope;
import ra.common.messaging.MessageProducer;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;

import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Exercises {@link BitcoinService} end-to-end against bitcoinj's RegTest network (no local
 * "bitcoind -regtest" instance required - the peer group's connection attempts happen
 * asynchronously and failing to connect does not block reaching RUNNING/SHUTDOWN).
 */
public class BitcoinServiceIntegrationTest {

    @Test
    public void startsAndShutsDownCleanly() {
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

        assertTrue("BitcoinService failed to gracefully shut down", service.gracefulShutdown());
        assertEquals(ServiceStatus.GRACEFULLY_SHUTDOWN, service.getServiceStatus());
    }
}
