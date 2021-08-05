package ra.btc.uses;

import java.util.Map;

public abstract class UseRequestBase implements UseRequest {

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public void fromMap(Map<String, Object> map) {

    }

    @Override
    public String toJSON() {
        return null;
    }

    @Override
    public void fromJSON(String s) {

    }
}
