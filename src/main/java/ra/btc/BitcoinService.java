package ra.btc;

import ra.btc.bitcoinj.BitcoinJClient;
import ra.btc.rpc.LocalBitcoinClient;
import ra.common.Envelope;
import ra.common.messaging.MessageProducer;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;
import ra.common.Config;
import ra.common.tasks.TaskRunner;

import java.util.*;
import java.util.logging.Logger;

/**
 * Service for providing access to the Bitcoin network
 */
public class BitcoinService extends BaseService {

    private static final Logger LOG = Logger.getLogger(BitcoinService.class.getName());

    private BitcoinClient client;
    private TaskRunner taskRunner;

    public BitcoinService() {
        taskRunner = new TaskRunner(1,1);
    }

    public BitcoinService(MessageProducer producer, ServiceStatusObserver observer) {
        super(producer, observer);
        taskRunner = new TaskRunner(1,1);
    }

    @Override
    public void handleDocument(Envelope e) {
        client.handleDocument(e);
    }

    @Override
    public boolean start(Properties p) {
        LOG.info("Starting...");
        updateStatus(ServiceStatus.STARTING);
        if(!super.start(p))
            return false;
        LOG.info("Loading properties...");
        try {
            config = Config.loadAll(p, "ra-btc.config");
        } catch (Exception e) {
            LOG.severe(e.getLocalizedMessage());
            return false;
        }
        if(localNodeRunning()) {
            client = new LocalBitcoinClient(this, taskRunner);
        } else {
            client = new BitcoinJClient(this, taskRunner);
        }
        try {
            client.init(p);
        } catch (Exception e) {
            LOG.severe(e.getLocalizedMessage());
            return false;
        }
        updateStatus(ServiceStatus.RUNNING);
        LOG.info("Started.");
        return true;
    }

    @Override
    public boolean shutdown() {
        LOG.info("Shutting down...");
        updateStatus(ServiceStatus.SHUTTING_DOWN);
        try {
            client.destroy();
        } catch (Exception e) {
            LOG.warning(e.getLocalizedMessage());
        }
        updateStatus(ServiceStatus.SHUTDOWN);
        LOG.info("Shutdown.");
        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        LOG.info("Gracefully shutting down...");
        updateStatus(ServiceStatus.GRACEFULLY_SHUTTING_DOWN);
        try {
            client.destroyGracefully();
        } catch (Exception e) {
            LOG.warning(e.getLocalizedMessage());
        }
        updateStatus(ServiceStatus.GRACEFULLY_SHUTDOWN);
        LOG.info("Gracefully shutdown.");
        return true;
    }

    private boolean localNodeRunning() {

        return false;
    }

//    public static void main(String[] args) {
//        BitcoinService service = new BitcoinService();
//        service.setProducer(new MessageProducer() {
//            @Override
//            public boolean send(Envelope envelope) {
//                return true;
//            }
//
//            @Override
//            public boolean send(Envelope envelope, Client client) {
//                return true;
//            }
//
//            @Override
//            public boolean deadLetter(Envelope envelope) {
//                return true;
//            }
//        });
//        Properties props = new Properties();
//        for(String arg : args) {
//            String[] nvp = arg.split("=");
//            props.put(nvp[0],nvp[1]);
//        }
//        if(service.start(props)) {
//            while(service.getServiceStatus() != ServiceStatus.SHUTDOWN) {
//                Wait.aSec(1);
//            }
//        } else {
//            System.exit(-1);
//        }
//    }

}
