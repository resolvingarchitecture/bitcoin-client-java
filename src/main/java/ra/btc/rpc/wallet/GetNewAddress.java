package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

public class GetNewAddress extends RPCRequest {
    public static final String NAME = "getnewaddress";

    public GetNewAddress() {
        super(NAME);
    }
}
