package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListWallets extends RPCRequest {
    public static final String NAME = "listwallets";

    // Response
    public List<String> wallets = new ArrayList<>();

    public ListWallets() {super(NAME);}

    @Override
    public void fromMap(Map<String, Object> m) {
        super.fromMap(m);
        // Response
        if(m.get("result")!=null) wallets = (List<String>)m.get("result");
    }
}
