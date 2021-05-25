package ra.btc.rpc.wallet;

import ra.btc.AddressType;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetNewAddress extends RPCRequest {

    public static final String NAME = "getnewaddress";

    // Request
    public String label;
    public AddressType addressType;

    // Response
    public String address;

    public GetNewAddress() {super(NAME);}

    public GetNewAddress(String label) {
        super(NAME);
        this.label = label;
    }

    public GetNewAddress(AddressType addressType) {
        super(NAME);
        this.addressType = addressType;
    }

    public GetNewAddress(String label, AddressType addressType) {
        super(NAME);
        this.label = label;
        this.addressType = addressType;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        if(label!=null) params.add(label);
        if(addressType!=null) {
            params.add(addressType);
        }
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        // Response
        address = (String)m.get("address");
    }
}
