package ra.btc;

import java.util.List;

public class TXI {
    public String txid; // Transaction ID in Hex, required
    public Integer vout; // The output number, required
    public ScriptSig scriptSig; // the script
    public List<String> txInWitness; // hex-encoded witness data (if any)
    public Integer sequence; // The sequence number; optional; default=depends on the value of the 'replaceable' and 'locktime' arguments
}
