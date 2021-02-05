package ra.btc.rpc.blockchain;

import ra.btc.BlockHeader;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetBlockHeader extends RPCRequest {

    public static final String NAME = "getblockheader";

    // Request
    public String blockhash;
    public Boolean verbose = true;

    // Response
    public BlockHeader blockHeader;

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
        blockHeader = new BlockHeader();
        blockHeader.hash = (String)m.get("hash");
        blockHeader.confirmations = (Integer)m.get("confirmations");
        blockHeader.height = (Integer)m.get("height");
        blockHeader.version = (Integer)m.get("version");
        blockHeader.versionHex = (String)m.get("versionHex");
        blockHeader.merkleroot = (String)m.get("merkleroot");
        blockHeader.time = (Long)m.get("time");
        blockHeader.mediantime = (Long)m.get("mediantime");
        blockHeader.nonce = (Integer)m.get("nonce");
        blockHeader.bits = (String)m.get("bits");
        blockHeader.difficulty = (Double)m.get("difficulty");
        blockHeader.chainwork = (String)m.get("chainwork");
        blockHeader.nTx = (Integer)m.get("nTx");
        blockHeader.previousblockhash = (String)m.get("previousblockhash");
        blockHeader.nextblockhash = (String)m.get("nextblockhash");
    }
}
