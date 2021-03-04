package ra.btc;

/**
 * Active state of the TX memory pool.
 */
public class MempoolInfo {
    public Integer size;
    public Integer bytes;
    public Integer usage;
    public Integer maxMempool;
    public Integer mempoolMinFee;
    public Integer minRelayTxFee;
}
