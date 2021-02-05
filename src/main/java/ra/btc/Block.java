package ra.btc;

import java.util.List;

public class Block {
    public String hash;
    public int confirmations;
    public long blocksize;
    public long blocksizeStripped; // excluding witness data
    public int blockweight;
    public long blockheight;
    public int blockversion;
    public String merkleroot;
    public List<String> tx;
    public long time;
    public long mediantime;
    public long nonce;
    public String bits;
    public Double difficulty;
    public String chainwork;
    public long nTx;
    public String previousblockhash;
    public String nextblockhash;
}
