package ra.btc.rpc.tx;

import ra.btc.*;
import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Return a JSON object representing the serialized, hex-encoded transaction.
 */
public class DecodeRawTransaction extends RPCRequest {
    public static final String NAME = "decoderawtransaction";

    // Request
    public String hexstring; // The transaction hex string
    public Boolean isWitness; // Whether the transaction hex is a serialized witness transaction. If iswitness is not present, heuristic tests will be used in decoding; optional, default=depends on heuristic tests

    // Response
    public Transaction tx; // the decoded transaction

    public DecodeRawTransaction(String hexstring) {
        super(NAME);
        this.hexstring = hexstring;
    }

    public DecodeRawTransaction(String hexstring, Boolean isWitness) {
        super(NAME);
        this.hexstring = hexstring;
        this.isWitness = isWitness;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(hexstring);
        if(isWitness==null) {
            // TODO: Determine isWitness
        }
        params.add(isWitness);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        tx = new Transaction();
        tx.fromMap(m);
    }
}
