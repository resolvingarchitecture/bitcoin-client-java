package ra.btc.blockchain;

import java.util.ArrayList;
import java.util.List;

/**
 * Transfer of Satoshis from one address to another address including the minting of new Satoshis.
 *
 */
public class Transaction {

    private long version;
    private List<TransactionInput> inputs;
    private List<TransactionOutput> outputs;

    public Transaction() {
        version = 1;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public void addInput(TransactionInput input) {
        inputs.add(input);
    }

    public void addOutput(TransactionOutput output) {
        outputs.add(output);
    }
}
