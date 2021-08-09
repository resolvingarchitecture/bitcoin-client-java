package ra.btc.uses;

import ra.common.Envelope;

import java.util.Currency;
import java.util.Map;

/**
 * Request an Exchange from/to fiat to/from BTC
 * using a supported method for a specified amount in BTC.
 *
 * Request Envelope Requires in NVP:
 *      toCurrency:String (e.g. BTC, LBP, USD, EUR) - Required
 *      fromCurrency: String (e.g. BTC, LBP, USD, EUR) - Required
 *      amountSats:Long - Required
 * One of toCurrency or fromCurrency must be BTC.
 *
 * Buy BTC as Taker is as follows:
 *  1. Look for Buy BTC offer for requested currency, amount, and method.
 *  2. If offer not found
 *      2.a. Continue to Buy BTC as Maker
 *  3. If offer found,
 *      3.a. Take Offer receiving Trade
 *      3.b. Wait for verification that BTC sent to Escrow
 *      3.c. Verify funds sent to Seller
 *      3.d. Wait for BTC funds released to provided address
 *      3.e. Close out Trade
 *
 * Buy BTC as Maker is as follows:
 *  1. Create Offer to Buy BTC with requested currency, amount, and method.
 *  2. Verify Offer is still active locally when requested by Peer.
 *  3.
 *
 * Sell BTC as Taker is as follows:
 *  1.
 *
 * Sell BTC as Maker is as follows:
 *  1.
 *
 */
public class Exchange extends UseRequestBase {

    public static int DIRECTION_TO_BTC = 0;
    public static int DIRECTION_TO_FIAT = 1;

    public void initiate(Envelope e) {}
    public void buyBTCAsTaker() {}
    public void buyBTCAsMaker() {}
    public void sellBTCAsTaker() {}
    public void sellBTCAsMaker() {}
    public void btcSentToEscrow() {}
    public void fundsSentToSeller() {}
    public void btcEscrowReleased() {}
    public void closeOutTrade() {}

    public Exchange() {}

    public Exchange(Currency fiat, long amountSats, ExchangeRequestType direction) {

    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }
}
