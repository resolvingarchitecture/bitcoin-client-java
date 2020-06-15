package ra.btc.requests;

import ra.common.Request;

public class SendRequest extends Request {
    public String base58From;
    public String base58To;
    public long satoshis;
}
