package ra.btc.rpc.blockchain;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetBlockHeader extends RPCRequest {

    public static final String NAME = "getblockheader";

    // Request
    public String blockhash;
    public Boolean verbose = true;

    // Response
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

    public GetBlockHeader(String blockhash) {
        super(NAME);
        this.blockhash = blockhash;
    }

    public GetBlockHeader(String blockhash, Boolean verbose) {
        super(NAME);
        this.blockhash = blockhash;
        this.verbose = verbose;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(blockhash);
        params.add(verbose);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        hash = (String)m.get("hash");
        confirmations = (Integer)m.get("confirmations");
        height = (Integer)m.get("height");
        version = (Integer)m.get("version");
        versionHex = (String)m.get("versionHex");
        merkleroot = (String)m.get("merkleroot");
        time = (Long)m.get("time");
        mediantime = (Long)m.get("mediantime");
        nonce = (Integer)m.get("nonce");
        bits = (String)m.get("bits");
        difficulty = (Double)m.get("difficulty");
        chainwork = (String)m.get("chainwork");
        nTx = (Integer)m.get("nTx");
        previousblockhash = (String)m.get("previousblockhash");
        nextblockhash = (String)m.get("nextblockhash");
    }
}
