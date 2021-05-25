package ra.btc.rpc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public abstract class RPCRequest implements JSONSerializable {

    private static Logger LOG = Logger.getLogger(RPCRequest.class.getName());

    public final String clazz = this.getClass().getName();

    public final String jsonrpc = "1.0";
    public final String name;
    public String id;
    public List<Object> params = new ArrayList<>();

    protected RPCRequest(String name) {
        this.name = name;
    }

    public static RPCRequest inflate(Map<String,Object> m) throws
            ClassNotFoundException,
            NoSuchMethodException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException {
        String clazz = (String)m.get("clazz");
        RPCRequest request = (RPCRequest) Class.forName(clazz).getConstructor().newInstance();
        request.fromMap(m);
        return request;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        Map<String,Object> m = new HashMap<>();
        m.put("clazz", clazz);
        m.put("method", name);
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
