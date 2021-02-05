package ra.btc.rpc.wallet;

import ra.btc.Wallet;
import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListWallets extends RPCRequest {
    public static final String NAME = "listwallets";

    public List<Wallet> wallets = new ArrayList<>();

    public ListWallets() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        // Response
        Map<String,Object>[] walletsMap = (Map<String,Object>[])m.get(0);
        Wallet w;
        for(Map<String,Object> wm : walletsMap) {
            w = new Wallet();
            w.fromMap(wm);
            wallets.add(w);
        }
    }
}
