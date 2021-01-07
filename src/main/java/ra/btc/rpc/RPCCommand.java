package ra.btc.rpc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RPCCommand implements JSONSerializable {

    public String jsonrpc = "1.0";
    public String id;
    public String method;
    public List<Object> params = new ArrayList<>();

    protected RPCCommand(String method) {
        this.method = method;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        Map<String,Object> m = new HashMap<>();
        if(jsonrpc!=null) m.put("jsonrpc", jsonrpc);
        if(id!=null) m.put("id", id);
        if(method!=null) m.put("method", method);
        if(params!=null) m.put("params", params);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {}

    @Override
    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(toMap()), 4);
    }

    @Override
    public void fromJSON(String s) {
        fromMap((Map<String,Object>)JSONParser.parse(s));
    }

}
