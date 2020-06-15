package ra.btc.wallet;

import ra.btc.Satoshi;
import ra.btc.blockchain.Transaction;

/**
 * TODO: Add Description
 *
 */
public interface SatoshisSent {
    void onSatoshisSent(Wallet wallet, Transaction tx, Satoshi prevBalance, Satoshi newBalance);
}
