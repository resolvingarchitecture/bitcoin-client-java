package ra.btc;

import java.util.List;

public class NetworkInfo {
    public Integer version;
    public String subVersion;
    public Integer protocolVersion;
    public String localServices;
    public Boolean localRelay;
    public Integer timeOffset;
    public Integer connections;
    public Boolean networkActive;
    public List<Network> networks;
    public Double relayFee;
    public Double incrementalFee;
    public List<LocalAddress> localAddresses;
    public String warnings;
}
