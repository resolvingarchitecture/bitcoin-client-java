package ra.btc.packet;

/**
 * The value represents a minimal fee and is expressed in satoshis per kilobyte.
 *
 * The payload is always 8 bytes long and it encodes 64 bit integer value (LSB / little endian) of feerate.
 *
 * Upon receipt of a {@link FeeFilterPacket}, the node will be permitted, but not required, to
 * filter transaction invs for transactions that fall below the feerate provided in the
 * {@link FeeFilterPacket} interpreted as satoshis per kilobyte.
 *
 * The fee filter is additive with a bloom filter for transactions so if an SPV client were to load
 * a bloom filter and send a {@link FeeFilterPacket}, transactions would only be relayed if they passed both filters.
 *
 * Inv's generated from a mempool message are also subject to a fee filter if it exists.
 *
 * Feature discovery is enabled by checking protocol version >= 70013
 *
 */
public class FeeFilterPacket extends BitcoinPacket {
}
