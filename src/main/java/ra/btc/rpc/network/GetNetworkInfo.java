package ra.btc.rpc.network;

import ra.btc.rpc.RPCRequest;

public class GetNetworkInfo extends RPCRequest {

    public static final String NAME = "getnetworkinfo";

    public GetNetworkInfo() {
        super(NAME);
    }
}
