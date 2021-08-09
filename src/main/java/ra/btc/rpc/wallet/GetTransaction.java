package ra.btc.rpc.wallet;

import ra.btc.Transaction;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Get detailed information about an in-wallet transaction.
 */
public class GetTransaction extends RPCRequest {
    public static final String NAME = "gettransaction";

    // Request
    public String txid; // The transaction id
    public Boolean includeWatchOnly = false;
    public Boolean verbose = false; // Whether to include a decoded field containing the decoded transaction (equivalent to RPC decoderawtransaction)

    // Response
    public String data;
    public Transaction tx; // If verbose is true

    public GetTransaction() {super(NAME);}

    public GetTransaction(String txid) {
        super(NAME);
        this.txid = txid;
    }

    public GetTransaction(String txid, Boolean includeWatchOnly) {
        super(NAME);
        this.txid = txid;
        this.includeWatchOnly = includeWatchOnly;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(txid);
        params.add(includeWatchOnly);
        params.add(verbose);
        Map<String,Object> m = super.toMap();
        if(tx!=null) {
            m.put("tx",tx.toMap());
        }
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            tx = new Transaction();
            tx.fromMap(m);
        }
    }
}
