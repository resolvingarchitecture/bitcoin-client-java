package ra.btc.rpc.tx;

import ra.btc.*;
import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Return the raw transaction data.
 *
 * By default this function only works for mempool transactions. When called with a blockhash argument, getrawtransaction will return the transaction if the specified block is available and the transaction is found in that block. When called without a blockhash argument, getrawtransaction will return the transaction if it is in the mempool, or if -txindex is enabled and the transaction is in a block in the blockchain.
 *
 * Hint: Use gettransaction for wallet transactions.
 *
 * If verbose is ‘true’, returns an Object with information about ‘txid’.
 *
 * If verbose is ‘false’ or omitted, returns a string that is serialized, hex-encoded data for ‘txid’.
 */
public class GetRawTransaction extends RPCRequest {
    public static final String NAME = "getrawtransaction";

    // Request
    public String txid; // The transaction id
    public Boolean verbose = false; // If false, return a string, otherwise return a json object
    public String blockhash;

    // Response
    public String data; // If verbose is false or not set
    public Transaction tx; // If verbose is true

    public GetRawTransaction() {super(NAME);}

    public GetRawTransaction(String txid) {
        super(NAME);
        this.txid = txid;
    }

    public GetRawTransaction(String txid, Boolean verbose) {
        super(NAME);
        this.txid = txid;
        this.verbose = verbose;
    }

    public GetRawTransaction(String txid, Boolean verbose, String blockhash) {
        super(NAME);
        this.txid = txid;
        this.verbose = verbose;
        this.blockhash = blockhash;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(txid);
        params.add(verbose);
        if(blockhash!=null)
            params.add(blockhash);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            tx = new Transaction();
            tx.fromMap(m);
        }
    }
}
