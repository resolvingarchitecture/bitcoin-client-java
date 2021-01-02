package ra.btc.rpc;

import ra.btc.BitcoinService;
import ra.common.Envelope;
import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ra.btc.BitcoinService.OPERATION_RESPONSE;

public abstract class RPCCommand implements JSONSerializable {

    public String jsonrpc = "1.0";
    public String id;
    public String method;
    public List<Object> params = new ArrayList<>();

    protected RPCCommand(String method) {
        this.method = method;
        this.id = method;
    }

    public Envelope buildEnvelope() {
        Envelope e = Envelope.documentFactory();
        e.setURL(BitcoinService.rpcUrl);
        e.setAction(Envelope.Action.POST);
        e.setHeader(Envelope.HEADER_AUTHORIZATION, BitcoinService.AUTHN);
        e.setHeader(Envelope.HEADER_CONTENT_TYPE, Envelope.HEADER_CONTENT_TYPE_JSON);
        e.addContent(this.toJSON());
        e.addRoute(BitcoinService.class.getName(), OPERATION_RESPONSE);
        e.addExternalRoute("ra.http.HTTPService", "SEND");
        e.ratchet();
        return e;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = new HashMap<>();
        if(jsonrpc!=null) m.put("jsonrpc", jsonrpc);
        if(id!=null) m.put("id", id);
        if(method!=null) m.put("method", method);
        if(params!=null) m.put("params", params);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("jsonrpc")!=null) jsonrpc = (String)m.get("jsonrpc");
        if(m.get("id")!=null) id = (String)m.get("id");
        if(m.get("method")!=null) method = (String)m.get("method");
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
