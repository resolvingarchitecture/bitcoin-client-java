package ra.btc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.Map;

public class ScriptVerificationError implements JSONSerializable {
    public String txid; // The hash of the referenced, previous transaction
    public Integer vOut; // The index of the output to spent and used as input
    public String scriptSig; // The hex-encoded signature script
    public Integer n; // Script sequence number
    public String error; // Verification or signing error related to the input

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
