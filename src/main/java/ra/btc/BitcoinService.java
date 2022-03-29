package ra.btc;

import ra.btc.bitcoinj.BitcoinJClient;
import ra.btc.rpc.LocalBitcoinClient;
import ra.btc.rpc.RPCRequest;
import ra.btc.rpc.blockchain.GetBlockchainInfo;
import ra.btc.rpc.blockchain.GetDifficulty;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.control.Uptime;
import ra.btc.rpc.mining.GetNetworkHashPS;
import ra.btc.rpc.network.GetNetworkInfo;
import ra.btc.rpc.network.GetPeerInfo;
import ra.btc.rpc.wallet.*;
import ra.btc.uses.UseRequest;
import ra.common.Envelope;
import ra.common.messaging.MessageProducer;
import ra.common.route.Route;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;
import ra.common.Config;
import ra.common.JSONParser;
import ra.common.SystemSettings;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Service for providing access to the Bitcoin network
 */
public class BitcoinService extends BaseService {

    private static final Logger LOG = Logger.getLogger(BitcoinService.class.getName());

    public static final String REMOTE_HOST = "ra.btc.remotehost";

    private BitcoinClient client;

    public BitcoinService() {
    }

    public BitcoinService(MessageProducer producer, ServiceStatusObserver observer) {
        super(producer, observer);
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
        boolean configLoaded = false;
        try {
            config = Config.loadAll(p, "ra-btc.config");
//            String modeParam = config.getProperty("ra.btc.mode");
//            if(modeParam!=null) {
//                mode = Byte.parseByte(modeParam);
//            }
            if(localNodeRunning()) {
                client = new LocalBitcoinClient(this);
            } else {
                client = new BitcoinJClient(this);
            }
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
        client.destroy();
        updateStatus(ServiceStatus.SHUTDOWN);
        LOG.info("Shutdown.");
        return true;
    }

    @Override
    public boolean gracefulShutdown() {
        LOG.info("Gracefully shutting down...");
        updateStatus(ServiceStatus.GRACEFULLY_SHUTTING_DOWN);
        client.destroyGracefully();
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
