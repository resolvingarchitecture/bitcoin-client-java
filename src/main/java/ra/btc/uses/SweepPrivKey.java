package ra.btc.uses;

import java.util.Map;

/**
 * Import key, generate new address, send to new address, and remove imported key
 */
public class SweepPrivKey extends UseRequestBase {

    private String privKey;

    public SweepPrivKey(String privKey) {
        this.privKey = privKey;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }
}
