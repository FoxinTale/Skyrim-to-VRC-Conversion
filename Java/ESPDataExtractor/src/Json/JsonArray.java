package Json;

import java.util.ArrayList;

public class JsonArray {
    private ArrayList<JsonValue> values =
            new ArrayList<JsonValue>();

    public void add(JsonValue value) {
        values.add(value);
    }

    public JsonValue get(int index) {
        return values.get(index);
    }

    public int size() {
        return values.size();
    }
}
