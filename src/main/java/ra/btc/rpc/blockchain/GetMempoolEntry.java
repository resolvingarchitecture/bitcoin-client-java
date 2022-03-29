package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

/**
 * Returns mempool data for given transaction
 */
public class GetMempoolEntry extends RPCRequest {
    public static final String NAME = "getmempoolentry";

    public MempoolEntry entry = new MempoolEntry();

    public GetMempoolEntry(){super(NAME);}

    public GetMempoolEntry(String txId) {
        super(NAME);
        this.entry.transactionId = txId;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(entry.transactionId);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        entry.size = (Integer) m.get("size");
        entry.modifiedFee = (Integer) m.get("modifiedfee");
        entry.time = (Integer) m.get("time");
        entry.height = (Integer) m.get("height");
        entry.descendantCount = (Integer) m.get("descendantcount");
        entry.descendantSize = (Integer) m.get("descendantsize");
        entry.descendantFees = (Integer) m.get("descendantfees");
        entry.ancestorCount = (Integer) m.get("ancestorcount");
        entry.ancestorSize = (Integer) m.get("ancestorsize");
        entry.ancestorFees = (Integer) m.get("ancestorfees");
        entry.wtxid = (String) m.get("wtxid");
        Map<String, Object> fees = (Map<String, Object>) m.get("fees");
        entry.feesBase = (Integer) fees.get("base");
        entry.feesModified = (Integer) fees.get("modified");
        entry.feesAncestor = (Integer) fees.get("ancestor");
        entry.feesDescendant = (Integer) fees.get("descendant");
        entry.depends = (List<String>) m.get("depends");
        entry.spentBy = (List<String>) m.get("spendby");
        entry.bip125Replaceable = (Boolean) m.get("bip125-replaceable");
    }
}
