package ra.btc.uses;

import ra.common.currency.crypto.BTC;

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
}
