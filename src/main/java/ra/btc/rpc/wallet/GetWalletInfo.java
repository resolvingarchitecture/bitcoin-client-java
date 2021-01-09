package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

public class GetWalletInfo extends RPCRequest {

    public static final String NAME = "getwalletinfo";

    public GetWalletInfo() {
        super(NAME);
    }

}
