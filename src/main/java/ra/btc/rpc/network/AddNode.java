package ra.btc.rpc.network;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

/**
 * Attempts to add or remove a node from the addnode list.
 *
 * Or try a connection to a node once.
 *
 * Nodes added using addnode (or -connect) are protected from DoS disconnection and are not required to
 * be full nodes/support SegWit as other outbound peers are (though such peers will not be synced from).
 */
public class AddNode extends RPCRequest {

    public static final String NAME = "addnode";

    public enum Command {add, remove, onetry}

    // Request
    public String node; // The node (see getpeerinfo for nodes)
    public Command command; // ‘add’ to add a node to the list, ‘remove’ to remove a node from the list, ‘onetry’ to try a connection to the node once

    public AddNode(String node, Command command) {
        super(NAME);
        this.node = node;
        this.command = command;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(node);
        params.add(command.name());
        return super.toMap();
    }

}
