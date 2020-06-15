package ra.btc.config;

import java.util.logging.Logger;

/**
 * Configuration parameters based on network in use.
 *
 */
public abstract class BitcoinConfig {

    private static Logger LOG = Logger.getLogger(BitcoinConfig.class.getName());

    protected String baseFolderPath = "bitcoin";

    protected String[] dnsSeeds;
    protected int[] addrSeeds;

    /**
     *
     * @param networkName Must be one of dev | test | main
     * @return
     */
    public static BitcoinConfig getConfig(String networkName) {
        BitcoinConfig c = null;
        switch (networkName) {
            case "dev": {c = new DevNetConfig();break;}
            case "test": {c = new TestNetConfig();break;}
            case "main": {c = new MainNetConfig();break;}
            default: LOG.severe("Must provide a network name of: dev | test | main");
        }
        return c;
    }

    public String[] getDnsSeeds() {
        return dnsSeeds;
    }

    public int[] getAddrSeeds() {
        return addrSeeds;
    }
}
