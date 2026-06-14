package Json;

import java.util.LinkedHashMap;
import java.util.Set;

public class JsonObject {
    private LinkedHashMap<String, JsonValue> values =
            new LinkedHashMap<String, JsonValue>();

    public void put(String key, JsonValue value) {
        values.put(key, value);
    }

    public JsonValue get(String key) {
        return values.get(key);
    }

    public boolean has(String key) {
        return values.containsKey(key);
    }

    public Set<String> keys() {
        return values.keySet();
    }
}
