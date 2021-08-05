package ra.btc.uses;

/**
 * Import key, generate new address, send to new address, and remove imported key
 */
public class SweepPrivKey extends UseRequestBase {

    private String privKey;

    public SweepPrivKey(String privKey) {
        this.privKey = privKey;
    }

}
