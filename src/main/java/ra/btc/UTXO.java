package ra.btc;

public class UTXO {
    public String bestBlock; // The hash of the block at the tip of the chain
    public Integer confirmations; // The number of confirmations
    public Double value; // The transaction value in BTC
    public ScriptPublicKey scriptPubKey;
    public Boolean coinbase; // Coinbase or not
}
