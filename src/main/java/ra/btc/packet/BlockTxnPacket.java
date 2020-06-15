package ra.btc.packet;

/**
 * A message containing a serialized BlockTransactions message and pchCommand == "blocktxn".
 *
 * Upon receipt of a properly-formatted requested {@link BlockTxnPacket}, nodes SHOULD attempt
 * to reconstruct the full block by:
 *
 *      Taking the prefilledtxn transactions from the original cmpctblock and placing them in the marked positions.
 *
 *      For each short transaction ID from the original cmpctblock, in order, find the corresponding transaction
 *      either from the blocktxn message or from other sources and place it in the first available position
 *      in the block.
 *
 *      Once the block has been reconstructed, it shall be processed as normal, keeping in mind that short
 *      transaction IDs are expected to occasionally collide, and that nodes MUST NOT be penalized for such
 *      collisions, wherever they appear.
 *
 */
public class BlockTxnPacket extends BitcoinPacket {
}
