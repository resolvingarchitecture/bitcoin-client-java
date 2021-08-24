package ra.btc.uses;

import ra.common.JSONParser;
import ra.common.JSONPretty;

import java.util.Map;

public abstract class UseRequestBase implements UseRequest {

    @Override
    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(toMap()), 4);
    }

    @Override
    public void fromJSON(String s) {
        fromMap((Map<String,Object>)JSONParser.parse(s));
    }
}
