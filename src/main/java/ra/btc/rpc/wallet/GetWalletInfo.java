package ra.btc.rpc.wallet;

import ra.btc.Wallet;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetWalletInfo extends RPCRequest {

    public static final String NAME = "getwalletinfo";

    public Wallet wallet;

    public GetWalletInfo() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        // Response
        wallet = new Wallet();
        wallet.fromMap(m);
    }

}
