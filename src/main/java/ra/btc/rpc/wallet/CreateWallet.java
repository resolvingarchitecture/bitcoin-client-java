package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;
import ra.util.RandomUtil;

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
    public String passphrase; // Encrypt the wallet with this passphrase.
    public Boolean avoidReuse = false; // Keep track of coin reuse, and treat dirty and clean coins differently with privacy considerations in mind.
    public Boolean descriptors = false; // Create a native descriptor wallet. The wallet will use descriptors internally to handle address creation
    public Boolean loadOnStartup = null; // Save wallet name to persistent settings and load on startup. True to add wallet to startup list, false to remove, null to leave unchanged.

    // Response
    public String name;
    public String warning;

    public CreateWallet() {
        super(NAME);
        walletName = RandomUtil.randomAlphanumeric(8);
    }

    public CreateWallet(String walletName, String passphrase) {
        super(NAME);
        this.walletName = walletName;
        this.passphrase = passphrase;
    }

    public CreateWallet(String walletName, Boolean disablePrivateKeys, Boolean blank, String passphrase, Boolean avoidReuse, Boolean descriptors, Boolean loadOnStartup) {
        super(NAME);
        this.walletName = walletName;
        this.disablePrivateKeys = disablePrivateKeys;
        this.blank = blank;
        this.passphrase = passphrase;
        this.avoidReuse = avoidReuse;
        this.descriptors = descriptors;
        this.loadOnStartup = loadOnStartup;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        params.add(walletName);
        params.add(disablePrivateKeys);
        params.add(blank);
        params.add(passphrase);
        params.add(avoidReuse);
        params.add(descriptors);
        params.add(loadOnStartup);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        name = (String)m.get("name");
        warning = (String)m.get("warning");
    }
}
