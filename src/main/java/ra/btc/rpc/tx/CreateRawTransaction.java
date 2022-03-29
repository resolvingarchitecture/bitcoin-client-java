package ra.btc.rpc.tx;

import ra.btc.rpc.blockchain.UTXO;
import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

/**
 * Create a transaction spending the given inputs and creating new outputs.
 *
 * Outputs can be addresses or data.
 *
 * Returns hex-encoded raw transaction.
 *
 * Note that the transaction’s inputs are not signed, and it is not stored in the wallet or transmitted to the network.
 */
public class CreateRawTransaction extends RPCRequest {
    public static final String NAME = "createrawtransaction";

    // Request
    public List<TXI> inputs;
    public List<UTXO> outputs;
    public Integer lockTime = 0; // Raw locktime. Non-0 value also locktime-activates inputs
    public Boolean replaceable = false; // Marks this transaction as BIP125-replaceable. Allows this transaction to be replaced by a transaction with higher fees. If provided, it is an error if explicit sequence numbers are incompatible.

    // Response
    public String transaction; // hex string of the transaction

    public CreateRawTransaction() {super(NAME);}

    public CreateRawTransaction(List<TXI> inputs, List<UTXO> outputs) {
        super(NAME);
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public CreateRawTransaction(List<TXI> inputs, List<UTXO> outputs, Integer lockTime, Boolean replaceable) {
        super(NAME);
        this.inputs = inputs;
        this.outputs = outputs;
        this.lockTime = lockTime;
        this.replaceable = replaceable;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(inputs);
        params.add(outputs);
        params.add(lockTime);
        params.add(replaceable);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            transaction = (String)m.get("transaction");
        }
    }
}
