package ra.btc;

import java.util.List;

public class MempoolEntry {
    public String transactionId;
    public Integer size;
    public Integer modifiedFee;
    public Integer time;
    public Integer height;
    public Integer descendantCount;
    public Integer descendantSize;
    public Integer descendantFees;
    public Integer ancestorCount;
    public Integer ancestorSize;
    public Integer ancestorFees;
    public String wtxid;
    public Integer feesBase;
    public Integer feesModified;
    public Integer feesAncestor;
    public Integer feesDescendant;
    public List<String> depends;
    public List<String> spentBy;
    public Boolean bip125Replaceable;

}
