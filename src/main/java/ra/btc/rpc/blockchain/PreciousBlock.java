package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class PreciousBlock extends RPCRequest {
    public static final String NAME = "preciousblock";

    // Request
    public String blockhash; // the hash of the block to mark as precious

    public PreciousBlock(String blockhash) {
        super(NAME);
        this.blockhash = blockhash;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(blockhash);
        return super.toMap();
    }

}
