package ra.btc.rpc;

public class GetBlockchainInfo extends RPCCommand {
    public static final String NAME = "getblockchaininfo";
    public GetBlockchainInfo() {
        super(NAME);
    }
}
