package ra.btc.uses;

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
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }
}
