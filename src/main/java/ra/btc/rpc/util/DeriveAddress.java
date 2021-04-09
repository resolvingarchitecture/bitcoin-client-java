package ra.btc.rpc.util;

import ra.btc.rpc.RPCRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Creates a multi-signature address with n signature of m keys required.
 *
 * It returns a json object with the address and redeemScript.
 */
public class DeriveAddress extends RPCRequest {
    public static final String NAME = "deriveaddresses";

    // Request
    public String descriptor;
    public Integer begin;
    public Integer end;

    // Response
    public List<String> addresses;

    public DeriveAddress() {}

    public DeriveAddress(String descriptor) {
        this.descriptor = descriptor;
        this.begin = null;
        this.end = null;
    }

    public DeriveAddress(String descriptor, Integer begin, Integer end) {
        this.descriptor = descriptor;
        this.begin = begin;
        this.end = end;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(descriptor);
        if(end!=null) {
            if(begin!=null)
                params.add(Arrays.asList(begin,end));
            else
                params.add(end);
        }
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            for(Object obj : m.values()) {
                addresses = (List<String>)obj;
                break;
            }
        }
    }
}
