package ra.btc.packet;

/**
 * Used in response to {@link InvPacket}, to retrieve the content of a specific object,
 * and is usually sent after receiving an {@link InvPacket}, after filtering known elements.
 * It can be used to retrieve transactions, but only if they are in the memory pool
 * or relay set - arbitrary access to transactions in the chain is not allowed to avoid
 * having clients start to depend on nodes having full transaction indexes
 * (which modern nodes do not).
 *
 */
public class GetDataPacket extends BitcoinPacket {

    public static int MAX_ENTRIES = 50000;


}
