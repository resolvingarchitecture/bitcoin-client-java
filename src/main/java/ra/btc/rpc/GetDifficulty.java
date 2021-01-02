package ra.btc.rpc;

public class GetDifficulty extends RPCCommand {
    public static final String NAME = "getdifficulty";
    public GetDifficulty() {
        super(NAME);
    }
}
