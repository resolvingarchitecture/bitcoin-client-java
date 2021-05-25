package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

public class SaveMempool extends RPCRequest {
    public static final String NAME = "savemempool";

    public SaveMempool() {super(NAME);}

}
