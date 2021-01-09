package ra.btc.rpc.mining;

import ra.btc.rpc.RPCCommand;

public class GetNetworkHashPS extends RPCCommand {

    public static final String NAME = "getnetworkhashps";

    public GetNetworkHashPS() {
        super(NAME);
    }
}
