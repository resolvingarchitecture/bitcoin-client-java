package ra.btc.rpc.blockchain;

import ra.common.JSONSerializable;
import ra.common.JSONParser;
import ra.common.JSONPretty;

import java.util.List;
import java.util.Map;

public class ScriptPublicKey implements JSONSerializable {

    public enum Type {pubkeyhash}

    public String asm;
    public String hex;
    public Integer reqSigs; // Number of required signatures
    public Type type; // The type eg pubkeyhash
    public List<String> addresses; // array of bitcoin addresses

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
