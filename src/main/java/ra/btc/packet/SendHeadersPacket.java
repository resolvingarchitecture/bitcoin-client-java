package ra.btc.packet;

/**
 * Request for direct headers announcement.
 *
 * Upon receipt of this message, the node is be permitted, but not required,
 * to announce new blocks by headers command (instead of {@link InvPacket}).
 *
 * This packet is supported by the protocol version >= 70012 or Bitcoin Core version >= 0.12.0.
 *
 */
public class SendHeadersPacket extends BitcoinPacket {
}
