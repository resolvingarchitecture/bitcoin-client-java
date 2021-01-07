package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCCommand;

public class GetDifficulty extends RPCCommand {

    public static final String NAME = "getdifficulty";

    // Response
    public double difficulty;

    public GetDifficulty() {
        super(NAME);
    }

}
