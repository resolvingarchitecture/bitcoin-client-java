package ra.btc;

import java.util.ArrayList;
import java.util.List;

public class BlockStats {
    // Request
    public String hashOrHeight;  // Required
    public List<String> stats; // Optional; default=all
    // Response
    public Double avgFee;
    public Integer avgFeeRate;
    public Integer avgTxSize;
    public String blockhash;
    public List<Double> feeRatePercentiles = new ArrayList<>();
    public Integer height;
    public Integer ins;
    public Integer maxFee;
    public Integer maxFeeRate;
    public Integer maxTxSize;
    public Integer medianFee;
    public Integer medianTime;
    public Integer medianTxSize;
    public Integer minFee;
    public Integer minFeeRate;
    public Integer minTxSize;
    public Integer outs;
    public Integer subsidy;
    public Integer swTotalSize;
    public Double swTotalWeight;
    public Integer swTxs;
    public Integer time;
    public Integer totalOut;
    public Integer totalSize;
    public Double totalWeight;
    public Integer totalFee;
    public Integer txs;
    public Integer utxoIncrease;
    public Integer utxoSizeInc;
}
