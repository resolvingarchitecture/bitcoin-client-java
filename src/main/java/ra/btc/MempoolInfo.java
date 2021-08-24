package ra.btc;

import ra.common.JSONSerializable;
import ra.common.JSONParser;
import ra.common.JSONPretty;

import java.util.Map;

/**
 * Active state of the TX memory pool.
 */
public class MempoolInfo implements JSONSerializable {
    public Integer size;
    public Integer bytes;
    public Integer usage;
    public Integer maxMempool;
    public Integer mempoolMinFee;
    public Integer minRelayTxFee;

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
