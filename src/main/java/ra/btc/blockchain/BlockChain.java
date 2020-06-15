package ra.btc.blockchain;


import ra.btc.blockstore.BlockStore;
import ra.btc.config.BitcoinConfig;

import java.util.logging.Logger;

/**
 * Contains a series of {@link Block} instances chained together by verifying the rules
 * defined in the {@link BitcoinConfig}.
 *
 */
public abstract class BlockChain {

    private static Logger LOG = Logger.getLogger(BlockChain.class.getName());

    protected BitcoinConfig config;

    protected BlockStore blockStore;

    /**
     * The end of the chain.
     * It can change if a new set of blocks is received that results in a chain of greater work.
     * When that happens, a reorganization is triggered potentially invalidating transactions in the wallet
     * if accepted as the new head.
     */
    protected StoredBlock chainHead;


}
