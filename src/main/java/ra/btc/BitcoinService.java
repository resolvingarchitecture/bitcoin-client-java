package ra.btc;

import ra.common.BaseService;
import ra.common.DLC;
import ra.common.MessageProducer;
import ra.common.ServiceStatus;
import ra.common.ServiceStatusListener;
import ra.common.Envelope;
import ra.common.route.Route;
import ra.btc.blockchain.BlockChain;
import ra.btc.blockstore.BlockStore;
import ra.btc.config.BitcoinConfig;
import ra.btc.network.*;
import ra.btc.requests.SendRequest;
import ra.btc.wallet.Wallet;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Service for providing access to the Bitcoin network
 *
 */
public class BitcoinService extends BaseService {

    private static final Logger LOG = Logger.getLogger(BitcoinService.class.getName());

    public static final String OPERATION_SEND = "SEND";

    private BlockChain blockChain;
    private BlockStore blockStore;
    private BitcoinPeerDiscovery peerDiscovery;
    private Wallet wallet;

    private BitcoinConfig config;

    public BitcoinService() {
    }

    public BitcoinService(MessageProducer producer, ServiceStatusListener listener) {
        super(producer, listener);
    }

    @Override
    public void handleDocument(Envelope e) {
        Route route = e.getRoute();
        String operation = route.getOperation();
        switch(operation) {
            case OPERATION_SEND: {
                SendRequest request = (SendRequest)DLC.getData(SendRequest.class,e);
//                if(!bitcoin.send(request)) {
//                    LOG.warning("Issue sending BTC to "+request.base58To);
//                }
                break;
            }
            default: deadLetter(e); // Operation not supported
        }
    }

    @Override
    public boolean start(Properties p) {
        LOG.info("Starting....");
        updateStatus(ServiceStatus.STARTING);

        updateStatus(ServiceStatus.RUNNING);
        LOG.info("Started.");
        return true;
    }

    @Override
    public boolean shutdown() {
        LOG.info("Shutting down...");
        updateStatus(ServiceStatus.SHUTTING_DOWN);


        updateStatus(ServiceStatus.SHUTDOWN);
        LOG.info("Shutdown.");
        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        LOG.info("Gracefully shutting down...");
        updateStatus(ServiceStatus.GRACEFULLY_SHUTTING_DOWN);


        updateStatus(ServiceStatus.GRACEFULLY_SHUTDOWN);
        LOG.info("Gracefully shutdown.");
        return true;
    }

    public static void main(String[] args) {
        BitcoinService service = new BitcoinService();
        Properties props = new Properties();
        for(String arg : args) {
            String[] nvp = arg.split("=");
            props.put(nvp[0],nvp[1]);
        }
        if(service.start(props)) {
            while(service.getServiceStatus() != ServiceStatus.SHUTDOWN) {
                try {
                    synchronized (service) {
                        service.wait(2 * 1000);
                    }
                } catch (InterruptedException e) {
                    System.exit(0);
                }
            }
        } else {
            System.exit(-1);
        }
    }
}
