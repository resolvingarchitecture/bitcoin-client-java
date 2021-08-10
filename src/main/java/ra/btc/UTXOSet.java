package ra.btc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.Map;

public class UTXOSet implements JSONSerializable {
    public Integer height;
    public String bestblock;
    public Integer transactions;
    public Integer txouts;
    public Integer bogosize;
    public String hashSerialized;
    public Integer diskSize;
    public Double totalAmount;

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
