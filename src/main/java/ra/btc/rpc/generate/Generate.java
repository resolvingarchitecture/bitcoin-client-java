package ra.btc.rpc.generate;

import ra.btc.rpc.RPCRequest;
import ra.btc.rpc.control.Logging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mine up to nblocks blocks immediately (before the RPC call returns) to an address in the wallet.
 */
public class Generate extends RPCRequest {
    public static final String NAME = "generate";

    // Request
    public Integer nBlocks; // How many blocks are generated immediately.
    public Integer maxTries = 1000000; // How many iterations to try.
    // Response
    public List<String> blockHashes; // hashes of blocks generated

    public Generate(Integer nBlocks) {
        super(NAME);
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
