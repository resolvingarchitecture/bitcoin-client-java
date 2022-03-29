package ra.btc.bitcoinj;

import ra.btc.BitcoinClient;
import ra.btc.BitcoinService;
import ra.common.Envelope;

import java.util.Properties;
import java.util.logging.Logger;

public class BitcoinJClient implements BitcoinClient {

    private static final Logger LOG = Logger.getLogger(BitcoinJClient.class.getName());

    private final BitcoinService service;

    public BitcoinJClient(BitcoinService service) {
        this.service = service;
    }

    @Override
    public boolean init(Properties props) throws Exception {
        return false;
    }

    @Override
    public void handleDocument(Envelope e) {

    }

    @Override
    public boolean destroy() {
        return false;
    }

    @Override
    public boolean destroyGracefully() {
        return false;
    }
}
