package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;
import java.util.Map;

public class VerifyChain extends RPCRequest {
    public static final String NAME = "verifychain";

    // Request
    public Integer checkLevel = 3; // How thorough the block verification is; numeric, optional, default=3, range=0-4
    public Integer nBlocks = 6; // The number of blocks to check; numeric, optional, default=6, 0=all

    // Response
    public Boolean verified;

    public VerifyChain() {}

    public VerifyChain(Integer checkLevel, Integer nBlocks) {
        this.checkLevel = checkLevel;
        this.nBlocks = nBlocks;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(checkLevel);
        params.add(nBlocks);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            for(Object obj : m.values()) {
                verified = "true".equals(obj);
                break;
            }
        }
    }
}
