package ra.btc;

import ra.btc.rpc.GetDifficulty;
import ra.btc.rpc.RPCResponse;
import ra.common.Envelope;
import ra.common.messaging.MessageProducer;
import ra.common.route.Route;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Service for providing access to the Bitcoin network
 *
 */
public class BitcoinService extends BaseService {

    private static final Logger LOG = Logger.getLogger(BitcoinService.class.getName());

    public static final String LOCAL_RPC_HOST = "http://127.0.0.1:";
    public static final Integer LOCAL_RPC_PORT = 8332;

    public static final Integer MAIN_NET_PORT = 8333;
    public static final Integer TEST_NET_PORT = 18333;
    public static final Integer REG_TEST_PORT = 18444;

    public static final String AUTHN = "Basic cmE6MTIzNA==";

    // Ops
    public static final String OPERATION_VERIFY_RPC = "VERIFY_RPC";

    // Control

    // Wallet
    public static final String OPERATION_CREATE_WALLET = "CREATE_WALLET";

    // Response
    public static final String OPERATION_RPC_RESPONSE = "RPC_RESPONSE";

    public static URL rpcUrl;

    private boolean localBTCNode = false;
    private BitcoinInfo info = new BitcoinInfo();

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
                if(obj!=null) {
                    RPCResponse response = new RPCResponse();
                    response.fromJSON(new String((byte[])obj));
                    switch (response.id) {
                        case GetDifficulty.NAME: {
                            if(response.result instanceof Double) {
                                info.difficulty = (double) response.result;
                                localBTCNode = true;
                                LOG.info("Difficulty: " + info.difficulty + "; local node verified running.");
                            }
                            break;
                        }
                    }
                }
                break;
            }
            default: deadLetter(e); // Operation not supported
        }
    }

    @Override
    public boolean start(Properties p) {
        LOG.info("Starting....");
        updateStatus(ServiceStatus.STARTING);
        try {
            rpcUrl = new URL(LOCAL_RPC_HOST+LOCAL_RPC_PORT);
        } catch (MalformedURLException e) {
            LOG.severe(e.getLocalizedMessage());
            return false;
        }
        // Send to verify a local node is running
        send(new GetDifficulty().buildEnvelope());

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
        System.out.println();
//        if(service.start(props)) {
//            while(service.getServiceStatus() != ServiceStatus.SHUTDOWN) {
//                Wait.aSec(1);
//            }
//        } else {
            System.exit(-1);
//        }
    }

}
