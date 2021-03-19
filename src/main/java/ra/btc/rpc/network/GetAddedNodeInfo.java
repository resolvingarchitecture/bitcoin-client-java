package ra.btc.rpc.network;

import ra.btc.BitcoinPeer;
import ra.btc.rpc.RPCRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Returns information about the given added node, or all added nodes (note that onetry addnodes are not listed here)
 */
public class GetAddedNodeInfo extends RPCRequest {

    public static final String NAME = "getaddednodeinfo";

    // Request
    public String node; // If provided, return information about this specific node, otherwise all nodes are returned.
    // Response
    public List<BitcoinPeer> peers;

    public GetAddedNodeInfo() {
        super(NAME);
    }

    public GetAddedNodeInfo(String node) {
        super(NAME);
        this.node = node;
    }

    @Override
    public Map<String, Object> toMap() {
        if(node!=null)
            params.add(node);
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            for(Object obj : m.values()) {
                List<Map<String,Object>> mNodes = (List<Map<String,Object>>)obj;
                BitcoinPeer peer;
                for(Map<String,Object> mNode : mNodes) {
                    peer = new BitcoinPeer();
                    peer.addressOrName = (String)m.get("addednode");
                    peer.connected = "true".equals(m.get("connected"));
                    List<Map<String,Object>> mAddrs = (List<Map<String,Object>>)m.get("addresses");
                    for(Map<String,Object> mAddr : mAddrs){
                        peer.addresses.put((String)mAddr.get("address"), BitcoinPeer.Connection.valueOf((String)mAddr.get("connection")));
                    }
                }
                break;
            }
        }
    }

}
