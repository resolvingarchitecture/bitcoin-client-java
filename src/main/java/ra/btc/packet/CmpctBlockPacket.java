package ra.btc.packet;

/**
 * A message containing a serialized HeaderAndShortIDs message and pchCommand == "cmpctblock".
 *
 * Upon receipt of a {@link CmpctBlockPacket} after sending a {@link SendCmpctPacket},
 * nodes SHOULD calculate the short transaction ID for each unconfirmed transaction they
 * have available (ie in their mempool) and compare each to each short transaction ID in the cmpctblock message.
 *
 * After finding already-available transactions, nodes which do not have all transactions available to
 * reconstruct the full block SHOULD request the missing transactions using a {@link GetBlockTxnPacket}.
 *
 * A node MUST NOT send a {@link CmpctBlockPacket} unless they are able to respond to
 * a {@link GetBlockTxnPacket} which requests every transaction in the block.
 *
 * A node MUST NOT send a {@link CmpctBlockPacket} without having validated that the header properly
 * commits to each transaction in the block, and properly builds on top of the existing chain with
 * a valid proof-of-work.
 *
 * A node MAY send a {@link CmpctBlockPacket} before validating that each transaction in the block
 * validly spends existing UTXO set entries.
 *
 * This packet is only supported by protocol version >= 70014
 *
 */
public class CmpctBlockPacket extends BitcoinPacket {
}
