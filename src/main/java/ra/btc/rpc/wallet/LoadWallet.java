package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class LoadWallet extends RPCRequest {

    public static final String NAME = "loadwallet";

    // Request
    private final String filename;

    // Response
    private String name;
    private String warning;

    public LoadWallet(String filename) {
        super(NAME);
        this.filename = filename;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        if(filename!=null) params.add(filename);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        name = (String)m.get("name");
        warning = (String)m.get("warning");
    }

    public String getFilename() {
        return filename;
    }

    public String getName() {
        return name;
    }

    public String getWarning() {
        return warning;
    }
}
