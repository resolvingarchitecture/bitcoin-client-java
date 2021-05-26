package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

/**
 * Removes the wallet encryption key from memory, locking the wallet.
 *
 * After calling this method, you will need to call walletpassphrase again before
 * being able to call any methods which require the wallet to be unlocked.
 */
public class WalletLock extends RPCRequest {

    public static final String NAME = "walletlock";

    public String walletName = "Default";

    public WalletLock() {
        super(NAME);
        path += "/wallet/"+walletName;
    }
}
