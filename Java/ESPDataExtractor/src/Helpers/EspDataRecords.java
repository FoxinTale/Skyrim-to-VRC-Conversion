package Helpers;

import ESP.EspFile;
import EspRecords.*;

import java.util.ArrayList;
import java.util.HashMap;

public class EspDataRecords {
    public ArrayList<TextureSet> textureSets;
    public ArrayList<ArmorAddon> armorAddons;
    public ArrayList<Armor> armors;
    public ArrayList<HeadPart> headParts;
    public ArrayList<Race> races;

    public HashMap<Long, TextureSet> textureSetsById;
    public HashMap<Long, ArmorAddon> armorAddonsById;
    public HashMap<Long, Armor> armorsById;
    public HashMap<Long, Race> racesById;
    public ArrayList<FormList> formLists;
    public HashMap<Long, FormList> formListsById;

    public EspDataRecords(EspFile esp) {
        textureSets = TextureSet.fromEsp(esp);
        armorAddons = ArmorAddon.fromEsp(esp);
        armors = Armor.fromEsp(esp);
        headParts = HeadPart.fromEsp(esp);
        races = Race.fromEsp(esp);

        textureSetsById = TextureSet.mapByFormId(textureSets);
        armorAddonsById = ArmorAddon.mapByFormId(armorAddons);
        armorsById = Armor.mapByFormId(armors);
        racesById = Race.mapByFormId(races);
        formLists = FormList.fromEsp(esp);
        formListsById = FormList.mapByFormId(formLists);

        linkRecords();
    }

    private void linkRecords() {
        for (Armor armor : armors) {
            armor.linkArmorAddons(armorAddonsById);
        }
    }
}
