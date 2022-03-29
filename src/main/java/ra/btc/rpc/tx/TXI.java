package ra.btc.rpc.tx;

import ra.common.JSONSerializable;
import ra.common.JSONParser;
import ra.common.JSONPretty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TXI implements JSONSerializable {
    public String txid; // Transaction ID in Hex, required
    public Integer vout; // The output number, required
    public ScriptSig scriptSig; // the script
    public List<String> txInWitness; // hex-encoded witness data (if any)
    public Integer sequence; // The sequence number; optional; default=depends on the value of the 'replaceable' and 'locktime' arguments

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = new HashMap<>();
        if(txid!=null) m.put("txid", txid);
        if(vout!=null) m.put("vout", vout);
        if(scriptSig!=null) m.put("scriptSig", scriptSig.toMap());
        if(txInWitness!=null) m.put("txInWitness", txInWitness);
        if(sequence!=null) m.put("sequence", sequence);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("txid")!=null) txid = (String)m.get("txid");
        if(m.get("vout")!=null) vout = (Integer)m.get("vout");
        if(m.get("scriptSig")!=null) {
            scriptSig = new ScriptSig();
            scriptSig.fromMap((Map<String,Object>)m.get("scriptSig"));
        }
        if(m.get("txInWitness")!=null) txInWitness = (List<String>)m.get("txInWitness");
        if(m.get("sequence")!=null) sequence = (Integer)m.get("sequence");
    }

    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(this.toMap()), 4);
    }

    public void fromJSON(String json) {
        this.fromMap((Map)JSONParser.parse(json));
    }
}
