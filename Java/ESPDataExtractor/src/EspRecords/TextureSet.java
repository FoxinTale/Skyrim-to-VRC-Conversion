package EspRecords;

import ESP.EspFile;
import ESP.EspRecord;
import ESP.EspSubrecord;

import java.util.ArrayList;
import java.util.HashMap;

public class TextureSet {
    private EspRecord record;

    public String editorId;

    public String diffuse;       // TX00
    public String normal;        // TX01
    public String environment;   // TX02
    public String glow;          // TX03
    public String height;        // TX04
    public String detail;        // TX05
    public String subsurface;    // TX06
    public String backlight;     // TX07

    public TextureSet(EspRecord record) {
        this.record = record;
        parse();
    }

    private void parse() {
        for (EspSubrecord sub : record.subrecords) {
            if ("EDID".equals(sub.type)) {
                editorId = sub.asString();
            } else if ("TX00".equals(sub.type)) {
                diffuse = sub.asString();
            } else if ("TX01".equals(sub.type)) {
                normal = sub.asString();
            } else if ("TX02".equals(sub.type)) {
                environment = sub.asString();
            } else if ("TX03".equals(sub.type)) {
                glow = sub.asString();
            } else if ("TX04".equals(sub.type)) {
                height = sub.asString();
            } else if ("TX05".equals(sub.type)) {
                detail = sub.asString();
            } else if ("TX06".equals(sub.type)) {
                subsurface = sub.asString();
            } else if ("TX07".equals(sub.type)) {
                backlight = sub.asString();
            }
        }
    }

    public long getFormId() {
        return record.formId;
    }

    public EspRecord getRawRecord() {
        return record;
    }

    public void printReport() {
        System.out.println("Texture Set");
        System.out.println("  Form ID:   0x" + Long.toHexString(getFormId()));
        System.out.println("  Editor ID: " + safe(editorId));

        printTexture("Diffuse", diffuse);
        printTexture("Normal", normal);
        printTexture("Environment", environment);
        printTexture("Glow", glow);
        printTexture("Height", height);
        printTexture("Detail", detail);
        printTexture("Subsurface", subsurface);
        printTexture("Backlight", backlight);

        System.out.println();
    }

    private void printTexture(String label, String path) {
        if (path != null && path.length() > 0) {
            System.out.println("  " + label + ": " + path);
        }
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }

    public static ArrayList<TextureSet> fromEsp(EspFile esp) {
        ArrayList<TextureSet> result =
                new ArrayList<TextureSet>();

        for (EspRecord raw : esp.getRecordsByType("TXST")) {
            result.add(new TextureSet(raw));
        }

        return result;
    }

    public static HashMap<Long, TextureSet> mapByFormId(
            ArrayList<TextureSet> records
    ) {
        HashMap<Long, TextureSet> map =
                new HashMap<Long, TextureSet>();

        for (TextureSet record : records) {
            map.put(record.getFormId(), record);
        }

        return map;
    }
}