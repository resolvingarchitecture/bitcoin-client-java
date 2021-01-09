package ra.btc.rpc.control;

import ra.btc.rpc.RPCCommand;

public class Uptime extends RPCCommand {

    public static final String NAME = "uptime";

    // Response
    public Long uptimeSec;

    public Uptime() {
        super(NAME);
    }
}
