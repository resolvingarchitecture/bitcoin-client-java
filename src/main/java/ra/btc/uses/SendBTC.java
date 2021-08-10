package ra.btc.uses;

import ra.btc.rpc.RPCRequest;
import ra.btc.rpc.RPCResponse;
import ra.common.currency.crypto.BTC;

import java.util.Map;

/**
 * Send BTC to a public address.
 *
 */
public class SendBTC extends UseRequestBase {

    public String receiverAddress;
    public BTC totalAmount;
    public BTC receiverAmount;
    public BTC estimatedMinerFee;
    public BTC devFee;

    @Override
    public Boolean additionalRequests() {
        return null;
    }

    @Override
    public RPCRequest nextRequest() {
        return null;
    }

    @Override
    public void handleResponse(RPCRequest request, RPCResponse response) {

    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }
}
