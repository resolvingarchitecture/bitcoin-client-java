package ra.btc.config;

/**
 * TODO: Add Description
 *
 */
class TestNetConfig extends BitcoinConfig {

    TestNetConfig() {

        dnsSeeds = new String[] {
                "testnet-seed.bitcoin.jonasschnelli.ch", // Jonas Schnelli
                "seed.tbtc.petertodd.org",               // Peter Todd
                "seed.testnet.bitcoin.sprovoost.nl",     // Sjors Provoost
                "testnet-seed.bluematt.me",              // Matt Corallo
                "bitcoin-testnet.bloqseeds.net",         // Bloq
        };

        addrSeeds = null;

    }
}
