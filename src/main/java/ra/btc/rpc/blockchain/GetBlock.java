package ra.btc.rpc.blockchain;

import ra.btc.Block;
import ra.btc.rpc.RPCRequest;

import java.util.List;
import java.util.Map;

public class GetBlock extends RPCRequest {

    public static final String NAME = "getblock";

    public enum Verbosity {HEX, JSON, JSONTX}

    // Request
    public String blockhash; // Required
    public Verbosity verbosity; // Optional; options=hex, json, jsontx; default=json

    // Response
    public Block block;

    public GetBlock() {}

    public GetBlock(String blockHash) {
        this.blockhash = blockHash;
        this.verbosity = Verbosity.JSON;
    }

    public GetBlock(String blockHash, Verbosity verbosity) {
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
        block = new Block();
        if(verbosity==Verbosity.HEX) {

            return;
        } else {
            block.hash = (String) m.get("hash");
            block.confirmations = (Integer) m.get("confirmations");
            block.blocksize = (Long) m.get("size");
            block.blocksizeStripped = (Long) m.get("strippedsize");
            block.blockweight = (Integer) m.get("weight");
            block.blockheight = (Long) m.get("height");
            block.blockversion = (Integer) m.get("version");
            block.merkleroot = (String) m.get("merkleroot");
            block.tx = (List<String>)m.get("tx");
            block.time = (Long)m.get("time");
            block.mediantime = (Long)m.get("mediantime");
            block.nonce = (Long)m.get("nonce");
            block.bits = (String)m.get("bits");
            block.difficulty = (Double)m.get("difficulty");
            block.chainwork = (String)m.get("chainwork");
            block.nTx = (Long)m.get("nTx");
            block.previousblockhash = (String)m.get("previousblockhash");
            block.nextblockhash = (String)m.get("nextblockhash");
        }
        if(verbosity==Verbosity.JSONTX) {
            // Pick up raw Tx

        }

    }
}
