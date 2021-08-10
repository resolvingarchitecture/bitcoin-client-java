package ra.btc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.List;
import java.util.Map;

public class Block implements JSONSerializable {
    public String hash;
    public int confirmations;
    public long blocksize;
    public long blocksizeStripped; // excluding witness data
    public int blockweight;
    public long blockheight;
    public int blockversion;
    public String merkleroot;
    public List<String> tx;
    public long time;
    public long mediantime;
    public long nonce;
    public String bits;
    public Double difficulty;
    public String chainwork;
    public long nTx;
    public String previousblockhash;
    public String nextblockhash;

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
