package ra.btc.rpc.network;

import ra.btc.NetworkInfo;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetNetworkInfo extends RPCRequest {

    public static final String NAME = "getnetworkinfo";

    // Response
    public NetworkInfo networkInfo;

    public GetNetworkInfo() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            networkInfo = new NetworkInfo();

        }
    }
}
