package ra.btc.rpc.network;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Returns the number of connections to other nodes.
 */
public class GetConnectionCount extends RPCRequest {

    public static final String NAME = "getconnectioncount";

    // Response
    public Integer numberConnections;

    public GetConnectionCount() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            numberConnections = (Integer)m.get("n");
        }
    }
}
