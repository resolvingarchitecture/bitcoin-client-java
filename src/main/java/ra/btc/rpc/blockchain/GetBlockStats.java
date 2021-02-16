package ra.btc.rpc.blockchain;

import ra.btc.BlockStats;
import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

public class GetBlockStats extends RPCRequest {

    public static final String NAME = "getblockstats";

    public BlockStats blockStats = new BlockStats();

    public GetBlockStats(String hashOrHeight) {
        super(NAME);
        this.blockStats.hashOrHeight = hashOrHeight;
    }

    public GetBlockStats(String hashOrHeight, List<String> stats) {
        super(NAME);
        this.blockStats.hashOrHeight = hashOrHeight;
        this.blockStats.stats = stats;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(blockStats.hashOrHeight);
        if(blockStats.stats!=null)
            params.add(blockStats.stats);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("avgfee")!=null) blockStats.avgFee = (Double)m.get("avgfee");
        if(m.get("avgfeerate")!=null) blockStats.avgFeeRate = (Integer)m.get("avgfeerate");
        if(m.get("avgtxsize")!=null) blockStats.avgTxSize = (Integer)m.get("avgtxsize");
        if(m.get("blockhash")!=null) blockStats.blockhash = (String)m.get("blockhash");
        if(m.get("feerate_percentiles")!=null) blockStats.feeRatePercentiles = (List<Double>)m.get("feerate_percentiles");
        if(m.get("height")!=null) blockStats.height = (Integer)m.get("height");
        if(m.get("ins")!=null) blockStats.ins = (Integer)m.get("ins");
        if(m.get("maxfee")!=null) blockStats.maxFee = (Integer)m.get("maxfee");
        if(m.get("maxfeerate")!=null) blockStats.maxFeeRate = (Integer)m.get("maxfeerate");
        if(m.get("maxtxsize")!=null) blockStats.maxTxSize = (Integer)m.get("maxtxsize");
        if(m.get("medianfee")!=null) blockStats.medianFee = (Integer)m.get("medianfee");
        if(m.get("mediantime")!=null) blockStats.medianTime = (Integer)m.get("mediantime");
        if(m.get("mediantxsize")!=null) blockStats.medianTxSize = (Integer)m.get("mediantxsize");
        if(m.get("minfee")!=null) blockStats.minFee = (Integer)m.get("minfee");
        if(m.get("minfeerate")!=null) blockStats.minFeeRate = (Integer)m.get("minfeerate");
        if(m.get("mintxsize")!=null) blockStats.minTxSize = (Integer)m.get("mintxsize");
        if(m.get("outs")!=null) blockStats.outs = (Integer)m.get("outs");
        if(m.get("subsidy")!=null) blockStats.subsidy = (Integer)m.get("subsidy");
        if(m.get("swtotal_size")!=null) blockStats.swTotalSize = (Integer)m.get("swtotal_size");
        if(m.get("swtotal_weight")!=null) blockStats.swTotalWeight = (Double)m.get("swtotal_weight");
        if(m.get("swtxs")!=null) blockStats.swTxs = (Integer)m.get("swtxs");
        if(m.get("time")!=null) blockStats.time = (Integer)m.get("time");
        if(m.get("total_out")!=null) blockStats.totalOut = (Integer)m.get("total_out");
        if(m.get("total_size")!=null) blockStats.totalSize = (Integer)m.get("total_size");
        if(m.get("total_weight")!=null) blockStats.totalWeight = (Double)m.get("total_weight");
        if(m.get("totalfee")!=null) blockStats.totalFee = (Integer)m.get("totalfee");
        if(m.get("txs")!=null) blockStats.txs = (Integer)m.get("txs");
        if(m.get("utxo_increase")!=null) blockStats.utxoIncrease = (Integer)m.get("utxo_increase");
        if(m.get("utxo_size_inc")!=null) blockStats.utxoSizeInc = (Integer)m.get("utxo_size_inc");
    }
}
