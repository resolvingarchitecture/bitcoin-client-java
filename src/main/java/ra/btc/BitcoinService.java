package ra.btc;

import ra.btc.rpc.RPCRequest;
import ra.btc.rpc.blockchain.GetBlockCount;
import ra.btc.rpc.blockchain.GetBlockchainInfo;
import ra.btc.rpc.blockchain.GetDifficulty;
import ra.btc.rpc.RPCResponse;
import ra.btc.rpc.control.Uptime;
import ra.btc.rpc.mining.GetNetworkHashPS;
import ra.btc.rpc.network.GetNetworkInfo;
import ra.btc.rpc.network.GetPeerInfo;
import ra.btc.rpc.util.EstimateSmartFee;
import ra.btc.rpc.wallet.GetBalance;
import ra.btc.rpc.wallet.GetWalletInfo;
import ra.btc.rpc.wallet.ListWallets;
import ra.btc.uses.ExchangeForBTC;
import ra.common.Client;
import ra.common.Envelope;
import ra.common.messaging.MessageProducer;
import ra.common.route.Route;
import ra.common.service.BaseService;
import ra.common.service.ServiceStatus;
import ra.common.service.ServiceStatusObserver;
import ra.util.Config;
import ra.util.JSONParser;
import ra.util.SystemSettings;
import ra.util.Wait;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

/**
 * Service for providing access to the Bitcoin network
 */
public class BitcoinService extends BaseService {

    private static final Logger LOG = Logger.getLogger(BitcoinService.class.getName());

    public static final String REMOTE_HOST = "ra.btc.remotehost";

    public static final String LOCAL_RPC_HOST = "http://127.0.0.1:";

    public static final Integer MAIN_NET_PORT = 8332;
    public static final Integer TEST_NET_PORT = 18332;
    public static final Integer REG_TEST_PORT = 18443;

    public static final String AUTHN = "Basic cmE6MTIzNA==";

    // RPC Requests
    public static final String OPERATION_RPC_REQUEST = "BTC_RPC_REQUEST";
    public static final String OPERATION_RPC_RESPONSE = "BTC_RPC_RESPONSE";

    // Lower Level Use Case Requests
    public static final String OPERATION_CREATE_2_2_MULTISIG = "CREATE_2_2_MULTISIG";
    public static final String OPERATION_CLOSE_2_2_MULTISIG = "CLOSE_2_2_MULTISIG";
    public static final String OPERATION_CREATE_2_N_MULTISIG = "CREATE_2_N_MULTISIG";
    public static final String OPERATION_CLOSE_2_N_MULTISIG = "CLOSE_2_N_MULTISIG";

    // Upper Level Use Case Requests
    // ** Wallet **
    public static final String OPERATION_CREATE_HD_WALLET = "CREATE_HD_WALLET";
    public static final String OPERATION_LOCK_HD_WALLET = "LOCK_HD_WALLET";
    public static final String OPERATION_UNLOCK_HD_WALLET = "UNLOCK_HD_WALLET";
    public static final String OPERATION_DESTROY_HD_WALLET = "DESTROY_HD_WALLET";
    public static final String OPERATION_CREATE_CLASSIC_WALLET = "CREATE_CLASSIC_WALLET";
    public static final String OPERATION_LOCK_CLASSIC_WALLET = "LOCK_CLASSIC_WALLET";
    public static final String OPERATION_UNLOCK_CLASSIC_WALLET = "UNLOCK_CLASSIC_WALLET";
    public static final String OPERATION_DESTROY_CLASSIC_WALLET = "DESTROY_CLASSIC_WALLET";
    // ** Escrow **
    public static final String OPERATION_CREATE_ESCROW = "CREATE_ESCROW";
    public static final String OPERATION_CLOSE_ESCROW = "CLOSE_ESCROW";
    // ** Buy/Sell BTC **
    public static final String OPERATION_MAKE_BTC_BUY_OFFER = "MAKE_BTC_BUY_OFFER";
    public static final String OPERATION_TAKE_BTC_BUY_OFFER = "TAKE_BTC_BUY_OFFER";
    public static final String OPERATION_MAKE_BTC_SELL_OFFER = "MAKE_BTC_SELL_OFFER";
    public static final String OPERATION_TAKE_BTC_SELL_OFFER = "TAKE_BTC_SELL_OFFER";
    public static final String OPERATION_EXCHANGE_FOR_BTC = "EXCHANGE_FOR_BTC";
    // ** BTC Trusts **
    public static final String OPERATION_CREATE_REVOCABLE_TRUST = "CREATE_REVOCABLE_TRUST";
    public static final String OPERATION_UPDATE_REVOCABLE_TRUST= "UPDATE_REVOCABLE_TRUST";
    public static final String OPERATION_CLOSE_REVOCABLE_TRUST = "CLOSE_REVOCABLE_TRUST";
    public static final String OPERATION_CREATE_IRREVOCABLE_TRUST = "CREATE_IRREVOCABLE_TRUST";
    // ** Payment Channels **
    public static final String OPERATION_OPEN_PAYMENT_CHANNEL = "OPEN_PAYMENT_CHANNEL";
    public static final String OPERATION_MAKE_PAYMENT_IN_CHANNEL = "MAKE_PAYMENT_IN_CHANNEL";
    public static final String OPERATION_CLOSE_PAYMENT_CHANNEL = "CLOSE_PAYMENT_CHANNEL";

    private final NodeConfig nodeConfig = new NodeConfig();
    public static URL rpcUrl;
    private final BlockchainInfo info = new BlockchainInfo();
    private final List<BitcoinPeer> peers = new ArrayList<>();

    private final Map<String, RPCRequest> requests = new HashMap<>();

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
            case OPERATION_RPC_REQUEST: {
                try {
                    forwardRequest(e);
                } catch (MalformedURLException malformedURLException) {
                    LOG.warning(malformedURLException.getLocalizedMessage());
                }
                break;
            }
            case OPERATION_RPC_RESPONSE: {
                Object obj = e.getContent();
                if(obj==null) {
                    LOG.warning("No content in response.");
                    break;
                }
                RPCResponse response = new RPCResponse();
                String result = null;
                if(obj instanceof String) {
                    result = (String)obj;
                } else if(obj instanceof byte[]) {
                    result = new String((byte[]) obj);
                } else {
                    LOG.warning("Unsupported response type.");
                    return;
                }
                LOG.info("BTC RPC Result: "+result);
                response.fromJSON(result);
                RPCRequest request = requests.get(response.id);
                updateInfo(request, response);
                requests.remove(response.id);
                e.addNVP(RPCCommand.RESPONSE, response);
                break;
            }
            case OPERATION_EXCHANGE_FOR_BTC: {
                ExchangeForBTC cmd = new ExchangeForBTC();

            }
            default: deadLetter(e); // Operation not supported
        }
    }

    private void forwardRequest(Envelope e) throws MalformedURLException {
        Object reqObj = e.getValue(RPCCommand.NAME);
        if(reqObj==null) {
            e.addErrorMessage(RPCCommand.NAME + " value required.");
            return;
        }
        RPCRequest request = null;
        if(reqObj instanceof RPCRequest) {
            request = (RPCRequest) reqObj;
        } else if(reqObj instanceof Map) {
            try {
                request = RPCRequest.inflate((Map<String, Object>) e.getValue(RPCCommand.NAME));
            } catch (Exception ex) {
                LOG.warning("Unable to inflate RPCRequest from map so can not make Bitcoin RPC call; ignoring: " + ex.getLocalizedMessage());
                e.addErrorMessage("Unable to inflate RPCRequest from map so can not make Bitcoin RPC call; ignoring: " + ex.getLocalizedMessage());
                return;
            }
        } else if(reqObj instanceof String) {
            try {
                Map<String,Object> tempReqM = (Map<String,Object>)JSONParser.parse((String)reqObj);
                request = RPCRequest.inflate(tempReqM);
            } catch (Exception ex) {
                LOG.warning("Unable to inflate RPCRequest from string so can not make Bitcoin RPC call; ignoring: " + ex.getLocalizedMessage());
                e.addErrorMessage("Unable to inflate RPCRequest from string so can not make Bitcoin RPC call; ignoring: " + ex.getLocalizedMessage());
                return;
            }
        } else {
            e.addErrorMessage("Must provide an RPCRequest, Map of RPCRequest, or JSON of RPCRequest.");
            return;
        }
        request.id = e.getId();
        requests.put(request.id, request);
        e.setURL(new URL(BitcoinService.rpcUrl, request.path));
        e.setAction(Envelope.Action.POST);
        e.setHeader(Envelope.HEADER_AUTHORIZATION, BitcoinService.AUTHN);
        e.setHeader(Envelope.HEADER_CONTENT_TYPE, Envelope.HEADER_CONTENT_TYPE_JSON);
        e.addContent(request.toJSON());
        e.addRoute(BitcoinService.class.getName(), OPERATION_RPC_RESPONSE);
        e.addExternalRoute("ra.http.HTTPService", "SEND");
        send(e);
    }

    private boolean sendRequest(RPCRequest request) throws MalformedURLException {
        Envelope e = Envelope.documentFactory();
        e.addNVP(RPCCommand.NAME, request);
        request.id = e.getId();
        requests.put(request.id, request);
        e.setURL(new URL(BitcoinService.rpcUrl, request.path));
        e.setAction(Envelope.Action.POST);
        e.setHeader(Envelope.HEADER_AUTHORIZATION, BitcoinService.AUTHN);
        e.setHeader(Envelope.HEADER_CONTENT_TYPE, Envelope.HEADER_CONTENT_TYPE_JSON);
        e.addContent(request.toJSON());
        e.addRoute(BitcoinService.class.getName(), OPERATION_RPC_RESPONSE);
        e.addExternalRoute("ra.http.HTTPService", "SEND");
        return send(e);
    }

    private void updateInfo(RPCRequest request, RPCResponse response) {
        if(response.error!=null) {
            LOG.warning(response.error.toString());
        } else {
            switch (request.name) {
                // TODO: Refactor out switch by passing fully qualified class name and using reflection to create instance then map
                case GetBlockchainInfo.NAME: {
                    GetBlockchainInfo gbi = (GetBlockchainInfo) request;
                    gbi.fromMap((Map<String, Object>) response.result);
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
                    info.difficulty = (Double) response.result;
                    break;
                }
                case Uptime.NAME: {
                    info.uptimeSec = (Integer) response.result;
                    break;
                }
                case GetNetworkHashPS.NAME: {
                    info.networkHashPS = (Double) response.result;
                    break;
                }
                case GetPeerInfo.NAME: {
                    GetPeerInfo gpi = (GetPeerInfo) request;
                    List<Map<String, Object>> peersInfo = (List<Map<String, Object>>) response.result;
                    BitcoinPeer bp;
                    peers.clear();
                    for (Map<String, Object> pm : peersInfo) {
                        // Map peer info to BitcoinPeer
                        bp = new BitcoinPeer();
                        bp.fromMap(pm);
                        peers.add(bp);
                    }
                    break;
                }
                case GetNetworkInfo.NAME: {
                    GetNetworkInfo gni = (GetNetworkInfo) request;
                    gni.fromMap((Map<String, Object>) response.result);
                    break;
                }
                case GetWalletInfo.NAME: {
                    GetWalletInfo gwi = (GetWalletInfo) request;
                    gwi.fromMap((Map<String, Object>) response.result);
                    break;
                }
                case GetBalance.NAME: {
                    GetBalance gb = (GetBalance) request;
                    gb.amount = (Double)response.result;
                    LOG.info("Balance: " + gb.amount);
                    break;
                }
            }
        }
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
            String env = config.getProperty("1m5.env");
            if("test".equals(env))
                rpcUrl = new URL(info.host+TEST_NET_PORT);
            else if("prod".equals(env)) {
                rpcUrl = new URL(info.host+MAIN_NET_PORT);
            } else {
                rpcUrl = new URL(info.host+REG_TEST_PORT);
            }
            String btcCfgDir;
            if(config.getProperty("ra.btc.directory")!=null) {
                btcCfgDir = config.getProperty("ra.btc.directory");
            } else {
                btcCfgDir = SystemSettings.getUserHomeDir().getAbsolutePath() + "/snap/bitcoin-core/common/.bitcoin/";
            }
            LOG.info(btcCfgDir);
            LOG.info("NodeConfig loaded: "+nodeConfig.load(btcCfgDir));
        } catch (Exception e) {
            LOG.severe(e.getLocalizedMessage());
            return false;
        }
        // Send to establish initial info
//        try {
//            sendRequest(new GetBlockchainInfo());
//            sendRequest(new Uptime());
//            sendRequest(new GetNetworkHashPS());
//            sendRequest(new GetPeerInfo());
//            sendRequest(new GetNetworkInfo());
//            sendRequest(new GetBlockCount());
//            sendRequest(new EstimateSmartFee(3));
//            sendRequest(new ListWallets());
//        } catch (Exception ex) {
//            LOG.warning(ex.getLocalizedMessage());
//            return false;
//        }

        // Tests
//        sendRequest(new CreateWallet("TestWallet"));
//        sendRequest(new GetNewAddress());

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
