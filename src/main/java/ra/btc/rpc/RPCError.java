package ra.btc.rpc;

public class RPCError {
    public Integer code;
    public String message;

    @Override
    public String toString() {
        return code + " : " + message;
    }
}
