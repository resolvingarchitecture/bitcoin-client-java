package ra.btc;

import ra.common.JSONSerializable;
import ra.util.JSONParser;
import ra.util.JSONPretty;

import java.util.Map;

/**
 * An object with output descriptor and metadata
 */
public class ScanObject implements JSONSerializable {
    public String desc; // (string, required) An output descriptor
    public Integer range = 1000; // (numeric or array, optional, default=1000) The range of HD chain indexes to explore (either end or [begin,end])

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }

    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(this.toMap()), 4);
    }

    public void fromJSON(String json) {
        this.fromMap((Map)JSONParser.parse(json));
    }
}
