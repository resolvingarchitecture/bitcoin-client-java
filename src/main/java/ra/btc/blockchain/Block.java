package ra.btc.blockchain;

import ra.common.Hash;

/**
 * A block of transactions
 *
 */
public class Block {

    /**
     * Max block size in bytes as a method to prevent DDoS attacks by creating a large enough valid block resulting
     * in the possibility of crashing a large number of nodes, potentially the whole network.
     */
    public static final int MAX_BLOCK_SIZE = 1000000; // 1mb
    /**
     * A "sigop" is a signature verification operation. Because they're expensive we also impose a separate limit on
     * the number in a block to prevent somebody mining a huge block that has way more sigops than normal, so is very
     * expensive/slow to verify.
     */
    public static final int MAX_BLOCK_SIGOPS = MAX_BLOCK_SIZE / 50;

    private long version;
    private Hash prevBlockHash;
    private Hash merkleRoot;
    private long time;
    private long difficultyTarget = 0x1d07fff8L;
    private long nonce;

    public Block(int version) {
        this.version = version;
        this.time = System.currentTimeMillis();
        this.prevBlockHash = new Hash("",Hash.Algorithm.SHA256);
    }
}
