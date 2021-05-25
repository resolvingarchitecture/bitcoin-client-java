package ra.btc.rpc.blockchain;

import ra.btc.MempoolEntry;
import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * If txid is in the mempool, returns all in-mempool ancestors.
 */
public class GetMempoolAncestors extends RPCRequest {
    public static final String NAME = "getmempoolancestors";

    public String txId;
    public Boolean verbose = false;
    public List<MempoolEntry> mempoolEntries = new ArrayList<>();

    public GetMempoolAncestors(){super(NAME);}

    public GetMempoolAncestors(String txId) {
        super(NAME);
        this.txId = txId;
    }

    public GetMempoolAncestors(String txId, Boolean verbose) {
        super(NAME);
        this.txId = txId;
        this.verbose = verbose;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(txId);
        params.add(verbose);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            MempoolEntry mAnc;
            if (verbose) {
                List<Map<String,Object>> txs = (List<Map<String,Object>>) m.get(0);
                for(Map<String,Object> tx : txs) {
                    mAnc = new MempoolEntry();
                    mAnc.transactionId = (String)m.get("transactionId"); // TODO: Verify this is available (doesn't show in API)
                    mAnc.size = (Integer)m.get("size");
                    mAnc.modifiedFee = (Integer)m.get("modifiedfee");
                    mAnc.time = (Integer)m.get("time");
                    mAnc.height = (Integer)m.get("height");
                    mAnc.descendantCount = (Integer)m.get("descendantcount");
                    mAnc.descendantSize = (Integer)m.get("descendantsize");
                    mAnc.descendantFees = (Integer)m.get("descendantfees");
                    mAnc.ancestorCount = (Integer)m.get("ancestorcount");
                    mAnc.ancestorSize = (Integer)m.get("ancestorsize");
                    mAnc.ancestorFees = (Integer)m.get("ancestorfees");
                    mAnc.wtxid = (String)m.get("wtxid");
                    Map<String,Object> fees = (Map<String,Object>)m.get("fees");
                    mAnc.feesBase = (Integer)fees.get("base");
                    mAnc.feesModified = (Integer)fees.get("modified");
                    mAnc.feesAncestor = (Integer)fees.get("ancestor");
                    mAnc.feesDescendant = (Integer)fees.get("descendant");
                    mAnc.depends = (List<String>)m.get("depends");
                    mAnc.spentBy = (List<String>)m.get("spendby");
                    mAnc.bip125Replaceable = (Boolean)m.get("bip125-replaceable");
                    mempoolEntries.add(mAnc);
                }
            } else {
                List<String> txIds = (List<String>) m.get(0);
                for (String txId : txIds) {
                    mAnc = new MempoolEntry();
                    mAnc.transactionId = txId;
                    mempoolEntries.add(mAnc);
                }
            }
        }
    }
}
