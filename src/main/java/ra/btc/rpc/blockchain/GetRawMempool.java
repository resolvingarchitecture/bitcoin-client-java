package ra.btc.rpc.blockchain;

import ra.btc.MempoolEntry;
import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Returns all transaction ids in memory pool as a json array of string transaction ids.
 *
 * Hint: use getmempoolentry to fetch a specific transaction from the mempool.
 */
public class GetRawMempool extends RPCRequest {
    public static final String NAME = "getrawmempool";

    public Boolean verbose = false;
    public List<MempoolEntry> mempoolEntries = new ArrayList<>();

    public GetRawMempool(){super(NAME);}

    public GetRawMempool(Boolean verbose) {
        super(NAME);
        this.verbose = verbose;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(verbose);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            MempoolEntry entry;
            if (verbose) {
                List<Map<String,Object>> txs = (List<Map<String,Object>>) m.get(0);
                for(Map<String,Object> tx : txs) {
                    entry = new MempoolEntry();
                    entry.transactionId = (String)m.get("transactionId"); // TODO: Verify this is available (doesn't show in API)
                    entry.size = (Integer)m.get("size");
                    entry.modifiedFee = (Integer)m.get("modifiedfee");
                    entry.time = (Integer)m.get("time");
                    entry.height = (Integer)m.get("height");
                    entry.descendantCount = (Integer)m.get("descendantcount");
                    entry.descendantSize = (Integer)m.get("descendantsize");
                    entry.descendantFees = (Integer)m.get("descendantfees");
                    entry.ancestorCount = (Integer)m.get("ancestorcount");
                    entry.ancestorSize = (Integer)m.get("ancestorsize");
                    entry.ancestorFees = (Integer)m.get("ancestorfees");
                    entry.wtxid = (String)m.get("wtxid");
                    Map<String,Object> fees = (Map<String,Object>)m.get("fees");
                    entry.feesBase = (Integer)fees.get("base");
                    entry.feesModified = (Integer)fees.get("modified");
                    entry.feesAncestor = (Integer)fees.get("ancestor");
                    entry.feesDescendant = (Integer)fees.get("descendant");
                    entry.depends = (List<String>)m.get("depends");
                    entry.spentBy = (List<String>)m.get("spendby");
                    entry.bip125Replaceable = (Boolean)m.get("bip125-replaceable");
                    mempoolEntries.add(entry);
                }
            } else {
                List<String> txIds = (List<String>) m.get(0);
                for (String txId : txIds) {
                    entry = new MempoolEntry();
                    entry.transactionId = txId;
                    mempoolEntries.add(entry);
                }
            }
        }
    }
}
