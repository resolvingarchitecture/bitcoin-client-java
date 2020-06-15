package ra.btc.packet;

/**
 * Sends a request to a node asking for information about transactions it has verified
 * but which have not yet confirmed.
 *
 * The response to receiving this packet is an {@link InvPacket} containing the
 * transaction hashes for all the transactions in the node's mempool.
 *
 */
public class MemPoolPacket extends BitcoinPacket {
}
