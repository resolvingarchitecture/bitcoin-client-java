package ra.btc.rpc.control;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Returns the total uptime of the server.
 */
public class Uptime extends RPCRequest {
    public static final String NAME = "uptime";

    // Response
    public Integer uptimeSeconds; // The number of seconds that the server has been running

    public Uptime() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            uptimeSeconds = (Integer)m.get("ttt");
        }
    }
}
