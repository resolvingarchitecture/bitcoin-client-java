package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class WalletPassphraseChange extends RPCRequest {

    public static final String NAME = "walletpassphrasechange";

    public String walletName = "Default";

    // Request
    public String oldPassphrase;
    public String newPassphrase;

    public WalletPassphraseChange() {
        super(NAME);
        path += "/wallet/"+walletName;
    }

    public WalletPassphraseChange(String oldPassphrase, String newPassphrase) {
        super(NAME);
        this.oldPassphrase = oldPassphrase;
        this.newPassphrase = newPassphrase;
        path += "/wallet/"+walletName;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        params.add(oldPassphrase);
        params.add(newPassphrase);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {}
}
