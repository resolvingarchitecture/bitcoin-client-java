package ra.btc.rpc;

import ra.btc.*;
import ra.btc.rpc.blockchain.GetBlockchainInfo;
import ra.btc.rpc.blockchain.GetDifficulty;
import ra.btc.rpc.control.Uptime;
import ra.btc.rpc.mining.GetNetworkHashPS;
import ra.btc.rpc.network.GetNetworkInfo;
import ra.btc.rpc.network.GetPeerInfo;
import ra.btc.rpc.wallet.*;
import ra.btc.uses.UseRequest;
import ra.common.Envelope;
import ra.common.JSONParser;
import ra.common.SystemSettings;
import ra.common.route.Route;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class LocalBitcoinClient implements BitcoinClient {

    private static final Logger LOG = Logger.getLogger(LocalBitcoinClient.class.getName());

    public static final Integer MAIN_NET_PORT = 8332;
    public static final Integer TEST_NET_PORT = 18332;
    public static final Integer REG_TEST_PORT = 18443;

    public static final String LOCAL_RPC_HOST = "http://127.0.0.1:";
    public static final String AUTHN = "Basic cmE6MTIzNA==";

    // RPC Requests
    public static final String OPERATION_RPC_REQUEST = "BTC_RPC_REQUEST";
    public static final String OPERATION_RPC_RESPONSE = "BTC_RPC_RESPONSE";

    public static final String OPERATION_USE_REQUEST = "USE_REQUEST";

    private final BitcoinService service;

    private final NodeConfig nodeConfig = new NodeConfig();
    public static URL rpcUrl;
    private final BlockchainInfo info = new BlockchainInfo();
    private final List<BitcoinPeer> peers = new ArrayList<>();

    // holds RPCRequests for simple correlation of RPCResponses
    private final Map<String, RPCRequest> clientRPCRequestHold = new HashMap<>();
    private final Map<String, UseRequest> clientUseRequestHold = new HashMap<>();
    // holds internal RPCRequests that are required to fulfill external RPCRequests, e.g. need to ensure a wallet is loaded prior to checking its balance
    private final Map<String, RPCRequest> internalRequestHold = new HashMap<>();

    private byte mode = 0; // 0 = local, 1 = remote personal, 2 = random remote non-personal
    private String currentWalletName = "";

    public LocalBitcoinClient(BitcoinService service) {
        this.service = service;
    }

    @Override
    public boolean init(Properties config) throws Exception {
        String env = config.getProperty("ra.env");
        if("test".equalsIgnoreCase(env) || "qa".equalsIgnoreCase(env))
            rpcUrl = new URL(info.host+TEST_NET_PORT);
        else if("prod".equalsIgnoreCase(env)) {
            rpcUrl = new URL(info.host+MAIN_NET_PORT);
        } else {
            rpcUrl = new URL(info.host+REG_TEST_PORT);
        }
        String btcCfgDir;
        if(nonNull(config.getProperty("ra.btc.directory"))) {
            btcCfgDir = config.getProperty("ra.btc.directory");
        } else {
            btcCfgDir = SystemSettings.getUserHomeDir().getAbsolutePath() + "/snap/bitcoin-core/common/.bitcoin/";
        }
        LOG.info(btcCfgDir);
        if(!nodeConfig.load(btcCfgDir)) {
            LOG.severe("Unable to load Bitcoin Node config.");
            return false;
        }
        return true;
    }

    @Override
    public void handleDocument(Envelope e) {
        Route route = e.getRoute();
        String operation = route.getOperation();
        switch(operation) {
            case OPERATION_RPC_REQUEST: {
                RPCRequest request = extractRPCRequest(e);
                RPCRequest requestToForward = request;
                if(isNull(request)) return;
                String corrId = UUID.randomUUID().toString();
                e.addNVP(BitcoinService.class.getName()+".corrId", corrId);
                clientRPCRequestHold.put(corrId, request);
                if(request instanceof GetWalletInfo) {
                    // Need to ensure Wallet is loaded first
                    GetWalletInfo getWalletInfo = (GetWalletInfo) request;
                    getWalletInfo.setWalletName(currentWalletName);
                    LoadWallet loadWallet = new LoadWallet();
                    internalRequestHold.put(corrId, loadWallet);
                    loadWallet.walletName = currentWalletName;
                    requestToForward = loadWallet;
                } else if(request instanceof SendToAddress) {
                    SendToAddress sendToAddress = (SendToAddress)request;
                    sendToAddress.setWalletName(currentWalletName);
                    if(isNull(sendToAddress.confirmationTarget))
                        sendToAddress.confirmationTarget = 6;
                }
                try {
                    forwardRequest(e, requestToForward);
                } catch (MalformedURLException malformedURLException) {
                    LOG.warning(malformedURLException.getLocalizedMessage());
                }
                break;
            }
            case OPERATION_RPC_RESPONSE: {
                Object obj = e.getContent();
                String responseStr = new String((byte[]) obj);
                LOG.info("BTC RPC Response: " + responseStr);
                RPCResponse response = new RPCResponse();
                response.fromJSON(responseStr);
                String corrId = (String) e.getValue(BitcoinService.class.getName() + ".corrId");
                RPCRequest clientRequest = clientRPCRequestHold.get(corrId);
                RPCRequest internalRequest = internalRequestHold.get(corrId);
                if(nonNull(response.error)) {
                    handleError(clientRequest, response);
                }
                if (nonNull(internalRequest)) {
                    if(clientRequest instanceof GetWalletInfo && internalRequest instanceof LoadWallet) {
                        if(isNull(response.error) || response.error.code == -35) { // -35 = already loaded
                            clientRPCRequestHold.remove(corrId);
                            internalRequestHold.remove(corrId);
                            try {
                                forwardRequest(e, clientRequest);
                                internalRequestHold.remove(corrId);
                            } catch (MalformedURLException malformedURLException) {
                                LOG.warning(malformedURLException.getLocalizedMessage());
                                clientRPCRequestHold.remove(corrId);
                                internalRequestHold.remove(corrId);
                            }
                            if(nonNull(response.error)) {
                                if(response.error.code == -35) {
                                    LOG.info(((LoadWallet)internalRequest).walletName+" wallet already loaded.");
                                } else {
                                    LOG.warning("LoadWallet error: " +response.error.message);
                                }
                            }
                        } else if(response.error.code == -18) {
                            LOG.warning("Wallet does not exist: "+((LoadWallet)internalRequest).walletName);
                        }
                    } else if(internalRequest instanceof ListWallets) {
                        currentWalletName = (String)((List)response.result).get(0);
                        LOG.info("Current Wallet assumed as first from ListWallets (for now): "+currentWalletName);
                    }
                } else {
//                    e.addNVP(RPCCommand.RESPONSE, JSONParser.toString(response.toMap()));
                    e.addNVP(RPCCommand.RESPONSE, response.toMap());
                    clientRPCRequestHold.remove(corrId);
                    // unwind
                }
                break;
            }
            case OPERATION_USE_REQUEST: {
                UseRequest useRequest = extractUseRequest(e);
                if(isNull(useRequest)) return;
                String corrId = UUID.randomUUID().toString();
                e.addNVP(BitcoinService.class.getName()+".corrId", corrId);
                clientUseRequestHold.put(corrId, useRequest);

            }
            default:
                service.deadLetter(e); // Operation not supported
        }
    }

    private boolean sendInternalRequest(RPCRequest request) throws MalformedURLException {
        Envelope e = Envelope.documentFactory();
        String corrId = UUID.randomUUID().toString();
        e.addNVP(BitcoinService.class.getName()+".corrId", corrId);
        internalRequestHold.put(corrId, request);
        return forwardRequest(e, request);
    }

    private boolean forwardRequest(Envelope e, RPCRequest request) throws MalformedURLException {
        String json = request.toJSON();
        e.addNVP(RPCCommand.NAME, json);
        e.setURL(new URL(rpcUrl, request.path));
        e.setAction(Envelope.Action.POST);
        e.setHeader(Envelope.HEADER_AUTHORIZATION, AUTHN);
        e.setHeader(Envelope.HEADER_CONTENT_TYPE, Envelope.HEADER_CONTENT_TYPE_JSON);
        LOG.info("Sending to BTC Node: "+json);
        e.addContent(json);
        e.addRoute(BitcoinService.class.getName(), OPERATION_RPC_RESPONSE);
        e.addExternalRoute("ra.http.HTTPService", "SEND");
        return service.send(e);
    }

    private void handleError(RPCRequest request, RPCResponse response) {
//        LOG.warning(response.error.code+":"+response.error.message);
        if(request instanceof SendToAddress) {
            if(response.error.code == -6) { // Insufficient Funds
                LOG.info(response.error.message);
            }
        }
    }

    private RPCRequest extractRPCRequest(Envelope e) {
        RPCRequest request = null;
        Object reqObj = e.getValue(RPCCommand.NAME);
        if(isNull(reqObj)) {
            e.addErrorMessage(RPCCommand.NAME + " value required.");
        } else if(reqObj instanceof RPCRequest) {
            request = (RPCRequest) reqObj;
        } else if(reqObj instanceof Map) {
            try {
                request = RPCRequest.inflate((Map<String, Object>) e.getValue(RPCCommand.NAME));
            } catch (Exception ex) {
                LOG.warning("Unable to inflate RPCRequest from map so can not make Bitcoin RPC call; ignoring: " + ex.getLocalizedMessage());
                e.addErrorMessage("Unable to inflate RPCRequest from map so can not make Bitcoin RPC call; ignoring: " + ex.getLocalizedMessage());
            }
        } else if(reqObj instanceof String) {
            try {
                Map<String,Object> tempReqM = (Map<String,Object>) JSONParser.parse((String)reqObj);
                request = RPCRequest.inflate(tempReqM);
            } catch (Exception ex) {
                LOG.warning("Unable to inflate RPCRequest from string so can not make Bitcoin RPC call; ignoring: " + ex.getLocalizedMessage());
                e.addErrorMessage("Unable to inflate RPCRequest from string so can not make Bitcoin RPC call; ignoring: " + ex.getLocalizedMessage());
            }
        } else {
            e.addErrorMessage("Must provide an RPCRequest, Map of RPCRequest, or JSON of RPCRequest.");
        }
        return request;
    }

    private UseRequest extractUseRequest(Envelope e) {

        return null;
    }

    private void updateInfo(RPCRequest request, RPCResponse response) {
        if(nonNull(response.error)) {
            LOG.warning(response.error.toString());
        } else {
            switch (request.method) {
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
                case ImportPrivKey.NAME: {
                    // TODO: Perform wallet backup after importing private key

                }
            }
        }
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
