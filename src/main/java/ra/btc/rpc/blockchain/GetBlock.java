package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCCommand;

import java.util.List;
import java.util.Map;

public class GetBlock extends RPCCommand {

    public static final String NAME = "getblock";

    public enum Verbosity {HEX, JSON, JSONTX}

    // Request
    public String blockhash;
    public Verbosity verbosity;

    // Response
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

    public GetBlock(String blockHash) {
        super(NAME);
        this.blockhash = blockHash;
        this.verbosity = Verbosity.JSON;
    }

    public GetBlock(String blockHash, Verbosity verbosity) {
        super(NAME);
        this.blockhash = blockHash;
        this.verbosity = verbosity;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        params.add(blockhash);
        params.add(verbosity.ordinal()); // 0 = hex, 1 = json, 2 = json with tx data
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        // Response
        if(verbosity==Verbosity.HEX) {

            return;
        } else {
            blockhash = (String) m.get("hash");
            confirmations = (Integer) m.get("confirmations");
            blocksize = (Long) m.get("size");
            blocksizeStripped = (Long) m.get("strippedsize");
            blockweight = (Integer) m.get("weight");
            blockheight = (Long) m.get("height");
            blockversion = (Integer) m.get("version");
            merkleroot = (String) m.get("merkleroot");
            tx = (List<String>)m.get("tx");
            time = (Long)m.get("time");
            mediantime = (Long)m.get("mediantime");
            nonce = (Long)m.get("nonce");
            bits = (String)m.get("bits");
            difficulty = (Double)m.get("difficulty");
            chainwork = (String)m.get("chainwork");
            nTx = (Long)m.get("nTx");
            previousblockhash = (String)m.get("previousblockhash");
            nextblockhash = (String)m.get("nextblockhash");
        }
        if(verbosity==Verbosity.JSONTX) {
            // Pick up raw Tx

        }

    }
}
