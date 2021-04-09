package ra.btc.rpc.util;

import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

/**
 * Estimates the approximate fee per kilobyte needed for a transaction to begin confirmation within
 * conf_target blocks if possible and return the number of blocks for which the estimate is valid.
 * Uses virtual transaction size as defined in BIP 141 (witness data is discounted).
 */
public class EstimateSmartFee extends RPCRequest {
    public static final String NAME = "estimatesmartfee";

    public static final String ESTIMATE_MODE_UNSET = "UNSET";
    public static final String ESTIMATE_MODE_ECONOMICAL = "ECONOMICAL";
    public static final String ESTIMATE_MODE_CONSERVATIVE = "CONSERVATIVE";

    // Request
    public Integer confTarget; // Confirmation target in blocks (1 - 1008)
    public String estimateMode = ESTIMATE_MODE_CONSERVATIVE; // The fee estimate mode. Whether to return a more conservative estimate which
                                // also satisfies a longer history. A conservative estimate potentially returns
                                // a higher feerate and is more likely to be sufficient for the desired target,
                                // but is not as responsive to short term drops in the prevailing fee market.
                                // Must be one of: “UNSET” “ECONOMICAL” “CONSERVATIVE”

    // Response
    public Double feeRate; // estimate fee rate in BTC/kB
    public List<String> errors; // Errors encountered during processing
    public Integer blocks; // block number where estimate was found

    public EstimateSmartFee() {}

    public EstimateSmartFee(Integer confirmationTarget) {
        confTarget = confirmationTarget;
    }

    public EstimateSmartFee(Integer confirmationTarget, String estimateMode) {
        confTarget = confirmationTarget;
        this.estimateMode = estimateMode;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(confTarget);
        params.add(estimateMode);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            feeRate = (Double)m.get("feerate");
            errors = (List<String>)m.get("errors");
            blocks = (Integer)m.get("blocks");
        }
    }
}
