package ra.btc.blockchain;

import ra.btc.network.BitcoinPeer;
import ra.btc.network.BitcoinPeerDiscovery;
import ra.btc.packet.RejectPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A broadcast of a single transaction to the Bitcoin network.
 * Success is determined by announcements by peers indicating their acceptance.
 * Failure is indicated by receiving an explicit rejection message from a peer
 * or not reaching success within a given time period.
 *
 */
public class TransactionBroadcast {

    private static final Logger log = Logger.getLogger(TransactionBroadcast.class.getName());

    private BitcoinPeerDiscovery peerDiscovery;
    private Transaction tx;
    // Peers that returned a rejection message regarding this broadcast.
    private Map<BitcoinPeer, RejectPacket> rejections = new HashMap<>();

}
