package Readers;

import Json.*;
import RaceMenu.*;

import java.io.File;

public class RaceMenuPresetReader {

    public static RaceMenuPreset read(File file) throws Exception {
        JsonParser parser = new JsonParser();
        JsonValue rootValue = parser.parse(file);
        JsonObject root = rootValue.asObject();

        RaceMenuPreset preset = new RaceMenuPreset();

        readActor(root, preset);
        readModNames(root, preset);
        readHeadParts(root, preset);
        readFaceTextures(root, preset);
        readBodyMorphs(root, preset);

        return preset;
    }

    private static void readActor(JsonObject root, RaceMenuPreset preset) {
        JsonValue actorValue = root.get("actor");

        if (actorValue == null || actorValue.type != JsonValue.Type.OBJECT) {
            return;
        }

        JsonObject actor = actorValue.asObject();

        preset.actor.hairColor = getLong(actor, "hairColor", 0);
        preset.actor.headTexture = getString(actor, "headTexture", null);
        preset.actor.weight = getDouble(actor, "weight", 0.0);
    }

    private static void readModNames(JsonObject root, RaceMenuPreset preset) {
        JsonValue value = root.get("modNames");

        if (value == null || value.type != JsonValue.Type.ARRAY) {
            return;
        }

        JsonArray array = value.asArray();

        for (int i = 0; i < array.size(); i++) {
            JsonValue item = array.get(i);

            if (item.type == JsonValue.Type.STRING) {
                preset.modNames.add(item.asString());
            }
        }
    }

    private static void readHeadParts(JsonObject root, RaceMenuPreset preset) {
        JsonValue value = root.get("headParts");

        if (value == null || value.type != JsonValue.Type.ARRAY) {
            return;
        }

        JsonArray array = value.asArray();

        for (int i = 0; i < array.size(); i++) {
            JsonValue item = array.get(i);

            if (item.type != JsonValue.Type.OBJECT) {
                continue;
            }

            JsonObject obj = item.asObject();

            long formId = getLong(obj, "formId", 0);
            String formIdentifier = getString(obj, "formIdentifier", null);
            int type = (int) getLong(obj, "type", -1);

            preset.headParts.add(new RaceMenuHeadPart(
                    formId,
                    formIdentifier,
                    type
            ));
        }
    }

    private static void readFaceTextures(JsonObject root, RaceMenuPreset preset) {
        JsonValue value = root.get("faceTextures");

        if (value == null || value.type != JsonValue.Type.ARRAY) {
            return;
        }

        JsonArray array = value.asArray();

        for (int i = 0; i < array.size(); i++) {
            JsonValue item = array.get(i);

            if (item.type != JsonValue.Type.OBJECT) {
                continue;
            }

            JsonObject obj = item.asObject();

            int index = (int) getLong(obj, "index", -1);
            String texture = getString(obj, "texture", null);

            preset.faceTextures.add(new RaceMenuFaceTexture(index, texture));
        }
    }

    private static String getString(JsonObject obj, String key, String defaultValue) {
        JsonValue value = obj.get(key);

        if (value == null || value.type != JsonValue.Type.STRING) {
            return defaultValue;
        }

        return value.asString();
    }

    private static long getLong(JsonObject obj, String key, long defaultValue) {
        JsonValue value = obj.get(key);

        if (value == null || value.type != JsonValue.Type.NUMBER) {
            return defaultValue;
        }

        return value.asLong();
    }

    private static double getDouble(JsonObject obj, String key, double defaultValue) {
        JsonValue value = obj.get(key);

        if (value == null || value.type != JsonValue.Type.NUMBER) {
            return defaultValue;
        }

        return value.asDouble();
    }

    private static void readBodyMorphs(JsonObject root, RaceMenuPreset preset) {
        JsonValue value = root.get("bodyMorphs");

        if (value == null || value.type != JsonValue.Type.ARRAY) {
            return;
        }

        JsonArray array = value.asArray();

        for (int i = 0; i < array.size(); i++) {
            JsonValue item = array.get(i);

            if (item.type != JsonValue.Type.OBJECT) {
                continue;
            }

            JsonObject obj = item.asObject();

            String name = getString(obj, "name", null);

            if (name == null || name.length() == 0) {
                continue;
            }

            RaceMenuBodyMorph morph = new RaceMenuBodyMorph(name);

            JsonValue keysValue = obj.get("keys");

            if (keysValue != null && keysValue.type == JsonValue.Type.ARRAY) {
                JsonArray keys = keysValue.asArray();

                for (int k = 0; k < keys.size(); k++) {
                    JsonValue keyItem = keys.get(k);

                    if (keyItem.type != JsonValue.Type.OBJECT) {
                        continue;
                    }

                    JsonObject keyObj = keyItem.asObject();

                    String key = getString(keyObj, "key", null);
                    double morphValue = getDouble(keyObj, "value", 0.0);

                    if (key != null && key.length() > 0) {
                        morph.keys.add(new RaceMenuBodyMorphKey(
                                key,
                                morphValue
                        ));
                    }
                }
            }

            preset.bodyMorphs.add(morph);
        }
    }
}
