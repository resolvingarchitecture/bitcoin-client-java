package ra.btc.rpc.wallet;

import ra.btc.BTCWallet;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetWalletInfo extends RPCRequest {

    public static final String NAME = "getwalletinfo";

    public String walletName = "Default";

    public BTCWallet wallet;

    public GetWalletInfo() {
        super(NAME);
        path += "/wallet/"+walletName;
    }

    public GetWalletInfo(String walletName) {
        super(NAME);
        this.walletName = walletName;
        path += "/wallet/"+walletName;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        // Response
        wallet = new BTCWallet();
        wallet.fromMap(m);
        this.path = this.path + "/wallet/" + this.walletName;
    }

}
