package ra.btc.rpc.tx;

import ra.btc.*;
import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DecodeRawTransaction extends RPCRequest {
    public static final String NAME = "decoderawtransaction";

    // Request
    public String hexstring; // The transaction hex string
    public Boolean isWitness; // Whether the transaction hex is a serialized witness transaction. If iswitness is not present, heuristic tests will be used in decoding; optional, default=depends on heuristic tests

    // Response
    public Transaction tx; // the decoded transaction

    public DecodeRawTransaction(String hexstring) {
        super(NAME);
        this.hexstring = hexstring;
    }

    public DecodeRawTransaction(String hexstring, Boolean isWitness) {
        super(NAME);
        this.hexstring = hexstring;
        this.isWitness = isWitness;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(hexstring);
        if(isWitness==null) {
            // TODO: Determine isWitness
        }
        params.add(isWitness);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            tx = new Transaction();
            tx.txid = (String)m.get("txid");
            tx.hash = (String)m.get("hash");
            tx.size = (Integer)m.get("size");
            tx.vsize = (Integer)m.get("vsize");
            tx.weight = (Integer)m.get("weight");
            tx.version = (Integer)m.get("version");
            tx.locktime = (Integer)m.get("locktime");
            List<Map<String,Object>> vInML = (List<Map<String,Object>>)m.get("vin");
            tx.vIn = new ArrayList<>();
            TXI txi;
            for(Map<String,Object> vInM : vInML) {
                txi = new TXI();
                txi.txid = (String)vInM.get("txid");
                txi.vout = (Integer)vInM.get("vout");
                Map<String,Object> vInSSM = (Map<String,Object>)vInM.get("scriptSig");
                txi.scriptSig = new ScriptSig();
                txi.scriptSig.asm = (String)vInSSM.get("asm");
                txi.scriptSig.hex = (String)vInSSM.get("hex");
                txi.txInWitness = (List<String>)vInM.get("txinwitness");
                txi.sequence = (Integer)vInM.get("sequence");
                tx.vIn.add(txi);
            }
            List<Map<String,Object>> vOutML = (List<Map<String,Object>>)m.get("vout");
            tx.vOut = new ArrayList<>();
            UTXO utxo;
            for(Map<String,Object> vOutM : vOutML) {
                utxo = new UTXO();
                utxo.value = (Double)vOutM.get("value");
                utxo.index = (Integer)vOutM.get("n");
                Map<String,Object> vOutSPK = (Map<String,Object>)vOutM.get("scriptPubKey");
                utxo.scriptPubKey = new ScriptPublicKey();
                utxo.scriptPubKey.asm = (String)vOutSPK.get("asm");
                utxo.scriptPubKey.hex = (String)vOutSPK.get("hex");
                utxo.scriptPubKey.reqSigs = (Integer)vOutSPK.get("reqSigs");
                utxo.scriptPubKey.type = ScriptPublicKey.Type.valueOf((String)vOutSPK.get("type"));
                utxo.scriptPubKey.addresses = (List<String>)vOutSPK.get("addresses");
            }
        }
    }
}
