package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetDifficulty extends RPCRequest {

    public static final String NAME = "getdifficulty";

    // Response
    public Double difficulty;

    public GetDifficulty() {}

    @Override
    public void fromMap(Map<String, Object> m) {
        difficulty = (Double) m.get("difficulty");
    }
}
