package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCCommand;

public class GetBlockchainInfo extends RPCCommand {

    public static final String NAME = "getblockchaininfo";

    public GetBlockchainInfo() {
        super(NAME);
    }

}
