package ra.btc.uses;

import ra.btc.rpc.RPCRequest;
import ra.btc.rpc.RPCResponse;
import ra.common.JSONSerializable;

public interface UseRequest extends JSONSerializable {
    Boolean additionalRequests();
    RPCRequest nextRequest();
    void handleResponse(RPCRequest request, RPCResponse response);
}
