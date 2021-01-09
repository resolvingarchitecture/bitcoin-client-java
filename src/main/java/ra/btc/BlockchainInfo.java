package ra.btc;

import java.util.List;
import java.util.Map;

public class BlockchainInfo {
    public boolean btcIsLocal = false;
    public String host;
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
    public String warnings;
}
