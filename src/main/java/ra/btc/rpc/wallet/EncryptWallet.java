package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class EncryptWallet extends RPCRequest {

    public static final String NAME = "encryptwallet";

    public String walletName = "Default";

    // Request
    public String passphrase;

    public EncryptWallet() {
        super(NAME);
        this.passphrase = "1234"; // Only use for testing
        path += "/wallet/"+walletName;
    }

    public EncryptWallet(String passphrase) {
        super(NAME);
        this.passphrase = passphrase;
        path += "/wallet/"+walletName;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        params.add(passphrase);
        return super.toMap();
    }
}
