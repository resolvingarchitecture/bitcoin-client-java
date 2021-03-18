package ra.btc.rpc.control;

import ra.btc.rpc.RPCRequest;

/**
 * Stop Bitcoin server.
 */
public class Stop extends RPCRequest {
    public static final String NAME = "stop";

    public Stop() {
        super(NAME);
    }

}
