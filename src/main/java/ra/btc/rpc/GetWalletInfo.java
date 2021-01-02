package ra.btc.rpc;

public class GetWalletInfo extends RPCCommand {
    public static final String NAME = "getwalletinfo";
    public GetWalletInfo() {
        super(NAME);
    }
}
