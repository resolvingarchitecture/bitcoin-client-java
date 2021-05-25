package ra.btc.rpc.tx;

import ra.btc.ScriptVerificationError;
import ra.btc.UTXO;
import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

/**
 * Sign inputs for raw transaction (serialized, hex-encoded).
 *
 * The second argument is an array of base58-encoded private keys that will be the only keys used to sign the transaction.
 *
 * The third optional argument (may be null) is an array of previous transaction outputs that this transaction depends on but may not yet be in the block chain.
 */
public class SignRawTransactionWithKey extends RPCRequest {

    public static final String NAME = "signrawtransactionwithkey";

    public static final String SIGHASHTYPE_ALL = "ALL";
    public static final String SIGHASHTYPE_NONE = "NONE";
    public static final String SIGHASHTYPE_SINGLE = "SINGLE";
    public static final String SIGHASHTYPE_ALL_ANYPAY = "ALL|ANYONECANPAY";
    public static final String SIGHASHTYPE_NONE_ANYPAY = "NONE|ANYONECANPAY";
    public static final String SIGHASHTYPE_SINGLE_ANYPAY = "SINGLE|ANYONECANPAY";

    // Request
    public String hexstring; // The transaction hex string; required
    public List<String> privKeys; // base58-encoded private keys for signing; required
    public List<UTXO> prevTxs; // previous dependent transaction outputs; optional, may be null
    public String sigHashType = SIGHASHTYPE_ALL; // The signature hash type; default = ALL

    // Response
    public String hex; // The hex-encoded raw transaction with signature(s)
    public Boolean complete; // If the transaction has a complete set of signatures
    public List<ScriptVerificationError> errors; // Script verification errors (if there are any)

    public SignRawTransactionWithKey() {super(NAME);}

    public SignRawTransactionWithKey(String hexstring, List<String> privKeys) {
        super(NAME);
        this.hexstring = hexstring;
        this.privKeys = privKeys;
    }

    public SignRawTransactionWithKey(String hexstring, List<String> privKeys, List<UTXO> prevTxs) {
        super(NAME);
        this.hexstring = hexstring;
        this.privKeys = privKeys;
        this.prevTxs = prevTxs;
    }

    public SignRawTransactionWithKey(String hexstring, List<String> privKeys, String sigHashType) {
        super(NAME);
        this.hexstring = hexstring;
        this.privKeys = privKeys;
        this.sigHashType = sigHashType;
    }

    public SignRawTransactionWithKey(String hexstring, List<String> privKeys, List<UTXO> prevTxs, String sigHashType) {
        super(NAME);
        this.hexstring = hexstring;
        this.privKeys = privKeys;
        this.prevTxs = prevTxs;
        this.sigHashType = sigHashType;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(hexstring);
        params.add(privKeys);
        params.add(prevTxs);
        params.add(sigHashType);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            hex = (String)m.get("hex");
        }
    }
}
