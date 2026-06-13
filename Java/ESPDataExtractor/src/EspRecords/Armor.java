package EspRecords;

import ESP.EspFile;
import ESP.EspRecord;
import ESP.EspSubrecord;

import java.util.ArrayList;
import java.util.HashMap;

public class Armor {
    private final EspRecord record;
    public ArrayList<Long> armorAddonFormIds = new ArrayList<Long>();

    public String editorId;
    public String name;

    public String worldModel;
    public String maleWorldModel;
    public String femaleWorldModel;

    public Armor(EspRecord record) {
        this.record = record;
        parse();
    }


    public static ArrayList<Armor> fromEsp(EspFile esp) {
        ArrayList<Armor> result =
                new ArrayList<Armor>();

        for (EspRecord raw : esp.getRecordsByType("ARMO")) {
            result.add(new Armor(raw));
        }

        return result;
    }

    private void parse() {
        for (EspSubrecord sub : record.subrecords) {
            if ("EDID".equals(sub.type)) {
                editorId = sub.asString();
            } else if ("FULL".equals(sub.type)) {
                name = sub.asString();
            } else if ("MOD2".equals(sub.type)) {
                maleWorldModel = sub.asString();
            } else if ("MOD4".equals(sub.type)) {
                femaleWorldModel = sub.asString();
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
        System.out.println("Armor");
        System.out.println("  Form ID:   0x" + Long.toHexString(getFormId()));
        System.out.println("  Editor ID: " + safe(editorId));

        if (name != null && name.length() > 0) {
            System.out.println("  Name:      " + name);
        }

        if (worldModel != null && worldModel.length() > 0) {
            System.out.println("  Model:     " + worldModel);
        }

        System.out.println();
    }

    public void dumpSubrecords() {
        System.out.println("ARMO 0x" + Long.toHexString(getFormId()) + " " + safe(editorId));

        for (EspSubrecord sub : record.subrecords) {
            System.out.print("  " + sub.type + " size=" + sub.size);

            if (isStringSubrecord(sub.type)) {
                System.out.print(" value=" + sub.asString());
            } else if (sub.size == 4) {
                long value = readUInt32LE(sub.data, 0);
                System.out.print(" u32=0x" + Long.toHexString(value));
            } else {
                System.out.print(" hex=" + toHex(sub.data, 32));
            }

            System.out.println();
        }

        System.out.println();
    }

    private boolean looksLikeString(EspSubrecord sub) {
        return "EDID".equals(sub.type)
                || "FULL".equals(sub.type)
                || "MODL".equals(sub.type)
                || "ICON".equals(sub.type)
                || "MICO".equals(sub.type);
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }

        return value;
    }

    private boolean isStringSubrecord(String type) {
        return "EDID".equals(type)
                || "FULL".equals(type)
                || "MODL".equals(type)
                || "ICON".equals(type)
                || "MICO".equals(type);
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


    public void linkArmorAddons(HashMap<Long, ArmorAddon> armorAddonsById) {
        armorAddonFormIds.clear();

        for (EspSubrecord sub : record.subrecords) {
            if (sub.data.length % 4 != 0) {
                continue;
            }

            for (int i = 0; i < sub.data.length; i += 4) {
                long possibleFormId = readUInt32LE(sub.data, i);

                if (armorAddonsById.containsKey(possibleFormId)) {
                    if (!armorAddonFormIds.contains(possibleFormId)) {
                        armorAddonFormIds.add(possibleFormId);
                    }
                }
            }
        }
    }

    public static HashMap<Long, Armor> mapByFormId(
            ArrayList<Armor> records
    ) {
        HashMap<Long, Armor> map = new HashMap<Long, Armor>();

        for (Armor record : records) {
            map.put(record.getFormId(), record);
        }

        return map;
    }
    
}