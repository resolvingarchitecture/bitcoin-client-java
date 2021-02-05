package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetBlockCount extends RPCRequest {

    public static final String NAME = "getblockcount";

    // Response
    public Integer count;

    public GetBlockCount() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        count = (Integer)m.get("n");
    }
}
