package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetNewAddress extends RPCRequest {

    public static final String NAME = "getnewaddress";

    private String walletName;

    // Request
    private String label;
    private String addressType;

    // Response
    private String address;

    public GetNewAddress() {
        super(NAME);
        this.walletName = "";
        this.label = "";
        this.addressType = AddressType.BECH32;
        path += "/wallet/";
    }

    public GetNewAddress(String walletName) {
        super(NAME);
        this.walletName = walletName;
        this.label = "";
        this.addressType = AddressType.BECH32;
        path += "/wallet/"+walletName;
    }

    public GetNewAddress(String walletName, String label, String addressType) {
        super(NAME);
        this.walletName = walletName;
        this.label = label;
        this.addressType = addressType;
        path += "/wallet/"+walletName;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
        path = "/wallet/"+walletName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        if(params.size()==0) {
            params.add(label);
            params.add(addressType);
        }
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        // Response
        super.fromMap(m);
        if(m.get("label")!=null) label = (String)m.get("label");
        if(m.get("addressType")!=null) addressType = (String)m.get("addressType");
        if(m.get("str")!=null) address = (String)m.get("str");
        if(m.get("result")!=null) address = (String)m.get("result");
    }
}
