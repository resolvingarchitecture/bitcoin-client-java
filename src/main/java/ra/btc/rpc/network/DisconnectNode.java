package ra.btc.rpc.network;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Immediately disconnects from the specified peer node.
 *
 * Strictly one out of ‘address’ and ‘nodeid’ can be provided to identify the node.
 *
 * To disconnect by nodeid, either set ‘address’ to the empty string, or call using the named ‘nodeid’ argument only.
 */
public class DisconnectNode extends RPCRequest {

    public static final String NAME = "addnode";

    public enum Command {add, remove, onetry}

    // Request
    public String address; // The IP address:port of the node
    public Integer nodeId; // The node ID (see getpeerinfo for node IDs)

    public DisconnectNode(String address) {
        super(NAME);
        this.address = address;
    }

    public DisconnectNode(Integer nodeId) {
        super(NAME);
        this.address = "";
        this.nodeId = nodeId;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(address);
        if(nodeId!=null)
            params.add(nodeId);
        return super.toMap();
    }

}
