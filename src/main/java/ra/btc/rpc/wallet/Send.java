package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

public class Send  extends RPCRequest {

    public static final String NAME = "send";

    protected Send(String method) {
        super(NAME);
    }
}
