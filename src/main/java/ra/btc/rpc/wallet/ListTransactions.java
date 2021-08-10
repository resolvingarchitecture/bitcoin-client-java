package ra.btc.rpc.wallet;

import ra.btc.Transaction;
import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ListTransactions extends RPCRequest {

    public static final String NAME = "listtransactions";

    // Request
    public String label = "*";
    public Integer count = 5;
    public Integer skip = 0;

    // Response
    public List<Transaction> txList;

    public ListTransactions() {
        super(NAME);
        path += "/wallet/";
    }

    public ListTransactions(String walletName) {
        super(NAME);
        path += "/wallet/"+walletName;
    }

    @Override
    public Map<String, Object> toMap() {
        // Request
        if(params.size()==0) {
            params.add(label);
            params.add(count);
            params.add(skip);
        }
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.get("txList")!=null) {
            txList = new ArrayList<>();
            List<Map<String,Object>> txListMaps = (List<Map<String,Object>>)m.get("txList");
            Transaction tx;
            for(Map<String,Object> txM : txListMaps) {
                tx = new Transaction();
                tx.fromMap(txM);
                txList.add(tx);
            }
        }
        super.fromMap(m);
    }
}
