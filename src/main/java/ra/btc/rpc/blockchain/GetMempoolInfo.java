package ra.btc.rpc.blockchain;

import ra.btc.MempoolInfo;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Returns details on the active state of the TX memory pool.
 */
public class GetMempoolInfo extends RPCRequest {
    public static final String NAME = "getmempoolinfo";

    public MempoolInfo info = new MempoolInfo();

    public GetMempoolInfo() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        info.size = (Integer)m.get("size");
        info.bytes = (Integer)m.get("bytes");
        info.usage = (Integer)m.get("usage");
        info.maxMempool = (Integer)m.get("maxmempool");
        info.mempoolMinFee = (Integer)m.get("mempoolminfee");
        info.minRelayTxFee = (Integer)m.get("minrelaytxfee");
    }
}
