package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;
import ra.common.currency.crypto.BTC;

import java.util.Map;

public class SendToAddress extends RPCRequest {

    public static final String NAME = "sendtoaddress";

    // Request
    public String receiverAddress;
    public Double amountInBTC;
    public String fromWalletName = "";

    public SendToAddress() {
        super(NAME);
        path += "/wallet/"+fromWalletName;
    }

    public SendToAddress(String fromWalletName, String receiverAddress, Double amountInBTC) {
        super(NAME);
        this.receiverAddress = receiverAddress;
        this.amountInBTC = amountInBTC;
        this.fromWalletName = fromWalletName;
        path += "/wallet/"+fromWalletName;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = super.toMap();
        // Request
        m.put("address", receiverAddress);
        m.put("amount", amountInBTC.doubleValue());
        m.put("fromWalletName", fromWalletName);
        params.add(receiverAddress);
        params.add(amountInBTC.doubleValue());
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("address")!=null) receiverAddress = (String)m.get("address");
        if(m.get("amount")!=null) amountInBTC =(double)m.get("amount");
        if(m.get("fromWalletName")!=null) fromWalletName = (String)m.get("fromWalletName");
    }
}
