package ra.btc;

import java.util.List;

public class Transaction {
    public String txid;
    public String hash;
    public Integer size;
    public Integer vsize;
    public Integer weight;
    public Integer locktime;
    public List<TXI> vIn;
    public List<UTXO> vOut;
}
