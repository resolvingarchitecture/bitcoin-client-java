package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

public class GetBestBlockHash extends RPCRequest {

    public static final String NAME = "getbestblockhash";

    public String blockhash;

    public GetBestBlockHash() {}
}
