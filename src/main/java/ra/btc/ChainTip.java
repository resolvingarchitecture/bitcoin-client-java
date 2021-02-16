package ra.btc;

public class ChainTip {
    public Integer height;
    public String hash;
    public Integer branchLength;
    public String status; // active, valid-fork, valid-headers, headers-only, invalid
}
