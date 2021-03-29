package ra.btc;

public class ScriptVerificationError {
    public String txid; // The hash of the referenced, previous transaction
    public Integer vOut; // The index of the output to spent and used as input
    public String scriptSig; // The hex-encoded signature script
    public Integer n; // Script sequence number
    public String error; // Verification or signing error related to the input
}
