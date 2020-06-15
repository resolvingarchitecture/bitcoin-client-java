package ra.btc.packet;

/**
 * A message containing a serialized BlockTransactionsRequest message and pchCommand == "getblocktxn".
 *
 * Upon receipt of a properly-formatted {@link GetBlockTxnPacket}, nodes which recently provided
 * the sender of such a message a {@link CmpctBlockPacket} for the block hash identified in this
 * message MUST respond with an appropriate {@link BlockTxnPacket}.
 *
 * Such a {@link BlockTxnPacket} MUST contain exactly and only each transaction which is present
 * in the appropriate {@link io.onemfive.monetary.btc.blockchain.Block} at the index specified in
 * the {@link GetBlockTxnPacket} indexes list, in the order requested.
 *
 * This message is only supported by protocol version >= 70014
 *
 */
public class GetBlockTxnPacket extends BitcoinPacket {
}
