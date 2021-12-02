package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;
import ra.common.currency.crypto.BTC;

import java.util.Map;

public class SendToAddress extends RPCRequest {

    public static final String NAME = "sendtoaddress";

    public String fromWalletName = "";
    // The bitcoin address to send to.
    public String receiverAddress;
    // The amount in BTC to send. eg 0.1
    public Double amountInBTC;
    // A comment used to store what the transaction is for. This is not part of the transaction, just kept in your wallet.
    public String comment = "";
    // A comment to store the name of the person or organization to which you’re sending the transaction.
    // This is not part of the transaction, just kept in your wallet
    public String commentTo = "";
    // The recipient will receive less bitcoins than you enter in the amount field.
    public Boolean subtractFeeFromAmount = false;
    // Allow this transaction to be replaced by a transaction with higher fees via BIP 125
    public Boolean replaceable = false;
    // Confirmation target in blocks
    public Integer confirmationTarget = 6;
    // The fee estimate mode, must be one of (case in-sensitive): “unset” “economical” “conservative”
    public String estimateMode = "unset";
    // (only available if avoid_reuse wallet flag is set) Avoid spending from dirty addresses;
    // addresses are considered dirty if they have previously been used in a transaction.
    public Boolean avoidReuse = true;

    public SendToAddress() {
        super(NAME);
        path += "/wallet/"+fromWalletName;
        determineConfirmationTarget();
    }

    public SendToAddress(String fromWalletName, String receiverAddress, Double amountInBTC) {
        super(NAME);
        this.receiverAddress = receiverAddress;
        this.amountInBTC = amountInBTC;
        this.fromWalletName = fromWalletName;
        path += "/wallet/"+fromWalletName;
        determineConfirmationTarget();
    }

    private void determineConfirmationTarget() {
        if(amountInBTC <= 0.001)
            confirmationTarget = 1;
        else if(amountInBTC <= 0.01)
            confirmationTarget = 3;
        else
            confirmationTarget = 6;
    }

    public void setWalletName(String walletName) {
        this.fromWalletName = walletName;
        if(!path.endsWith(walletName))
            path += walletName;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = super.toMap();
        // Request
        m.put("fromWalletName", fromWalletName);
        m.put("address", receiverAddress);
        m.put("amount", amountInBTC);
        m.put("comment", comment);
        m.put("commentTo", commentTo);
        m.put("subtractFeeFromAmount", subtractFeeFromAmount);
        m.put("replaceable", replaceable);
        m.put("confirmationTarget", confirmationTarget);
        m.put("estimateMode", estimateMode);
        m.put("avoidReuse", avoidReuse);
        params.add(receiverAddress);
        params.add(amountInBTC);
        params.add(comment);
        params.add(commentTo);
        params.add(subtractFeeFromAmount);
        params.add(replaceable);
        params.add(confirmationTarget);
        params.add(estimateMode);
        params.add(avoidReuse);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("fromWalletName")!=null) fromWalletName = (String)m.get("fromWalletName");
        if(m.get("address")!=null) receiverAddress = (String)m.get("address");
        if(m.get("amount")!=null) amountInBTC =(Double)m.get("amount");
        if(m.get("comment")!=null) comment = (String)m.get("comment");
        if(m.get("commentTo")!=null) commentTo = (String)m.get("commentTo");
        if(m.get("subtractFeeFromAmount")!=null) subtractFeeFromAmount = (Boolean)m.get("subtractFeeFromAmount");
        if(m.get("replaceable")!=null) replaceable = (Boolean)m.get("replaceable");
        if(m.get("confirmationTarget")!=null) confirmationTarget = (Integer)m.get("confirmationTarget");
        if(m.get("estimateMode")!=null) estimateMode = (String)m.get("estimateMode");
        if(m.get("avoidReuse")!=null) avoidReuse = (Boolean)m.get("avoidReuse");
    }
}
