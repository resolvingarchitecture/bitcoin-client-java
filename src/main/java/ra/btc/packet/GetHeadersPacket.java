package ra.btc.packet;

/**
 * Return a headers packet containing the headers of blocks starting right after the last
 * known hash in the block locator object, up to hash_stop or 2000 blocks, whichever comes first.
 *
 * To receive the next block headers, one needs to issue {@link GetHeadersPacket} again with a
 * new block locator object.
 *
 * Keep in mind that some clients may provide headers of blocks which are invalid if the
 * block locator object contains a hash on the invalid branch.
 *
 * For the block locator object in this packet, the same rules apply as for the {@link GetBlocksPacket}.
 *
 */
public class GetHeadersPacket extends BitcoinPacket {
}
