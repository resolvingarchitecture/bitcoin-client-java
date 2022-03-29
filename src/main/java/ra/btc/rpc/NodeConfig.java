package ra.btc.rpc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class NodeConfig {

    public static final Logger LOG = Logger.getLogger(NodeConfig.class.getName());

    public String directory;
    public String filename = "bitcoin.conf";
    public boolean regtest = false;
    public boolean testnet = false;
    public boolean server = false;
    public boolean rest = false;
    public String rpcauth;
    public String rpcuser;
    public String rpcpassword;
    public Integer prune;

    public boolean loaded = false;

    public Map<String,Object> mainnetOptions;
    public Map<String,Object> testnetOptions;
    public Map<String,Object> regtestOptions;

    public boolean load(String directory) throws IOException {
        this.directory = directory;
        File configFile = new File(directory+filename);
        if(configFile.exists() && configFile.canRead()) {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            boolean loaded = false;
            reader.lines().forEach(line -> {
                if(!line.isEmpty() && !line.startsWith("#")) {
                    LOG.fine(line);
                    if(line.equals("regtest=1")) { regtest = true; return; }
                    if(line.equals("testnet=1")) { testnet = true; return; }
                    if(line.equals("server=1")) { server = true; return; }
                    if(line.equals("rest=1")) { rest = true; return; }
                    if(line.startsWith("rpcuser=")) { rpcuser = line.substring("rpcuser=".length()); return; }
                    if(line.startsWith("rpcpassword=")) { rpcpassword = line.substring("rpcpassword=".length()); return; }
                    if(line.startsWith("prune=")) { prune = Integer.parseInt(line.substring("prune=".length())); return; }
                    this.loaded = true;
                }
            });
            return this.loaded;
        }
        return false;
    }

    public boolean save() {

        return true;
    }
}
