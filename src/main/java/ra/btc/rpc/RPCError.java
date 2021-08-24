package ra.btc.rpc;

import ra.common.JSONSerializable;
import ra.common.JSONParser;
import ra.common.JSONPretty;

import java.util.HashMap;
import java.util.Map;

public class RPCError implements JSONSerializable {

    public Integer code;
    public String message;

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = new HashMap<>();
        m.put("code", code);
        m.put("message", message);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("code")!=null) code = (Integer)m.get("id");
        if(m.get("message")!=null) message = (String)m.get("message");
    }

    @Override
    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(toMap()), 4);
    }

    @Override
    public void fromJSON(String s) {
        fromMap((Map<String, Object>) JSONParser.parse(s));
    }

    @Override
    public String toString() {
        return toJSON();
    }
}
