package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Rescan the local blockchain for wallet related transactions.
 */
public class RescanBlockchain extends RPCRequest {

    public static final String NAME = "rescanblockchain";

    // Request
    public Integer startHeight = 0;
    public Integer stopHeight;

    public RescanBlockchain() {}

    public RescanBlockchain(Integer startHeight, Integer stopHeight) {
        this.startHeight = startHeight;
        this.stopHeight = stopHeight;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(startHeight);
        if(stopHeight!=null)
            params.add(stopHeight);
        return super.toMap();
    }
}
