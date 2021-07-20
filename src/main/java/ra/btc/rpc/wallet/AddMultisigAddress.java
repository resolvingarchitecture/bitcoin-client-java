package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

public class AddMultisigAddress extends RPCRequest {

    public static final String NAME = "addmultisigaddress";

    // Request
    public Integer nRequired; // Required
    public List<String> keys; // Required
    public String label; // Optional
    public String addressType; // Optional

    // Response
    public String multisigAddress;
    public String redeemScript;

    public AddMultisigAddress(){super(NAME);}

    /**
     *
     * @param nRequired The number of required signatures out of the n keys or addresses.
     * @param keys A json array of bitcoin addresses or hex-encoded public keys
     * @param addressType The address type to use. Options are “legacy”, “p2sh-segwit”, and “bech32”.
     */
    public AddMultisigAddress(Integer nRequired, List<String> keys, String addressType) {
        super(NAME);
        this.nRequired = nRequired;
        this.keys = keys;
        this.addressType = addressType;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        params.add(nRequired);
        params.add(keys.toArray());
        if(label!=null)
            params.add(label);
        if(addressType!=null)
            params.add(addressType);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        multisigAddress = (String)m.get("address");
        redeemScript = (String)m.get("redeemScript");
    }
}
