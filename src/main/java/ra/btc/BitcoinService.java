package ra.btc;

import ra.btc.rpc.RPCCommand;
import ra.btc.rpc.blockchain.GetBlockchainInfo;
import ra.btc.rpc.blockchain.GetDifficulty;
import ra.btc.rpc.RPCResponse;
import ra.common.Client;
import ra.common.Envelope;
import ra.common.messaging.MessageProducer;
import ra.common.route.Route;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;
import ra.util.Config;
import ra.util.Wait;

import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Service for providing access to the Bitcoin network
 */
public class BitcoinService extends BaseService {

    private static final Logger LOG = Logger.getLogger(BitcoinService.class.getName());

    public static final String REMOTE_HOST = "ra.btc.remotehost";

    public static final String LOCAL_RPC_HOST = "http://127.0.0.1:8332";

    public static final Integer MAIN_NET_PORT = 8333;
    public static final Integer TEST_NET_PORT = 18333;
    public static final Integer REG_TEST_PORT = 18444;

    public static final String AUTHN = "Basic cmE6MTIzNA==";

    // Request
    public static final String OPERATION_RPC_REQUEST = "BTC_RPC_REQUEST";

    // Response
    public static final String OPERATION_RPC_RESPONSE = "BTC_RPC_RESPONSE";

    public static URL rpcUrl;
    private BlockchainInfo info = new BlockchainInfo();

    public BitcoinService() {
    }

    public BitcoinService(MessageProducer producer, ServiceStatusObserver observer) {
        super(producer, observer);
    }

    @Override
    public void handleDocument(Envelope e) {
        Route route = e.getRoute();
        String operation = route.getOperation();
        switch(operation) {
            case OPERATION_RPC_RESPONSE: {
                Object obj = e.getContent();
                if(obj==null) {
                    LOG.warning("No content in response.");
                    break;
                }
                RPCResponse response = new RPCResponse();
                response.fromJSON(new String((byte[])obj));
                Map<String, Object> result = (Map<String, Object>)response.result;
                RPCCommand cmd = (RPCCommand)e.getValue("cmd");
                cmd.fromMap(result);
                e.addContent(null);
                if(e.getDynamicRoutingSlip().peekAtNextRoute()==null) {
                    // Request originated from this Service so update BitcoinInfo with result
                    updateInfo(cmd);
                }
                break;
            }
            case OPERATION_RPC_REQUEST: {
                send(addRoutes(e));
                break;
            }
            default: deadLetter(e); // Operation not supported
        }
    }

    private Envelope addRoutes(Envelope e) {
        RPCCommand cmd = (RPCCommand) e.getValue("cmd");
        cmd.id = e.getId();
        e.setURL(BitcoinService.rpcUrl);
        e.setAction(Envelope.Action.POST);
        e.setHeader(Envelope.HEADER_AUTHORIZATION, BitcoinService.AUTHN);
        e.setHeader(Envelope.HEADER_CONTENT_TYPE, Envelope.HEADER_CONTENT_TYPE_JSON);
        e.addContent(cmd.toJSON());
        e.addRoute(BitcoinService.class.getName(), OPERATION_RPC_RESPONSE);
        e.addExternalRoute("ra.http.HTTPService", "SEND");
        e.ratchet();
        return e;
    }

    private void updateInfo(RPCCommand cmd) {
        switch(cmd.method) {
            case GetBlockchainInfo.NAME: {
                GetBlockchainInfo gbi = (GetBlockchainInfo) cmd;
                gbi.info.btcIsLocal = info.btcIsLocal;
                gbi.info.host = info.host;
                info = gbi.info;
                break;
            }
            case GetDifficulty.NAME: {
                GetDifficulty gd = (GetDifficulty) cmd;
                info.difficulty = gd.difficulty;
                info.btcIsLocal = true;
                break;
            }
        }
    }

    @Override
    public boolean start(Properties p) {
        LOG.info("Starting....");
        updateStatus(ServiceStatus.STARTING);
        try {
            config = Config.loadAll(p, "ra-btc.config");
//            if(config.get(REMOTE_HOST)!=null) {
//                host = config.getProperty(REMOTE_HOST);
//            } else {
                info.host = LOCAL_RPC_HOST;
//            }
            rpcUrl = new URL(info.host);
        } catch (Exception e) {
            LOG.severe(e.getLocalizedMessage());
            return false;
        }
        // Send to verify a local node is running
        Envelope e = Envelope.documentFactory();
        e.addNVP("cmd", new GetBlockchainInfo());
        send(addRoutes(e));

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
        service.setProducer(new MessageProducer() {
            @Override
            public boolean send(Envelope envelope) {
                return true;
            }

            @Override
            public boolean send(Envelope envelope, Client client) {
                return true;
            }

            @Override
            public boolean deadLetter(Envelope envelope) {
                return true;
            }
        });
        Properties props = new Properties();
        for(String arg : args) {
            String[] nvp = arg.split("=");
            props.put(nvp[0],nvp[1]);
        }
        if(service.start(props)) {
            while(service.getServiceStatus() != ServiceStatus.SHUTDOWN) {
                Wait.aSec(1);
            }
        } else {
            System.exit(-1);
        }
    }

}
