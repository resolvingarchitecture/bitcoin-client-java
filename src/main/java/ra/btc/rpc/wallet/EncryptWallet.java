package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class EncryptWallet extends RPCRequest {

    public static final String NAME = "encryptwallet";

    // Request
    public String passphrase;

    public EncryptWallet(String passphrase) {
        super(NAME);
        this.passphrase = passphrase;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        if(passphrase!=null) params.add(passphrase);
        return super.toMap();
    }
}
