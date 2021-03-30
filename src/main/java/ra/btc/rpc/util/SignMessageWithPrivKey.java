package ra.btc.rpc.util;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Sign a message with the private key of an address.
 */
public class SignMessageWithPrivKey extends RPCRequest {
    public static final String NAME = "signmessagewithprivkey";

    // Request
    public final String privKey; // The private key to sign the message with.
    public final String message; // The message to create a signature of.

    // Response
    public String signature; // The signature of the message encoded in base 64

    public SignMessageWithPrivKey(String privKey, String message) {
        super(NAME);
        this.privKey = privKey;
        this.message = message;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(privKey);
        params.add(message);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            signature = (String)m.get("signature");
        }
    }
}
