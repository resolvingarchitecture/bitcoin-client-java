package ra.btc.rpc.mining;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Returns the estimated network hashes per second based on the last n blocks.
 *
 * Pass in [blocks] to override # of blocks, -1 specifies since last difficulty change.
 *
 * Pass in [height] to estimate the network speed at the time when a certain block was found.
 */
public class GetNetworkHashPS extends RPCRequest {

    public static final String NAME = "getnetworkhashps";

    // Request
    public Integer nBlocks = 120; // The number of blocks, or -1 for blocks since last difficulty change.
    public Integer height = -1; // To estimate at the time of the given height.
    // Response
    public Integer hashesPerSecEst; // Hashes per second estimated

    public GetNetworkHashPS() {
        super(NAME);
    }

    public GetNetworkHashPS(Integer nBlocks) {
        super(NAME);
        this.nBlocks = nBlocks;
    }

    public GetNetworkHashPS(Integer nBlocks, Integer height) {
        super(NAME);
        this.nBlocks = nBlocks;
        this.height = height;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(nBlocks);
        params.add(height);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            hashesPerSecEst = (Integer)m.get("x");
        }
    }
}
