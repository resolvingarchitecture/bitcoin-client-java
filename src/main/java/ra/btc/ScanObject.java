package ra.btc;

/**
 * An object with output descriptor and metadata
 */
public class ScanObject {
    public String desc; // (string, required) An output descriptor
    public Integer range = 1000; // (numeric or array, optional, default=1000) The range of HD chain indexes to explore (either end or [begin,end])
}
