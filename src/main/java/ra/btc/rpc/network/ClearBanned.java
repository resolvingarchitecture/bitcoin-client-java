package ra.btc.rpc.network;

import ra.btc.rpc.RPCRequest;

/**
 * Clear all banned IPs.
 */
public class ClearBanned extends RPCRequest {

    public static final String NAME = "clearbanned";

    public ClearBanned() {super(NAME);}

}
