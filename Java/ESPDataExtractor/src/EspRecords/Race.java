package EspRecords;

import ESP.EspFile;
import ESP.EspRecord;
import ESP.EspSubrecord;

import java.util.ArrayList;
import java.util.HashMap;

public class Race {
    private final EspRecord record;

    public String editorId;
    public String name;
    public ArrayList<String> modelPaths = new ArrayList<String>();
    public long skinFormId;

    public Race(EspRecord record) {
        this.record = record;
        parse();
    }

    public static ArrayList<Race> fromEsp(EspFile esp) {
        ArrayList<Race> result = new ArrayList<Race>();

        for (EspRecord raw : esp.getRecordsByType("RACE")) {
            result.add(new Race(raw));
        }

        return result;
    }

    private void parse() {
        for (EspSubrecord sub : record.subrecords) {
            if ("EDID".equals(sub.type)) {
                editorId = sub.asString();
            } else if("ANAM".equals(sub.type)){
                modelPaths.add(sub.asString());
            } else if ("WNAM".equals(sub.type) && sub.data.length == 4) {
                skinFormId = readUInt32LE(sub.data, 0);
            } else if ("FULL".equals(sub.type)) {
                name = sub.asString();
            }

        }
    }

    public long getFormId() {
        return record.formId;
    }

    public EspRecord getRawRecord() {
        return record;
    }

    public void dumpSubrecords() {
        System.out.println("RACE 0x" + Long.toHexString(getFormId()) + " " + safe(editorId));

        for (EspSubrecord sub : record.subrecords) {
            System.out.print("  " + sub.type + " size=" + sub.size);

            if (isStringSubrecord(sub.type)) {
                System.out.print(" value=" + sub.asString());
            } else if (sub.size == 4) {
                System.out.print(" u32=0x" + Long.toHexString(readUInt32LE(sub.data, 0)));
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
                || "DESC".equals(type);
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
        return value == null ? "" : value;
    }


    public static HashMap<Long, Race> mapByFormId(
            ArrayList<Race> records
    ) {
        java.util.HashMap<Long, Race> map =
                new java.util.HashMap<Long, Race>();

        for (Race record : records) {
            map.put(record.getFormId(), record);
        }

        return map;
    }
}
