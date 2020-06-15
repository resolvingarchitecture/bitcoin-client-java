package ra.btc.packet;

/**
 * Return an {@link InvPacket} containing the list of {@link io.onemfive.monetary.btc.blockchain.Block}
 * starting right after the last known hash in the block locator object, up to hash_stop or 500 blocks,
 * whichever comes first.
 *
 * The locator hashes are processed by a node in the order as they appear in the message.
 * If a block hash is found in the node's main chain, the list of its children is returned
 * back via the {@link InvPacket} and the remaining locators are ignored, no matter if the
 * requested limit was reached, or not.
 *
 * To receive the next blocks hashes, one needs to issue {@link GetBlocksPacket} again with
 * a new block locator object.
 *
 * Keep in mind that some clients may provide blocks which are invalid if the block locator
 * object contains a hash on the invalid branch.
 *
 * Note that it is allowed to send in fewer known hashes down to a minimum of just one hash.
 * However, the purpose of the block locator object is to detect a wrong branch in the
 * caller's main chain.
 *
 * If the peer detects that you are off the main chain, it will send in block hashes which are
 * earlier than your last known block.
 *
 * So if you just send in your last known hash and it is off the main chain, the peer
 * starts over at block #1.
 *
 */
public class GetBlocksPacket extends BitcoinPacket {
}
