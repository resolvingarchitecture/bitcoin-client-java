package ra.btc.rpc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.HashMap;
import java.util.Map;

public class RPCResponse implements JSONSerializable {

    public String id;
    public RPCError error;
    public Object result;

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = new HashMap<>();
        if(id!=null) m.put("id", id);
        if(error!=null) m.put("error", error);
        if(result!=null) m.put("result", result);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("id")!=null) id = (String)m.get("id");
        if(m.get("error")!=null) {
            Map<String,Object> errorMap = (Map<String,Object>)m.get("error");
            error = new RPCError();
            error.code = (Integer)errorMap.get("code");
            error.message = (String)errorMap.get("message");
        }
        if(m.get("result")!=null) {
           result = m.get("result");
        }
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
