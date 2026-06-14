package RaceMenu;

import Json.*;

public class RaceMenuPresetInspector {
    public static void inspect(JsonObject root) {
        inspectObject(root, "actor");
        inspectArray(root, "bodyMorphs", 5);
        inspectArray(root, "faceTextures", 5);
        inspectArray(root, "headParts", 10);
        inspectArray(root, "modNames", 20);
        inspectObject(root, "morphs");
        inspectArray(root, "overrides", 5);
        inspectArray(root, "tintInfo", 5);
        inspectObject(root, "version");
    }

    private static void inspectObject(JsonObject root, String key) {
        JsonValue value = root.get(key);

        if (value == null || value.type != JsonValue.Type.OBJECT) {
            return;
        }

        System.out.println(key + ": OBJECT");

        for (String childKey : value.asObject().keys()) {
            JsonValue child = value.asObject().get(childKey);
            System.out.println("  " + childKey + " : " + child.type);
        }

        System.out.println();
    }

    private static void inspectArray(JsonObject root, String key, int maxItems) {
        JsonValue value = root.get(key);

        if (value == null || value.type != JsonValue.Type.ARRAY) {
            return;
        }

        JsonArray array = value.asArray();

        System.out.println(key + ": ARRAY size=" + array.size());

        int count = Math.min(array.size(), maxItems);

        for (int i = 0; i < count; i++) {
            JsonValue item = array.get(i);
            if (item.type == JsonValue.Type.OBJECT) {
                JsonObject obj = item.asObject();

                System.out.println("  [" + i + "] OBJECT");

                for (String childKey : obj.keys()) {
                    JsonValue child = obj.get(childKey);
                    System.out.println("      " + childKey + " : " +
                            child.type + " = " + preview(child));
                }
            } else {
                System.out.println("  [" + i + "] " + item.type +
                        " = " + preview(item));
            }
        }

        System.out.println();
    }

    private static String preview(JsonValue value) {
        if (value == null || value.isNull()) {
            return "null";
        }

        if (value.type == JsonValue.Type.STRING) {
            return "\"" + value.asString() + "\"";
        }

        if (value.type == JsonValue.Type.NUMBER) {
            return String.valueOf(value.value);
        }

        if (value.type == JsonValue.Type.BOOLEAN) {
            return String.valueOf(value.asBoolean());
        }

        if (value.type == JsonValue.Type.OBJECT) {
            return "{...}";
        }

        if (value.type == JsonValue.Type.ARRAY) {
            return "[...]";
        }

        return "";
    }
}
