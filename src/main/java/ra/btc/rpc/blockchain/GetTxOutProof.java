package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

/**
 * Returns a hex-encoded proof that “txid” was included in a block.
 *
 * NOTE: By default this function only works sometimes. This is when there is an unspent output in the
 * utxo for this transaction. To make it always work, you need to maintain a transaction index, using
 * the -txindex command line option or specify the block in which the transaction is included manually
 * (by blockhash).
 */
public class GetTxOutProof extends RPCRequest {
    public static final String NAME = "gettxoutproof";

    // Request
    public List<String> txIds; // A json array of txids to filter
    public String blockhash; // If specified, looks for txid in the block with this hash
    // Response
    public String data; // A string that is a serialized, hex-encoded data for the proof.

    public GetTxOutProof() {super(NAME);}

    public GetTxOutProof(List<String> txIds) {
        super(NAME);
        this.txIds = txIds;
    }

    public GetTxOutProof(List<String> txIds, String blockhash) {
        super(NAME);
        this.txIds = txIds;
        this.blockhash = blockhash;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(txIds);
        if(blockhash!=null)
            params.add(blockhash);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            data = (String)m.get("data");
        }
    }
}
