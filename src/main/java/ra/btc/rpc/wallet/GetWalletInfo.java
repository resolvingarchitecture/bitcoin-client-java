package ra.btc.rpc.wallet;

import ra.btc.BTCWallet;
import ra.btc.rpc.RPCRequest;

import java.util.HashMap;
import java.util.Map;

public class GetWalletInfo extends RPCRequest {

    public static final String NAME = "getwalletinfo";

    public BTCWallet wallet = new BTCWallet();

    public GetWalletInfo() {
        super(NAME);
        path += "/wallet/Default";
    }

    public GetWalletInfo(String walletName) {
        super(NAME);
        this.wallet.setName(walletName);
        path += "/wallet/"+walletName;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        // Response
        wallet.fromMap(m);
        if(wallet.getName()!=null) {
            path = path.substring(0,path.lastIndexOf("/")+1);
            path += wallet.getName();
        }
    }

}
