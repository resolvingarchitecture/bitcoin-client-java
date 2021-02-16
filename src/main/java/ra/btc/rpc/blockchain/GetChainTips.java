package ra.btc.rpc.blockchain;

import ra.btc.ChainTip;
import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Return information about all known tips in the block tree, including the main chain as well as orphaned branches.
 */
public class GetChainTips extends RPCRequest {

    public static final String NAME = "getchaintips";

    public List<ChainTip> chainTips = new ArrayList<>();

    public GetChainTips() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        List<Map<String,Object>> mList = (List)m.get(0);
        ChainTip ct;
        for(Map<String,Object> mC : mList) {
            ct = new ChainTip();
            ct.height = (Integer)mC.get("height");
            ct.hash = (String)m.get("hash");
            ct.branchLength = (Integer)m.get("branchlen");
            ct.status = (String)m.get("hash");
            chainTips.add(ct);
        }
    }
}
