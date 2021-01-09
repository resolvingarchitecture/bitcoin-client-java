package ra.btc;

import java.util.List;
import java.util.Map;

public class BlockchainInfo {
    public boolean btcIsLocal = false;
    public String host;
    public String chain;
    public Long blocks;
    public Long headers;
    public String bestblockhash;
    public Double difficulty;
    public Long mediantime;
    public Long verificationprogress;
    public Boolean initialblockdownload;
    public String chainwork;
    public Long sizeOnDisk;
    public Boolean pruned;
    public Long pruneheight;
    public Boolean automaticPruning;
    public Long pruneTargetSize;
    public List<Map<String,Object>> softforks;
    public Map<String,Object> bip9Softforks;
    public String warnings;
}
