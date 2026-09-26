package ra.btc.bitcoinj;

import org.bitcoinj.net.discovery.PeerDiscoveryException;
import org.junit.Test;
import ra.btc.BitcoinClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * {@link BitcoinJClient.ProxiedDnsSeedDiscovery} against a fake {@link
 * BitcoinClient.ProxiedHostResolver} - no real Tor daemon or network needed. Confirms it never
 * calls anything but the supplied resolver (no local DNS involved) and matches {@code
 * MultiplexingDiscovery}'s own per-seed-failure tolerance.
 */
public class ProxiedDnsSeedDiscoveryTest {

    @Test
    public void resolvesEverySeedThroughTheSuppliedResolver() throws Exception {
        Set<String> asked = ConcurrentHashMap.newKeySet();
        BitcoinClient.ProxiedHostResolver resolver = (hostname, timeout) -> {
            asked.add(hostname);
            return InetAddress.getByAddress(hostname, new byte[]{10, 0, 0, 1});
        };
        BitcoinJClient.ProxiedDnsSeedDiscovery discovery =
                new BitcoinJClient.ProxiedDnsSeedDiscovery(new String[]{"seedA.example", "seedB.example"}, 8333, resolver);

        List<InetSocketAddress> peers = discovery.getPeers(0, Duration.ofSeconds(5));

        assertEquals(2, peers.size());
        assertEquals(Set.of("seedA.example", "seedB.example"), asked);
        for (InetSocketAddress peer : peers) {
            assertEquals(8333, peer.getPort());
        }
    }

    @Test
    public void toleratesSomeSeedsFailingAsLongAsOneSucceeds() throws Exception {
        BitcoinClient.ProxiedHostResolver resolver = (hostname, timeout) -> {
            if (hostname.equals("bad.example")) throw new IOException("simulated resolve failure");
            return InetAddress.getByAddress(hostname, new byte[]{10, 0, 0, 2});
        };
        BitcoinJClient.ProxiedDnsSeedDiscovery discovery =
                new BitcoinJClient.ProxiedDnsSeedDiscovery(new String[]{"bad.example", "good.example"}, 8333, resolver);

        List<InetSocketAddress> peers = discovery.getPeers(0, Duration.ofSeconds(5));

        assertEquals(1, peers.size());
        assertEquals("good.example", peers.get(0).getAddress().getHostName());
    }

    @Test
    public void throwsWhenEverySeedFails() throws Exception {
        BitcoinClient.ProxiedHostResolver resolver = (hostname, timeout) -> {
            throw new IOException("simulated resolve failure for " + hostname);
        };
        BitcoinJClient.ProxiedDnsSeedDiscovery discovery =
                new BitcoinJClient.ProxiedDnsSeedDiscovery(new String[]{"a.example", "b.example"}, 8333, resolver);

        try {
            discovery.getPeers(0, Duration.ofSeconds(5));
            fail("expected PeerDiscoveryException");
        } catch (PeerDiscoveryException expected) {
            // expected
        }
    }

    @Test
    public void throwsWhenNoSeedsConfigured() throws Exception {
        BitcoinJClient.ProxiedDnsSeedDiscovery discovery =
                new BitcoinJClient.ProxiedDnsSeedDiscovery(new String[0], 8333, (hostname, timeout) -> {
                    throw new AssertionError("should never be called with zero seeds");
                });

        try {
            discovery.getPeers(0, Duration.ofSeconds(5));
            fail("expected PeerDiscoveryException");
        } catch (PeerDiscoveryException expected) {
            // expected
        }
    }
}