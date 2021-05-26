package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Stores the wallet decryption key in memory for ‘timeout’ seconds.
 *
 * This is needed prior to performing transactions related to private keys such as sending bitcoins Note:
 *
 * Issuing the walletpassphrase command while the wallet is already unlocked will set a new unlock time that overrides the old one.
 */
public class WalletPassphrase extends RPCRequest {

    public static final String NAME = "walletpassphrase";

    public String walletName = "Default";

    // Request
    public String passphrase = "1234"; // Default for testing only
    public Integer timeoutSeconds = 60;

    public WalletPassphrase() {
        super(NAME);
        path += "/wallet/"+walletName;
    }

    /**
     *
     * @param passphrase Wallet passphrase
     * @param timeoutSeconds The time to keep the decryption key in seconds; capped at 100000000 (~3 years).
     */
    public WalletPassphrase(String passphrase, Integer timeoutSeconds) {
        super(NAME);
        this.passphrase = passphrase;
        this.timeoutSeconds = timeoutSeconds;
        path += "/wallet/"+walletName;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        params.add(passphrase);
        params.add(timeoutSeconds);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {}
}
