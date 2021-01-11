package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;
import ra.btc.rpc.blockchain.GetBlock;

import java.util.List;
import java.util.Map;

public class BackupWallet  extends RPCRequest {

    public static final String NAME = "backupwallet";

    // Request
    public String destination;

    public BackupWallet(String destination) {
        super(NAME);
        this.destination = destination;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        params.add(destination);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {}
}
