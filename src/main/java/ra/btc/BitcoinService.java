package ra.btc;

import ra.btc.rpc.RPCRequest;
import ra.btc.rpc.blockchain.GetBlockchainInfo;
import ra.btc.rpc.blockchain.GetDifficulty;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.control.Uptime;
import ra.btc.rpc.mining.GetNetworkHashPS;
import ra.btc.rpc.network.GetNetworkInfo;
import ra.btc.rpc.network.GetPeerInfo;
import ra.btc.rpc.wallet.GetWalletInfo;
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
import java.util.*;
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

    public static final String OPERATION_RPC_REQUEST = "BTC_RPC_REQUEST";
    public static final String OPERATION_RPC_RESPONSE = "BTC_RPC_RESPONSE";

    public static URL rpcUrl;
    private final BlockchainInfo info = new BlockchainInfo();
    private final List<BitcoinPeer> peers = new ArrayList<>();

    private final Map<String, RPCRequest> commands = new HashMap<>();

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
                String result = new String((byte[])obj);
                LOG.info("Result: "+result);
                response.fromJSON(result);
                RPCRequest cmd = commands.get(response.id);
                if(e.getDynamicRoutingSlip().peekAtNextRoute()==null) {
                    updateInfo(cmd, response);
                }
                commands.remove(response.id);
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
        RPCRequest cmd = (RPCRequest) e.getValue("cmd");
        cmd.id = e.getId();
        commands.put(cmd.id, cmd);
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

    private void updateInfo(RPCRequest cmd, RPCResponse response) {
        switch(cmd.method) {
            case GetBlockchainInfo.NAME: {
                GetBlockchainInfo gbi = (GetBlockchainInfo) cmd;
                gbi.fromMap((Map<String, Object>)response.result);
                info.automaticPruning = gbi.info.automaticPruning;
                info.bip9Softforks = gbi.info.bip9Softforks;
                info.bestblockhash = gbi.info.bestblockhash;
                info.blocks = gbi.info.blocks;
                info.chain = gbi.info.chain;
                info.chainwork = gbi.info.chainwork;
                info.difficulty = gbi.info.difficulty;
                info.headers = gbi.info.headers;
                info.mediantime = gbi.info.mediantime;
                info.pruned = gbi.info.pruned;
                info.pruneheight = gbi.info.pruneheight;
                info.pruneTargetSize = gbi.info.pruneTargetSize;
                info.sizeOnDisk = gbi.info.sizeOnDisk;
                info.softforks = gbi.info.softforks;
                info.verificationprogress = gbi.info.verificationprogress;
                info.warnings = gbi.info.warnings;
                break;
            }
            case GetDifficulty.NAME: {
                info.difficulty = (Double)response.result;
                break;
            }
            case Uptime.NAME: {
                info.uptimeSec = (Integer)response.result;
                break;
            }
            case GetNetworkHashPS.NAME: {
                info.networkHashPS = (Double)response.result;
                break;
            }
            case GetPeerInfo.NAME: {
                GetPeerInfo gpi = (GetPeerInfo) cmd;
                List<Map<String,Object>> peersInfo = (List<Map<String,Object>>)response.result;
                BitcoinPeer bp;
                peers.clear();
                for(Map<String,Object> pm : peersInfo) {
                    // Map peer info to BitcoinPeer
                    bp = new BitcoinPeer();
                    bp.fromMap(pm);
                    peers.add(bp);
                }
                break;
            }
            case GetNetworkInfo.NAME: {
                GetNetworkInfo gni = (GetNetworkInfo) cmd;
                gni.fromMap((Map<String, Object>)response.result);
                break;
            }
            case GetWalletInfo.NAME: {
                GetWalletInfo gwi = (GetWalletInfo) cmd;
                gwi.fromMap((Map<String, Object>)response.result);
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
            rpcUrl = new URL(info.host);
        } catch (Exception e) {
            LOG.severe(e.getLocalizedMessage());
            return false;
        }
        // Send to establish initial info
        Envelope e = Envelope.documentFactory();
        e.addNVP("cmd", new GetBlockchainInfo());
        send(addRoutes(e));
        Envelope e2 = Envelope.documentFactory();
        e2.addNVP("cmd", new Uptime());
        send(addRoutes(e2));
        Envelope e3 = Envelope.documentFactory();
        e3.addNVP("cmd", new GetNetworkHashPS());
        send(addRoutes(e3));
        Envelope e4 = Envelope.documentFactory();
        e4.addNVP("cmd", new GetPeerInfo());
        send(addRoutes(e4));
        Envelope e5 = Envelope.documentFactory();
        e5.addNVP("cmd", new GetNetworkInfo());
        send(addRoutes(e5));
        Envelope e6 = Envelope.documentFactory();
        e6.addNVP("cmd", new GetWalletInfo());
        send(addRoutes(e6));

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
