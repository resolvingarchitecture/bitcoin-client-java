package ra.btc.rpc.util;

import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

/**
 * Creates a multi-signature address with n signature of m keys required.
 *
 * It returns a json object with the address and redeemScript.
 */
public class CreateMultiSig extends RPCRequest {
    public static final String NAME = "createmultisig";

    public static final String ADDRESS_TYPE_LEGACY = "legacy";
    public static final String ADDRESS_TYPE_SEGWIT = "p2sh-segwit";
    public static final String ADDRESS_TYPE_BECH32 = "bech32";

    // Request
    public Integer nRequired; // The number of required signatures out of the n keys.
    public List<String> keys; // hex-encoded public keys.
    public String addressType = ADDRESS_TYPE_LEGACY; // The address type to use. Options are “legacy”, “p2sh-segwit”, and “bech32”. optional; default = legacy

    // Response
    public String address; // the new multisig address.
    public String redeemScript; // string value of the hex-encoded redemption script.

    public CreateMultiSig() {}

    public CreateMultiSig(Integer nRequired, List<String> keys) {
        this.nRequired = nRequired;
        this.keys = keys;
    }

    public CreateMultiSig(Integer nRequired, List<String> keys, String addressType) {
        this.nRequired = nRequired;
        this.keys = keys;
        this.addressType = addressType;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(nRequired);
        params.add(keys);
        params.add(addressType);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            address = (String)m.get("address");
            redeemScript = (String)m.get("redeemScript");
        }
    }
}
