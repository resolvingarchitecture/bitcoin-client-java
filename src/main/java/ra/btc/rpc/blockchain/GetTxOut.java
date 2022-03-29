package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

/**
 * Returns details about an unspent transaction output.
 */
public class GetTxOut extends RPCRequest {
    public static final String NAME = "gettxout";

    // Request
    public String txId;
    public Integer n;
    public Boolean includeMempool = true;
    // Response
    public UTXO utxo;

    public GetTxOut(){super(NAME);}

    public GetTxOut(String txId, Integer n) {
        super(NAME);
        this.txId = txId;
        this.n = n;
    }

    public GetTxOut(String txId, Integer n, Boolean includeMempool) {
        super(NAME);
        this.txId = txId;
        this.n = n;
        this.includeMempool = includeMempool;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(txId);
        params.add(n);
        params.add(includeMempool);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            utxo = new UTXO();
            utxo.bestBlock = (String)m.get("bestblock");
            utxo.confirmations = (Integer)m.get("confirmations");
            utxo.value = (Double)m.get("value");
            utxo.scriptPubKey = new ScriptPublicKey();
            Map<String,Object> sm = (Map<String,Object>)m.get("scriptPubKey");
            utxo.scriptPubKey.asm = (String)sm.get("asm");
            utxo.scriptPubKey.hex = (String)sm.get("hex");
            utxo.scriptPubKey.reqSigs = (Integer)sm.get("reqSigs");
            utxo.scriptPubKey.type = ScriptPublicKey.Type.valueOf((String)sm.get("type"));
            utxo.scriptPubKey.addresses = (List<String>)sm.get("addresses");
            utxo.coinbase = (Boolean)m.get("coinbase");
        }
    }
}
