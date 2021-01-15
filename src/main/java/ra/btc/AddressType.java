package ra.btc;

public enum AddressType {
    LEGACY("legacy"),
    P2SH_SEGWIT("p2sh-segwit"),
    BECH32("bech32");

    private String name;

    AddressType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
