package ra.btc.rpc.generate;

import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mine blocks immediately to a specified address (before the RPC call returns)
 */
public class GenerateToAddress extends RPCRequest {
    public static final String NAME = "generatetoaddress";

    // Request
    public Integer nBlocks; // How many blocks are generated immediately.
    public String address; // The address to send the newly generated bitcoin to.
    public Integer maxTries = 1000000; // How many iterations to try.
    // Response
    public List<String> blockHashes; // hashes of blocks generated

    public GenerateToAddress() {}

    public GenerateToAddress(Integer nBlocks) {
        this.nBlocks = nBlocks;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(nBlocks);
        params.add(maxTries);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            blockHashes = new ArrayList<>();
            for(Object obj : m.values()) {
                blockHashes = (List<String>)obj;
            }
        }
    }
}
