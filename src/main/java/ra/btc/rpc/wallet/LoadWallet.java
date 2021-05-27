package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class LoadWallet extends RPCRequest {

    public static final String NAME = "loadwallet";

    // Request
    public String walletName = "Default";
    public Boolean loadOnStartup = null;

    // Response
    public String name;
    public String warning;

    public LoadWallet() {
        super(NAME);
    }

    public LoadWallet(String walletName) {
        super(NAME);
        this.walletName = walletName;
    }

    public LoadWallet(String walletName, Boolean loadOnStartup) {
        super(NAME);
        this.walletName = walletName;
        this.loadOnStartup = loadOnStartup;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        params.add(walletName);
        params.add(loadOnStartup);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("walletname")!=null) walletName = (String)m.get("walletname");
        if(m.get("name")!=null) walletName = (String)m.get("name");
        if(m.get("warning")!=null) warning = (String)m.get("warning");
    }

}
