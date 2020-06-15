package ra.btc.packet;

/**
 * Sends a request to a node asking for information about known active peers
 * to help with finding potential nodes in the network.
 *
 * The response to receiving this message is to transmit one or more {@link AddrPacket}
 * with one or more peers from a database of known active peers.
 *
 * The typical presumption is that a node is likely to be active if it has
 * been sending a packet within the last three hours.
 *
 */
public class GetAddrPacket extends BitcoinPacket {
}
