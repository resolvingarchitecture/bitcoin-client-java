package ra.btc.rpc.tx;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Submits raw transaction (serialized, hex-encoded) to local node and network.
 *
 * Also see createrawtransaction and signrawtransactionwithkey calls.
 */
public class SendRawTransaction extends RPCRequest {
    public static final String NAME = "sendrawtransaction";

    // Request
    public String hexstring; // The hex string of the raw transaction; required
    public Boolean allowhighfees = false; // optional; default = false

    // Response
    public String hex; // The transaction hash in hex

    public SendRawTransaction() {super(NAME);}

    public SendRawTransaction(String hexstring) {
        super(NAME);
        this.hexstring = hexstring;
    }

    public SendRawTransaction(String hexstring, Boolean allowhighfees) {
        super(NAME);
        this.hexstring = hexstring;
        this.allowhighfees = allowhighfees;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(hexstring);
        params.add(allowhighfees);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            hex = (String)m.get("hex");
        }
    }
}
