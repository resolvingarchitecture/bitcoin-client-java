package ra.btc.rpc.blockchain;

import ra.btc.UTXOSet;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Returns statistics about the unspent transaction output set.
 *
 * Note this call may take some time.
 */
public class GetTxOutSetInfo extends RPCRequest {
    public static final String NAME = "gettxoutsetinfo";

    // Response
    public UTXOSet set; // A string that is a serialized, hex-encoded data for the proof.

    public GetTxOutSetInfo() {}

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            set = new UTXOSet();
            set.height = (Integer)m.get("height");
            set.bestblock = (String)m.get("bestblock");
            set.transactions = (Integer)m.get("transactions");
            set.txouts = (Integer)m.get("txouts");
            set.bogosize = (Integer)m.get("bogosize");
            set.hashSerialized = (String)m.get("hash_serialized");
            set.diskSize = (Integer)m.get("disk_size");
            set.totalAmount = (Double)m.get("total_amount");
        }
    }
}
