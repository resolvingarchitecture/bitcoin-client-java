package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class WalletPassphraseChange extends RPCRequest {

    public static final String NAME = "walletpassphrasechange";

    // Request
    public String oldPassphrase;
    public String newPassphrase;

    public WalletPassphraseChange() {}

    public WalletPassphraseChange(String oldPassphrase, String newPassphrase) {
        this.oldPassphrase = oldPassphrase;
        this.newPassphrase = newPassphrase;
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
