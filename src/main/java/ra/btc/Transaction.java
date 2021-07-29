package ra.btc;

import ra.common.JSONSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Transaction implements JSONSerializable {
    boolean inActiveChain;
    public String txid;
    public String hash;
    public Integer size;
    public Integer vsize;
    public Integer weight;
    public Integer version;
    public Integer locktime;
    public List<TXI> vIn;
    public List<UTXO> vOut;
    public String blockhash;
    public Integer confirmations;
    public Long blocktime;
    public Long time;

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            inActiveChain = (Boolean)m.get("in_active_chain");
            txid = (String)m.get("txid");
            hash = (String)m.get("hash");
            size = (Integer)m.get("size");
            vsize = (Integer)m.get("vsize");
            weight = (Integer)m.get("weight");
            version = (Integer)m.get("version");
            locktime = (Integer)m.get("locktime");
            List<Map<String,Object>> vInML = (List<Map<String,Object>>)m.get("vin");
            vIn = new ArrayList<>();
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
                vIn.add(txi);
            }
            List<Map<String,Object>> vOutML = (List<Map<String,Object>>)m.get("vout");
            vOut = new ArrayList<>();
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
            blockhash = (String)m.get("blockhash");
            confirmations = (Integer)m.get("confirmations");
            blocktime = (Long)m.get("blocktime");
            time = (Long)m.get("time");
        }
    }

    @Override
    public String toJSON() {
        return null;
    }

    @Override
    public void fromJSON(String s) {

    }
}
