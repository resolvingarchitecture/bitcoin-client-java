package ra.btc;

public class UTXO {
    public String txid; // The transaction id
    public Integer index;
    public Integer vout; // the vout value
    public String bestBlock; // The hash of the block at the tip of the chain
    public Integer confirmations; // The number of confirmations
    public Double value; // The transaction value in BTC
    public ScriptPublicKey scriptPubKey; // the script key
    public String desc; // A specialized descriptor for the matched scriptPubKey
    public Boolean coinbase; // Coinbase or not
    public Integer height; // Height of the unspent transaction output
}
