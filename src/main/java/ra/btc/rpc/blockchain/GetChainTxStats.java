package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetChainTxStats extends RPCRequest {
    public static final String NAME = "getchaintxstats";

    public BlockchainTxStats blockchainTxStats = new BlockchainTxStats();

    public GetChainTxStats() {super(NAME);}

    public GetChainTxStats(Integer nBlocks, String blockhash) {
        super(NAME);
        this.blockchainTxStats.nBlocks = nBlocks;
        this.blockchainTxStats.blockhash = blockhash;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(blockchainTxStats.nBlocks);
        params.add(blockchainTxStats.blockhash);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        blockchainTxStats.time = (Integer)m.get("avgfee");
        blockchainTxStats.txCount = (Integer)m.get("avgfeerate");
        blockchainTxStats.windowFinalBlockHash = (String)m.get("avgtxsize");
        blockchainTxStats.windowBlockCount = (Integer)m.get("blockhash");
        blockchainTxStats.windowTxCount = (Integer)m.get("feerate_percentiles");
        blockchainTxStats.windowInterval = (Integer)m.get("height");
        blockchainTxStats.txRate = (Integer)m.get("ins");
    }
}
