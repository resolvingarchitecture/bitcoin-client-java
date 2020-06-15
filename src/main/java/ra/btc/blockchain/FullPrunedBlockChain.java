package ra.btc.blockchain;

/**
 * A FullPrunedBlockChain works in conjunction with a {@link io.onemfive.monetary.btc.blockstore.FullPrunedBlockStore}
 * to verify all the rules of the Bitcoin system, with the downside being a large cost in system resources.
 * Fully verifying means all unspent transaction outputs are stored. Once a transaction output is spent and
 * that spend is buried deep enough, the data related to it is deleted to ensure disk space usage doesn't grow
 * forever. For this reason a pruning node cannot serve the full block chain to other clients, but it nevertheless
 * provides the same security guarantees as Bitcoin Core does.
 *
 */
public class FullPrunedBlockChain extends BlockChain {
}
