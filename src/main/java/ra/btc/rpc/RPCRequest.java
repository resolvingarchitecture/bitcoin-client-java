package ra.btc.rpc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class RPCRequest implements JSONSerializable {

    private static Logger LOG = Logger.getLogger(RPCRequest.class.getName());

    public final String clazz = this.getClass().getName();

    public final String jsonrpc = "1.0";
    public String id;
    public List<Object> params = new ArrayList<>();

    protected RPCRequest() {}

    public static RPCRequest inflate(Map<String,Object> m) {
        String clazz = (String)m.get("clazz");
        try {
            RPCRequest request = (RPCRequest) Class.forName(clazz).getConstructor().newInstance();
            request.fromMap(m);
            return request;
        } catch (Exception e) {
            LOG.warning(e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        Map<String,Object> m = new HashMap<>();
        m.put("clazz", clazz);
        if(jsonrpc!=null) m.put("jsonrpc", jsonrpc);
        if(id!=null) m.put("id", id);
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
