package ra.btc;

import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.Map;

import static ra.btc.BitcoinService.LOCAL_RPC_HOST;

public class BlockchainInfo {
    public boolean btcIsLocal = true;
    public String host = LOCAL_RPC_HOST;
    public String chain;
    public Integer blocks;
    public Integer headers;
    public String bestblockhash;
    public Double difficulty;
    public Integer mediantime;
    public Double verificationprogress;
    public Boolean initialblockdownload;
    public String chainwork;
    public Long sizeOnDisk;
    public Boolean pruned;
    public Integer pruneheight;
    public Boolean automaticPruning;
    public Long pruneTargetSize;
    public Map<String,Object> softforks;
    public Map<String,Object> bip9Softforks;
    public Integer uptimeSec;
    public String warnings;
}
