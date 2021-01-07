package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCCommand;

public class GetBestBlockHash extends RPCCommand {

    public static final String NAME = "getbestblockhash";

    public String blockhash;

    public GetBestBlockHash() {
        super(NAME);
    }
}
