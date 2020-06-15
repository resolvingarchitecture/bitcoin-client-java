package ra.btc.packet;

/**
 * Describes a bitcoin transaction, in reply to {@link GetDataPacket}.
 *
 * When a bloom filter is applied, tx objects are sent automatically for matching
 * transactions following the {@link MerkleBlockPacket}.
 *
 */
public class TxPacket extends BitcoinPacket {
}
