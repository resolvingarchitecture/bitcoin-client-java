package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class PruneBlockchain extends RPCRequest {
    public static final String NAME = "pruneblockchain";

    // Request
    public Integer height; // The block height to prune up to. May be set to a discrete height, or a unix timestamp to prune blocks whose block time is at least 2 hours older than the provided timestamp.

    public PruneBlockchain(Integer height) {
        super(NAME);
        this.height = height;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(height);
        return super.toMap();
    }
}
