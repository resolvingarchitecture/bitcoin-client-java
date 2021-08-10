package ra.btc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.Map;

public class UTXO implements JSONSerializable {
    public String txid; // The transaction id
    public Integer index;
    public Integer vout; // the vout value
    public String bestBlock; // The hash of the block at the tip of the chain
    public Integer confirmations; // The number of confirmations
    public Double value; // The transaction value in BTC
    public ScriptPublicKey scriptPubKey; // the script key
    public String desc; // A specialized descriptor for the matched scriptPubKey
    public Boolean coinbase; // Coinbase or not
    public Integer height; // Height of the unspent transaction output

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
