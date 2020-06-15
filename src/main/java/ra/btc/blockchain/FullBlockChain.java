package ra.btc.blockchain;

/**
 * A FullBlockChain works in conjunction with a {@link io.onemfive.monetary.btc.blockstore.FullBlockStore} to verify all the rules of the
 * Bitcoin system, with the downside being a very large cost in system resources. Fully verifying means all unspent
 * transaction outputs are stored.
 *
 */
public class FullBlockChain extends BlockChain {
}
