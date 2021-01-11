package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

public class ListWallets extends RPCRequest {
    public static final String NAME = "listwallets";

    public ListWallets() {
        super(NAME);
    }
}
