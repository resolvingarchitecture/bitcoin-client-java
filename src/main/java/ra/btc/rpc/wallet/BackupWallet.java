package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Safely copies current wallet file to destination, which can be a directory or a path with filename.
 */
public class BackupWallet extends RPCRequest {

    public static final String NAME = "backupwallet";

    // Request
    public String destination;

    public BackupWallet() {super(NAME);}

    /**
     *
     * @param destination Directory or path with a filename
     */
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
