package ra.btc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.HashMap;
import java.util.Map;

public class BitcoinPeer implements JSONSerializable {

    public enum Connection {inbound, outbound}

    public String addressOrName;
    public Boolean connected;
    public Map<String,Connection> addresses = new HashMap<>(); // IP address:port, Connection

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
