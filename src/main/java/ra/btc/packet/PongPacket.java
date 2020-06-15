package ra.btc.packet;

/**
 * Sent in response to a {@link PingPacket}.
 *
 * In modern protocol versions, a {@link PongPacket} is generated using a nonce included in the {@link PingPacket}.
 *
 */
public class PongPacket extends BitcoinPacket {
}
