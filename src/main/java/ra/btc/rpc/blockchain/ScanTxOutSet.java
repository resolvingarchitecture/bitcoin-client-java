package ra.btc.rpc.blockchain;

import ra.btc.ScanObject;
import ra.btc.ScriptPublicKey;
import ra.btc.UTXO;
import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * EXPERIMENTAL warning: this call may be removed or changed in future releases.
 *
 * Scans the unspent transaction output set for entries that match certain output descriptors.
 *
 * Examples of output descriptors are:
 *
 *     addr(<address>) Outputs whose scriptPubKey corresponds to the specified address (does not include P2PK) raw(<hex script>) Outputs whose scriptPubKey equals the specified hex scripts combo(<pubkey>) P2PK, P2PKH, P2WPKH, and P2SH-P2WPKH outputs for the given pubkey pkh(<pubkey>) P2PKH outputs for the given pubkey sh(multi(<n>,<pubkey>,<pubkey>,…)) P2SH-multisig outputs for the given threshold and pubkeys
 *
 * In the above, <pubkey> either refers to a fixed public key in hexadecimal notation, or to an xpub/xprv optionally followed by one or more path elements separated by “/”, and optionally ending in “/*” (unhardened), or “/*’” or “/*h” (hardened) to specify all unhardened or hardened child keys.
 *
 * In the latter case, a range needs to be specified by below if different from 1000.
 *
 * For more information on output descriptors, see the documentation in the doc/descriptors.md file.
 */
public class ScanTxOutSet extends RPCRequest {
    public static final String NAME = "scantxoutset";

    public enum Action {start, abort, status}

    // Request
    public Action action; // The action to execute; “start” for starting a scan, “abort” for aborting the current scan (returns true when abort was successful), “status” for progress report (in %) of the current scan
    public List<ScanObject> scanobjects;
    // Response
    public List<UTXO> unspents;
    public Double totalAmount;

    public ScanTxOutSet() {super(NAME);}

    public ScanTxOutSet(Action action, List<ScanObject> scanobjects) {
        super(NAME);
        this.action = action;
        this.scanobjects = scanobjects;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(action.name());
        params.add(scanobjects);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            List<Map<String,Object>> uMs = (List<Map<String,Object>>)m.get("unspents");
            UTXO utxo;
            unspents = new ArrayList<>();
            for(Map<String,Object> uM : uMs) {
                utxo = new UTXO();
                utxo.txid = (String)m.get("txid");
                utxo.vout = (Integer)m.get("vout");
                utxo.scriptPubKey = new ScriptPublicKey();
                utxo.scriptPubKey.hex = (String)m.get("scriptPubKey");
                utxo.value = (Double)m.get("amount");
                utxo.height = (Integer)m.get("height");
                unspents.add(utxo);
            }
            totalAmount = (Double)m.get("total_amount");
        }
    }
}
