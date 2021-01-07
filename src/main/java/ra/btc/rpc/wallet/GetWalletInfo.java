package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCCommand;

public class GetWalletInfo extends RPCCommand {

    public static final String NAME = "getwalletinfo";

    public GetWalletInfo() {
        super(NAME);
    }

}
