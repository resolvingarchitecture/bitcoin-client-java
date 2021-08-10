package ra.btc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.List;
import java.util.Map;

public class NetworkInfo implements JSONSerializable {
    public Integer version;
    public String subVersion;
    public Integer protocolVersion;
    public String localServices;
    public Boolean localRelay;
    public Integer timeOffset;
    public Integer connections;
    public Boolean networkActive;
    public List<Network> networks;
    public Double relayFee;
    public Double incrementalFee;
    public List<LocalAddress> localAddresses;
    public String warnings;

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
