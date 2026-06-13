package EspRecords;

import ESP.EspFile;
import ESP.EspRecord;
import ESP.EspSubrecord;

import java.util.ArrayList;
import java.util.HashMap;

public class FormList {
    private final EspRecord record;

    public String editorId;
    public ArrayList<Long> formIds = new ArrayList<Long>();

    public FormList(EspRecord record) {
        this.record = record;
        parse();
    }

    public static ArrayList<FormList> fromEsp(EspFile esp) {
        ArrayList<FormList> result = new ArrayList<FormList>();

        for (EspRecord raw : esp.getRecordsByType("FLST")) {
            result.add(new FormList(raw));
        }

        return result;
    }

    public static HashMap<Long, FormList> mapByFormId(
            ArrayList<FormList> records
    ) {
        HashMap<Long, FormList> map =
                new HashMap<Long, FormList>();

        for (FormList record : records) {
            map.put(record.getFormId(), record);
        }

        return map;
    }

    private void parse() {
        for (EspSubrecord sub : record.subrecords) {
            if ("EDID".equals(sub.type)) {
                editorId = sub.asString();

            } else if ("LNAM".equals(sub.type) && sub.data.length == 4) {
                formIds.add(readUInt32LE(sub.data, 0));
            }
        }
    }

    public boolean containsFormId(long formId) {
        return formIds.contains(formId);
    }

    public long getFormId() {
        return record.formId;
    }

    private long readUInt32LE(byte[] data, int offset) {
        return ((long) data[offset] & 0xFF)
                | (((long) data[offset + 1] & 0xFF) << 8)
                | (((long) data[offset + 2] & 0xFF) << 16)
                | (((long) data[offset + 3] & 0xFF) << 24);
    }
}
