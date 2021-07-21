package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Returns the total available balance.
 *
 * The available balance is what the wallet considers currently spendable,
 * and is thus affected by options which limit spendability such as -spendzeroconfchange.
 *
 * Make sure walletName is set to currently loaded wallet.
 */
public class GetBalance extends RPCRequest {

    public static final String NAME = "getbalance";

    public String walletName = "";

    // Request
    public String dummy = "*";
    public Integer minconf = 0;
    public Boolean includeWatchOnly = true;
    public Boolean avoidReuse = false;

    // Response
    public Double amount;

    /**
     * The total amount in the wallet with 1 or more confirmations.
     */
    public GetBalance() {
        super(NAME);
        path += "/wallet/"+walletName;
    }

    /**
     * The total amount in the wallet at least minconf blocks confirmed, including/excluding watch-only addresses
     * @param dummy
     * @param minconf
     * @param includeWatchOnly
     * @param avoidReuse
     */
    public GetBalance(String dummy, Integer minconf, Boolean includeWatchOnly, Boolean avoidReuse) {
        super(NAME);
        path += "/wallet/"+walletName;
        this.dummy = dummy;
        this.minconf = minconf;
        this.includeWatchOnly = includeWatchOnly;
        this.avoidReuse = avoidReuse;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        params.add(dummy);
        params.add(minconf);
        params.add(includeWatchOnly);
        params.add(avoidReuse);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        super.fromMap(m);
        if(m.get("amount")!=null) amount = (Double)m.get("amount");
    }
}
