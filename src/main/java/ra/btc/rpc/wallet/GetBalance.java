package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Returns the total available balance.
 *
 * The available balance is what the wallet considers currently spendable,
 * and is thus affected by options which limit spendability such as -spendzeroconfchange.
 */
public class GetBalance extends RPCRequest {

    public static final String NAME = "getbalance";

    // Request
    public String dummy;
    public Integer minconf;
    public Boolean includeWatchOnly;

    // Response
    public Double amount;

    /**
     * The total amount in the wallet with 1 or more confirmations.
     */
    public GetBalance() {
        super(NAME);
    }

    /**
     * The total amount in the wallet at least minconf blocks confirmed, including/excluding watch-only addresses
     * @param dummy
     * @param minconf
     * @param includeWatchOnly
     */
    public GetBalance(String dummy, Integer minconf, Boolean includeWatchOnly) {
        super(NAME);
        this.dummy = dummy;
        this.minconf = minconf;
        this.includeWatchOnly = includeWatchOnly;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        if(dummy!=null) params.add(dummy);
        if(minconf!=null) params.add(minconf);
        if(includeWatchOnly!=null) params.add(includeWatchOnly);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        amount = (Double)m.get("amount");
    }
}
