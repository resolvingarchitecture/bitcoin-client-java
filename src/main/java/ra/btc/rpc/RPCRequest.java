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

    public String jsonrpc = "1.0";
    public String method;
    public String path = "";
    public String id;
    public String error;
    public List<Object> params = new ArrayList<>();

    protected RPCRequest(String method) {
        this.method = method;
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
        m.put("method", method);
        m.put("path", path);
        if(jsonrpc!=null) m.put("jsonrpc", jsonrpc);
        if(id!=null) m.put("id", id);
        if(params!=null) m.put("params", params);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("id")!=null) id = (String)m.get("id");
        if(m.get("error")!=null) error = (String)m.get("error");
        if(m.get("method")!=null) method = (String)m.get("method");
        if(m.get("path")!=null) path = (String)m.get("path");
        if(m.get("jsonrpc")!=null) jsonrpc = (String)m.get("jsonrpc");
        if(m.get("params")!=null) params = (List<Object>)m.get("params");
    }

    @Override
    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(toMap()), 4);
    }

    @Override
    public void fromJSON(String s) {
        fromMap((Map<String,Object>)JSONParser.parse(s));
    }

}
