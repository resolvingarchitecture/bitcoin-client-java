package ra.btc.rpc.control;

import ra.btc.Memory;
import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

/**
 * Returns details of the RPC server.
 */
public class GetRPCInfo extends RPCRequest {
    public static final String NAME = "getrpcinfo";

    // Response
    public Map<String,Long> runningTimes; // RPC Command Name, running time in microseconds

    public GetRPCInfo() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            List<Map<String, Object>> cmds = (List<Map<String, Object>>) m.get("active_commands");
            for(Map<String,Object> cmd : cmds) {
                runningTimes.put((String)cmd.get("method"), (Long)cmd.get("duraction"));
            }
        }
    }
}
