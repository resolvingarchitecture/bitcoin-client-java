package ra.btc;

public class BlockHeader {
    public String hash; // the block hash (same as provided)
    public Integer confirmations; // The number of confirmations, or -1 if the block is not on the main chain
    public Integer height; // The block height or index
    public Integer version; // The block version
    public String versionHex; // The block version formatted in hexadecimal
    public String merkleroot; // The merkle root
    public Long time; // The block time in seconds since epoch (Jan 1 1970 GMT)
    public Long mediantime; // The median block time in seconds since epoch (Jan 1 1970 GMT)
    public Integer nonce; // The nonce
    public String bits; // The bits
    public Double difficulty; // The difficulty
    public String chainwork; // Expected number of hashes required to produce the current chain (in hex)
    public Integer nTx; // The number of transactions in the block.
    public String previousblockhash; // The hash of the previous block
    public String nextblockhash; // The hash of the next block
}
