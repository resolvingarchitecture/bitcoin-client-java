package ra.btc;

import ra.common.JSONSerializable;
import ra.common.network.Network;
import ra.common.network.NetworkPeer;
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
        Map<String,Object> m = new HashMap<>();
        if(addressOrName!=null) m.put("addressOrName", addressOrName);
        if(connected!=null) m.put("connected", connected);
        if(addresses!=null) m.put("addresses", addresses);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("addressOrName")!=null) addressOrName = (String)m.get("addressOrName");
        if(m.get("connected")!=null) connected = (Boolean)m.get("connected");
        if(m.get("addresses")!=null) addresses = (Map<String,Connection>)m.get("addresses");
    }

    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(this.toMap()), 4);
    }

    public void fromJSON(String json) {
        this.fromMap((Map)JSONParser.parse(json));
    }
}
