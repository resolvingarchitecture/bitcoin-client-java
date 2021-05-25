package ra.btc.rpc.wallet;

import ra.btc.BTCWallet;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetWalletInfo extends RPCRequest {

    public static final String NAME = "getwalletinfo";

    public BTCWallet wallet;

    public GetWalletInfo() {super(NAME);}

    @Override
    public void fromMap(Map<String, Object> m) {
        // Response
        wallet = new BTCWallet();
        wallet.fromMap(m);
    }

}
