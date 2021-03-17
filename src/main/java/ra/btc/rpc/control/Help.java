package ra.btc.rpc.control;

import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

public class Help extends RPCRequest {
    public static final String NAME = "help";

    // Request
    public String command = "all"; // The command to get help on; optional, default=all commands
    // Response
    public String txt; // The help text

    public Help() {
        super(NAME);
    }

    public Help(String command) {
        super(NAME);
        this.command = command;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(command);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            txt = (String)m.get("text");
        }
    }
}
