package Json;

public class JsonValue {
    public enum Type {
        OBJECT,
        ARRAY,
        STRING,
        NUMBER,
        BOOLEAN,
        NULL
    }

    public Type type;
    public Object value;

    public JsonValue(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public JsonObject asObject() {
        return (JsonObject) value;
    }

    public JsonArray asArray() {
        return (JsonArray) value;
    }

    public String asString() {
        return (String) value;
    }

    public double asDouble() {
        return ((Number) value).doubleValue();
    }

    public long asLong() {
        return ((Number) value).longValue();
    }

    public boolean asBoolean() {
        return ((Boolean) value).booleanValue();
    }

    public boolean isNull() {
        return type == Type.NULL;
    }
}
