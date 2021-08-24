package ra.btc;

import ra.common.JSONSerializable;
import ra.common.JSONParser;
import ra.common.JSONPretty;

import java.util.List;
import java.util.Map;

public class MempoolEntry implements JSONSerializable {
    public String transactionId;
    public Integer size;
    public Integer modifiedFee;
    public Integer time;
    public Integer height;
    public Integer descendantCount;
    public Integer descendantSize;
    public Integer descendantFees;
    public Integer ancestorCount;
    public Integer ancestorSize;
    public Integer ancestorFees;
    public String wtxid;
    public Integer feesBase;
    public Integer feesModified;
    public Integer feesAncestor;
    public Integer feesDescendant;
    public List<String> depends;
    public List<String> spentBy;
    public Boolean bip125Replaceable;

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }

    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(this.toMap()), 4);
    }

    public void fromJSON(String json) {
        this.fromMap((Map)JSONParser.parse(json));
    }
}
