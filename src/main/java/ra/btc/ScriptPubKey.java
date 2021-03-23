package ra.btc;

import java.util.List;

public class ScriptPubKey {
    public enum Type {pubkeyhash}
    public String asm;
    public String hex;
    public Integer reqSigs;
    public Type type;
    public List<String> addresses;

}
