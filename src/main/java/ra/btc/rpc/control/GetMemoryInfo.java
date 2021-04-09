package ra.btc.rpc.control;

import ra.btc.Memory;
import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetMemoryInfo extends RPCRequest {
    public static final String NAME = "getmemoryinfo";

    public enum MODE {stats, mallocinfo}

    // Request
    public GetMemoryInfo.MODE mode = GetMemoryInfo.MODE.stats; // determines what kind of information is returned. “stats” returns general statistics about memory usage in the daemon. “mallocinfo” returns an XML string describing low-level heap state (only available if compiled with glibc 2.10+).

    // Response
    public Memory memory;
    public String mallocInfo;

    public GetMemoryInfo() {}

    public GetMemoryInfo(MODE mode) {
        this.mode = mode;
    }

    @Override
    public Map<String, Object> toMap() {
        params.add(mode.name());
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            for(Object obj : m.values()) {
                if(mode == MODE.mallocinfo) {
                    mallocInfo = (String)obj;
                } else if(mode == MODE.stats) {
                    Map<String, Object> locked = (Map<String, Object>) obj;
                    memory = new Memory();
                    memory.used = (Integer)locked.get("used");
                    memory.free = (Integer)locked.get("free");
                    memory.total = (Integer)locked.get("total");
                    memory.locked = (Integer)locked.get("locked");
                    memory.chunksUsed = (Integer)locked.get("chunks_used");
                    memory.chunksFree = (Integer)locked.get("chunks_free");
                }
                break;
            }
        }
    }
}
