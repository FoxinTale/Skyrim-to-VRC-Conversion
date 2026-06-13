package EspRecords;

import ESP.EspFile;
import ESP.EspRecord;
import ESP.EspSubrecord;
import Helpers.EspDataRecords;

import java.util.ArrayList;

public class HeadPart {
    private final EspRecord record;

    public String editorId;
    public String name;
    public String modelPath;
    public int headPartType;
    public long hnamFormId;
    public long textureSetFormId;
    public long raceFormId;

    public ArrayList<String> triPaths = new ArrayList<String>();

    public HeadPart(EspRecord record) {
        this.record = record;
        parse();
    }

    public static ArrayList<HeadPart> fromEsp(EspFile esp) {
        ArrayList<HeadPart> result =
                new ArrayList<HeadPart>();

        for (EspRecord raw : esp.getRecordsByType("HDPT")) {
            result.add(new HeadPart(raw));
        }

        return result;
    }

    private void parse() {
        Long pendingNam0 = null;

        for (EspSubrecord sub : record.subrecords) {
            if ("EDID".equals(sub.type)) {
                editorId = sub.asString();

            } else if ("FULL".equals(sub.type)) {
                name = sub.asString();

            } else if ("MODL".equals(sub.type)) {
                modelPath = sub.asString();

            } else if ("DATA".equals(sub.type) && sub.data.length >= 1) {
                headPartType = sub.data[0] & 0xFF;

            } else if ("HNAM".equals(sub.type) && sub.data.length == 4) {
                hnamFormId = readUInt32LE(sub.data, 0);

            } else if ("TNAM".equals(sub.type) && sub.data.length == 4) {
                textureSetFormId = readUInt32LE(sub.data, 0);

            } else if ("RNAM".equals(sub.type) && sub.data.length == 4) {
                raceFormId = readUInt32LE(sub.data, 0);

            } else if ("NAM0".equals(sub.type) && sub.data.length == 4) {
                pendingNam0 = readUInt32LE(sub.data, 0);

            } else if ("NAM1".equals(sub.type)) {
                triPaths.add(sub.asString());
                pendingNam0 = null;
            }
        }
    }

    public String getHeadPartTypeName() {
        switch (headPartType) {
            case 0: return "Misc";
            case 1: return "Face";
            case 2: return "Eyes";
            case 3: return "Hair";
            case 4: return "Facial Hair";
            case 5: return "Scar";
            case 6: return "Eyebrows";
            default: return "Unknown " + headPartType;
        }
    }

    public boolean isValidForRace(Race race, EspDataRecords db) {
        if (raceFormId == 0) {
            return false;
        }

        // Direct race reference
        if (raceFormId == race.getFormId()) {
            return true;
        }

        // Local FLST reference
        FormList list = db.formListsById.get(raceFormId);

        if (list != null) {
            return list.containsFormId(race.getFormId());
        }

        // Unknown external FLST. Do not include by default.
        return false;
    }

    public long getFormId() {
        return record.formId;
    }

    public EspRecord getRawRecord() {
        return record;
    }

    public void printReport() {
        System.out.println("Head Part");
        System.out.println("  Form ID:   0x" + Long.toHexString(getFormId()));
        System.out.println("  Editor ID: " + safe(editorId));

        if (name != null && name.length() > 0) {
            System.out.println("  Name:      " + name);
        }

        if (modelPath != null && modelPath.length() > 0) {
            System.out.println("  Model:     " + modelPath);
        }

        System.out.println();
    }

    public void dumpSubrecords() {
        System.out.println("HDPT 0x" + Long.toHexString(getFormId()) + " " + safe(editorId));

        for (EspSubrecord sub : record.subrecords) {
            System.out.print("  " + sub.type + " size=" + sub.size);

            if (isStringSubrecord(sub.type)) {
                System.out.print(" value=" + sub.asString());
            } else if (sub.size == 4) {
                long value = readUInt32LE(sub.data, 0);
                System.out.print(" u32=0x" + Long.toHexString(value));
            } else {
                System.out.print(" hex=" + toHex(sub.data, 64));
            }

            System.out.println();
        }

        System.out.println();
    }

    private boolean isStringSubrecord(String type) {
        return "EDID".equals(type)
                || "FULL".equals(type)
                || "MODL".equals(type)
                || "ICON".equals(type);
    }

    private long readUInt32LE(byte[] data, int offset) {
        return ((long) data[offset] & 0xFF)
                | (((long) data[offset + 1] & 0xFF) << 8)
                | (((long) data[offset + 2] & 0xFF) << 16)
                | (((long) data[offset + 3] & 0xFF) << 24);
    }

    private String toHex(byte[] data, int maxBytes) {
        StringBuilder sb = new StringBuilder();

        int count = Math.min(data.length, maxBytes);

        for (int i = 0; i < count; i++) {
            if (i > 0) {
                sb.append(" ");
            }

            String hex = Integer.toHexString(data[i] & 0xFF);

            if (hex.length() == 1) {
                sb.append("0");
            }

            sb.append(hex);
        }

        if (data.length > maxBytes) {
            sb.append(" ...");
        }

        return sb.toString();
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }


    public boolean isProbablyForRacePlugin(Race race, EspDataRecords db) {
        if (isValidForRace(race, db)) {
            return true;
        }

        boolean samePlugin =
                (record.formId >>> 24) == (race.getFormId() >>> 24);

        if (!samePlugin) {
            return false;
        }

        // Some custom headparts have no RNAM.
        if (raceFormId == 0) {
            return true;
        }

        // RNAM points to a master FLST we have not loaded.
        if (!db.formListsById.containsKey(raceFormId)) {
            return true;
        }

        return false;
    }
}