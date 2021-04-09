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

    // Request
    public String passphrase;
    public Integer timeoutSeconds;

    public WalletPassphrase() {}

    /**
     *
     * @param passphrase Wallet passphrase
     * @param timeoutSeconds The time to keep the decryption key in seconds; capped at 100000000 (~3 years).
     */
    public WalletPassphrase(String passphrase, Integer timeoutSeconds) {
        this.passphrase = passphrase;
        this.timeoutSeconds = timeoutSeconds;
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
