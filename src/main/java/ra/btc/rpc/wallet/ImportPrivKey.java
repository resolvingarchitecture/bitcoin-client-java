package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class ImportPrivKey extends RPCRequest {

    public static final String NAME = "importprivkey";

    public String privateKeyAddress;

    public ImportPrivKey() {
        super(NAME);
    }

    public ImportPrivKey(String privateKeyAddress) {
        super(NAME);
        this.privateKeyAddress = privateKeyAddress;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = super.toMap();
        if(privateKeyAddress!=null) m.put("privateKeyAddress", privateKeyAddress);
        params.add(privateKeyAddress);
        params.add(""); // blank label
        params.add(true); // rescan wallet transactions
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        super.fromMap(m);
    }
}
