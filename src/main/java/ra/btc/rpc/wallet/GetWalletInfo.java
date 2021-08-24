package ra.btc.rpc.wallet;

import ra.btc.BTCWallet;
import ra.btc.rpc.RPCRequest;
import ra.common.JSONParser;

import java.util.HashMap;
import java.util.Map;

public class GetWalletInfo extends RPCRequest {

    public static final String NAME = "getwalletinfo";

    public BTCWallet wallet = new BTCWallet();

    public GetWalletInfo() {
        super(NAME);
        path += "/wallet/";
    }

    public GetWalletInfo(String walletName) {
        super(NAME);
        if(walletName!=null && !walletName.isEmpty() && !"Default".equals(walletName)) {
            this.wallet.setName(walletName);
            path += "/wallet/" + walletName;
        } else {
            path += "/wallet/";
        }
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        super.fromMap(m);
        // Response
        if(m.get("result")!=null) {
            wallet.fromMap((Map<String, Object>) m.get("result"));
        }
        if(m.get("path")!=null)
            path = (String)m.get("path");
        else if(wallet.getName()!=null) {
            path = path.substring(0,path.lastIndexOf("/")+1);
            path += wallet.getName();
        }
    }

}
