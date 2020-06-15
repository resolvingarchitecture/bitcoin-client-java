package ra.btc;

import ra.common.currency.Coin;

/**
 * TODO: Add Description
 *
 */
public class Satoshi implements Coin {

    private String base58Address;
    private long value = 0L;

    @Override
    public boolean limitedSupply() {
        return true;
    }

    /**
     * 2.1 Quadrillion Satoshis (21 Million Bitcoin)
     * @return long max supply
     */
    @Override
    public long maxSupply() {
        return 2100000000000000L;
    }

    public String getBase58Address() {
        return base58Address;
    }

    public void setBase58Address(String base58Address) {
        this.base58Address = base58Address;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public long value() {
        return value;
    }

    @Override
    public String symbol() {
        return "里";
    }

    public Satoshi(long value) {
        this.value = value;
    }
}
