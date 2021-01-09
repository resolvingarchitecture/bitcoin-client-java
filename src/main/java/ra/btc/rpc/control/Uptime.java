package ra.btc.rpc.control;

import ra.btc.rpc.RPCRequest;

public class Uptime extends RPCRequest {

    public static final String NAME = "uptime";

    // Response
    public Long uptimeSec;

    public Uptime() {
        super(NAME);
    }
}
