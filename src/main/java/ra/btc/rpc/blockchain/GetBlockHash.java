package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetBlockHash extends RPCRequest {

    public static final String NAME = "getblockhash";

    // Response
    public String hash;

    public GetBlockHash() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        hash = (String)m.get("hash");
    }
}
