package ra.btc.uses;

import ra.btc.rpc.RPCRequest;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.wallet.GetNewAddress;

import java.util.Map;

/**
 * Import key, generate new address, send to new address
 */
public class SweepPrivKey extends UseRequestBase {

    private int step = 1;
    private String privKey;
    private String walletName;
    private String receiverAddress;
    private Double amountInBTC;

    public SweepPrivKey(String privKey, String walletName) {
        this.privKey = privKey;
        this.walletName = walletName;
    }

    @Override
    public Boolean additionalRequests() {
        return step < 4;
    }

    @Override
    public RPCRequest nextRequest() {
        RPCRequest request;
        switch (step) {
            // TODO: Determine how to do a sweep; import of private key may not be appropriate
//            case 1: { request = new ImportPrivKey(privKey);break; }
            case 2: { request = new GetNewAddress(walletName);break; }
//            case 3: { request = new Send(walletName, receiverAddress, amountInBTC, fromPrivKey); break; }
            default: request = null;
        }
        step++;
        return request;
    }

    @Override
    public void handleResponse(RPCRequest request, RPCResponse response) {
        if (request instanceof GetNewAddress) {
            receiverAddress = (String)response.result;
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }
}
