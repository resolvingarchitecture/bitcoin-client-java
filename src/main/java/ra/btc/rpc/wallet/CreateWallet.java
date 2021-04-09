package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Creates and loads a new wallet.
 */
public class CreateWallet extends RPCRequest {

    public static final String NAME = "createwallet";

    // Request
    public String walletName; // The name for the new wallet. Required. If this is a path, the wallet will be created at the path location.
    public Boolean disablePrivateKeys = false; // Disable the possibility of private keys (only watchonlys are possible in this mode).
    public Boolean blank = false; // Create a blank wallet. A blank wallet has no keys or HD seed. One can be set using sethdseed.

    // Response
    public String warning;

    public CreateWallet() {}

    public CreateWallet(String walletName) {
        this.walletName = walletName;
    }

    public CreateWallet(String walletName, Boolean disablePrivateKeys, Boolean blank) {
        this.walletName = walletName;
        this.disablePrivateKeys = disablePrivateKeys;
        this.blank = blank;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        params.add(walletName);
        if(disablePrivateKeys!=null) params.add(disablePrivateKeys);
        if(blank!=null) params.add(blank);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        warning = (String)m.get("warning");
    }
}
