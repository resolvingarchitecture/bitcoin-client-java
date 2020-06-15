package ra.btc.packet;

/**
 * Version Send / Reply Request
 *
 * When a node creates an outgoing connection, it will immediately advertise its version.
 * The remote node will respond with its version.
 * No further communication is possible until both peers have exchanged their version.
 *
 */
public class VersionPacket extends BitcoinPacket {

    public static final int SERVICE_NODE_NETWORK = 1;
    public static final int SERVICE_NODE_GETUTXO = 2;
    public static final int SERVICE_NODE_BLOOM = 4;
    public static final int SERVICE_NODE_WITNESS = 8;
    public static final int SERVICE_NODE_NETWORK_LIMITED = 1024;

}
