package ra.btc.rpc.blockchain;

import ra.btc.BlockchainInfo;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetBlockchainInfo extends RPCRequest {

    public static final String NAME = "getblockchaininfo";

    // Response
    public BlockchainInfo info;

    public GetBlockchainInfo() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        info = new BlockchainInfo();
        info.chain = (String)m.get("chain");
        info.blocks = (Integer)m.get("blocks");
        info.headers = (Integer)m.get("headers");
        info.bestblockhash = (String)m.get("bestblockhash");
        info.difficulty = (Double)m.get("difficulty");
        info.mediantime = (Integer)m.get("mediantime");
        info.verificationprogress = (Double)m.get("verificationprogress");
        info.initialblockdownload = (Boolean)m.get("initialblockdownload");
        info.chainwork = (String)m.get("chainwork");
        info.sizeOnDisk = (Long)m.get("size_on_disk");
        info.pruned = (Boolean)m.get("pruned");
        info.pruneheight = (Integer)m.get("pruneheight");
        info.automaticPruning = (Boolean)m.get("automatic_pruning");
        info.pruneTargetSize = (Long)m.get("prune_target_size");
        info.softforks = (Map<String,Object>)m.get("softforks");
        info.bip9Softforks = (Map<String,Object>)m.get("bip9_softforks");
        info.warnings = (String)m.get("warnings");
    }
}
