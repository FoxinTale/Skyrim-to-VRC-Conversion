package EspRecords;

import ESP.EspFile;
import ESP.EspRecord;
import ESP.EspSubrecord;

import java.util.ArrayList;
import java.util.HashMap;

import static EspRecords.AlternateTexture.parseAlternateTextures;

public class ArmorAddon {
    private final EspRecord record;

    public String editorId;

    public ArrayList<Long> textureSetFormIds = new ArrayList<Long>();

    public String maleModel;
    public String femaleModel;
    public String maleFirstPersonModel;
    public String femaleFirstPersonModel;
    public ModelSlot maleWorld = new ModelSlot("Male World");
    public ModelSlot femaleWorld = new ModelSlot("Female World");
    public ModelSlot maleFirstPerson = new ModelSlot("Male 1st Person");
    public ModelSlot femaleFirstPerson = new ModelSlot("Female 1st Person");

    public ArmorAddon(EspRecord record) {
        this.record = record;
        parse();
    }


    public static ArrayList<ArmorAddon> fromEsp(EspFile esp) {
        ArrayList<ArmorAddon> result = new ArrayList<>();

        for (EspRecord raw : esp.getRecordsByType("ARMA")) {
            result.add(new ArmorAddon(raw));
        }

        return result;
    }

    private void parse() {
        for (EspSubrecord sub : record.subrecords) {
            if ("EDID".equals(sub.type)) {
                editorId = sub.asString();

            } else if ("MOD2".equals(sub.type)) {
                maleWorld.modelPath = sub.asString();
                maleModel = maleWorld.modelPath;

            } else if ("MO2S".equals(sub.type)) {
                maleWorld.alternateTextures = parseAlternateTextures(sub.data);

            } else if ("MOD3".equals(sub.type)) {
                femaleWorld.modelPath = sub.asString();
                femaleModel = femaleWorld.modelPath;

            } else if ("MO3S".equals(sub.type)) {
                femaleWorld.alternateTextures = parseAlternateTextures(sub.data);

            } else if ("MOD4".equals(sub.type)) {
                maleFirstPerson.modelPath = sub.asString();
                maleFirstPersonModel = maleFirstPerson.modelPath;

            } else if ("MO4S".equals(sub.type)) {
                maleFirstPerson.alternateTextures = parseAlternateTextures(sub.data);

            } else if ("MOD5".equals(sub.type)) {
                femaleFirstPerson.modelPath = sub.asString();
                femaleFirstPersonModel = femaleFirstPerson.modelPath;

            } else if ("MO5S".equals(sub.type)) {
                femaleFirstPerson.alternateTextures = parseAlternateTextures(sub.data);
            }
        }
    }

    public long getFormId() {
        return record.formId;
    }


    public static HashMap<Long, ArmorAddon> mapByFormId(ArrayList<ArmorAddon> records) {
        HashMap<Long, ArmorAddon> map = new HashMap<>();

        for (ArmorAddon record : records) {
            map.put(record.getFormId(), record);
        }
        return map;
    }

    public ArrayList<ModelSlot> getModelSlots() {
        ArrayList<ModelSlot> slots = new ArrayList<ModelSlot>();

        slots.add(maleWorld);
        slots.add(femaleWorld);
        slots.add(maleFirstPerson);
        slots.add(femaleFirstPerson);

        return slots;
    }

}
