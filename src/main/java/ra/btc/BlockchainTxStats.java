package ra.btc;

import ra.common.JSONSerializable;
import ra.common.JSONParser;
import ra.common.JSONPretty;

import java.util.Map;

public class BlockchainTxStats implements JSONSerializable {
    // Request
    public Integer nBlocks;
    public String blockhash;
    // Response
    public Integer time;
    public Integer txCount;
    public String windowFinalBlockHash;
    public Integer windowBlockCount;
    public Integer windowTxCount;
    public Integer windowInterval;
    public Integer txRate;

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
