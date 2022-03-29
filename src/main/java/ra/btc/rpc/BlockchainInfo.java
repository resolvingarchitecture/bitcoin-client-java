package ra.btc.rpc;

import ra.common.JSONSerializable;
import ra.common.JSONParser;
import ra.common.JSONPretty;

import java.util.Map;

import static ra.btc.rpc.LocalBitcoinClient.LOCAL_RPC_HOST;

public class BlockchainInfo implements JSONSerializable {
    public Boolean automaticPruning;
    public String bestblockhash;
    public Map<String,Object> bip9Softforks;
    public Integer blocks;
    public boolean btcIsLocal = true;
    public String chain;
    public String chainwork;
    public Double difficulty;
    public Integer headers;
    public String host = LOCAL_RPC_HOST;
    public Boolean initialblockdownload;
    public Integer mediantime;
    public Double networkHashPS;
    public Boolean pruned;
    public Integer pruneheight;
    public Long pruneTargetSize;
    public Long sizeOnDisk;
    public Map<String,Object> softforks;
    public Integer uptimeSec;
    public Double verificationprogress;
    public String warnings;

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
