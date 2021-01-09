package ra.btc;

import java.util.Map;

import static ra.btc.BitcoinService.LOCAL_RPC_HOST;

public class BlockchainInfo {
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
    public Integer networkHashPS;
    public Boolean pruned;
    public Integer pruneheight;
    public Long pruneTargetSize;
    public Long sizeOnDisk;
    public Map<String,Object> softforks;
    public Integer uptimeSec;
    public Double verificationprogress;
    public String warnings;
}
