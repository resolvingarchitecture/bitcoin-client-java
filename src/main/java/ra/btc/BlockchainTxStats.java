package ra.btc;

public class BlockchainTxStats {
    // Request
    public Integer nBlocks;
    public String blockhash;
    // Response
    public Integer time;
    public Integer txCount;
    public String windowFinalBlockHash;
    public Integer windowBlockCount;
    public Integer windowTxCount;
    public Integer windowInterval;
    public Integer txRate;
}
