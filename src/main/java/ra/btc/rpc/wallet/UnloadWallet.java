package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class UnloadWallet extends RPCRequest {

    public static final String NAME = "loadwallet";

    // Request
    private String walletName = "Default";
    private Boolean loadOnStartup = null;

    // Response
    private String warning;

    public UnloadWallet() {
        super(NAME);
    }

    public UnloadWallet(String walletName) {
        super(NAME);
        this.walletName = walletName;
    }

    public UnloadWallet(String walletName, Boolean loadOnStartup) {
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
        warning = (String)m.get("warning");
    }
}
