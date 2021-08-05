package ra.btc.uses;

import ra.common.Envelope;

/**
 * Request an Exchange from/to a currency to/from BTC
 * using a supported method for a specified amount in BTC.
 *
 * Request Envelope Requires in NVP:
 *      toCurrency:String (e.g. BTC, USD, EUR) - Required
 *      fromCurrency: String (e.g. BTC, USD, EUR) - Required
 *      amountSats:Long - Required
 *
 * Supported methods:
 * LBP : F2F
 * USD : Zelle
 * BSQ : Altcoins
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
public class ExchangeForBTC extends UseRequestBase {
    public void initiate(Envelope e) {}
    public void buyBTCAsTaker() {}
    public void buyBTCAsMaker() {}
    public void sellBTCAsTaker() {}
    public void sellBTCAsMaker() {}
    public void btcSentToEscrow() {}
    public void fundsSentToSeller() {}
    public void btcEscrowReleased() {}
    public void closeOutTrade() {}
}
