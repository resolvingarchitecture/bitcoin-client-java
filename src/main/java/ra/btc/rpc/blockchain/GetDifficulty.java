package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCCommand;

import java.util.Map;

public class GetDifficulty extends RPCCommand {

    public static final String NAME = "getdifficulty";

    // Response
    public double difficulty;

    public GetDifficulty() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        difficulty = (Double)m.get("difficulty");
    }
}
