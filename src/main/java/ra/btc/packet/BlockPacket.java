package ra.btc.packet;

/**
 * The block packet is sent in response to a {@link GetDataPacket} which requests
 * transaction information from a block hash.
 *
 * The SHA256 hash that identifies each {@link io.onemfive.monetary.btc.blockchain.Block}
 * (and which must have a run of 0 bits) is calculated from the first 6 fields of
 * this structure (version, prev_block, merkle_root, timestamp, bits, nonce, and
 * standard SHA256 padding, making two 64-byte chunks in all) and not from the
 * complete block.
 *
 * To calculate the hash, only two chunks need to be processed by the SHA256 algorithm.
 *
 * Since the nonce field is in the second chunk, the first chunk stays constant during
 * mining and therefore only the second chunk needs to be processed.
 *
 * However, a Bitcoin hash is the hash of the hash, so two SHA256 rounds are needed
 * for each mining iteration.
 *
 */
public class BlockPacket extends BitcoinPacket {
}
