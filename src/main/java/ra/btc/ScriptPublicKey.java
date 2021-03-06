package ra.btc;

import java.util.List;

public class ScriptPublicKey {

    public enum Type {pubkeyhash}

    public String asm;
    public String hex;
    public Integer reqSigs; // Number of required signatures
    public Type type; // The type eg pubkeyhash
    public List<String> addresses; // array of bitcoin addresses
}
