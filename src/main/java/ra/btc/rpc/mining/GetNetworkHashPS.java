package ra.btc.rpc.mining;

import ra.btc.rpc.RPCRequest;

public class GetNetworkHashPS extends RPCRequest {

    public static final String NAME = "getnetworkhashps";

    public GetNetworkHashPS() {
        super(NAME);
    }
}
