package ra.btc;

import ra.common.JSONSerializable;
import ra.common.JSONParser;
import ra.common.JSONPretty;

import java.util.HashMap;
import java.util.Map;

public class ScriptSig implements JSONSerializable {
    public String asm;
    public String hex;

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = new HashMap<>();
        if(asm!=null) m.put("asm", asm);
        if(hex!=null) m.put("hex", hex);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("asm")!=null) asm = (String)m.get("asm");
        if(m.get("hex")!=null) hex = (String)m.get("hex");
    }

    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(this.toMap()), 4);
    }

    public void fromJSON(String json) {
        this.fromMap((Map)JSONParser.parse(json));
    }
}
