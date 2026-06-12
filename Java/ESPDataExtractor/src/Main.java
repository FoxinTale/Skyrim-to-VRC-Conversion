import ESP.EspFile;
import ESP.EspRecord;
import EspRecords.*;
import Readers.EspReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {

        EspFile esp = EspReader.read(new File("Survival Gear.esp"));


        ArrayList<TextureSet> textureSets = TextureSet.fromEsp(esp);
        ArrayList<ArmorAddon> armorAddons = ArmorAddon.fromEsp(esp);
        HashMap<Long, ArmorAddon> armorAddonsById = ArmorAddon.mapByFormId(armorAddons);

        ArrayList<Armor> armors = Armor.fromEsp(esp);




        HashMap<Long, TextureSet> textureSetsById = TextureSet.mapByFormId(textureSets);


        for (ArmorAddon addon : armorAddons) {
            printSlot(addon.femaleWorld, textureSetsById);
        }

        for (Armor armor : armors) {
            armor.linkArmorAddons(armorAddonsById);

            System.out.println(armor.name + " / " + armor.editorId);

            for (Long addonId : armor.armorAddonFormIds) {
                ArmorAddon addon = armorAddonsById.get(addonId);

                System.out.println("  Addon: " + addon.editorId);


                for (Long txstId : addon.textureSetFormIds) {
                    TextureSet txst = textureSetsById.get(txstId);

                    System.out.println("    TextureSet: " + txst.editorId);

                    if (txst.diffuse != null) {
                        System.out.println("      Diffuse: " + txst.diffuse);
                    }

                    if (txst.normal != null) {
                        System.out.println("      Normal: " + txst.normal);
                    }
                }
            }

            System.out.println();
        }
    }

    private static void printSlot(
            ModelSlot slot,
            HashMap<Long, TextureSet> textureSetsById
    ) {
        if (slot.modelPath == null && slot.alternateTextures.size() == 0) {
            return;
        }

        System.out.println("  " + slot.label + ": " + slot.modelPath);

        for (AlternateTexture alt : slot.alternateTextures) {
            TextureSet txst = textureSetsById.get(alt.textureSetFormId);

            System.out.println("    Alternate Texture: " + alt.name3d);
            System.out.println("      Index: " + alt.index);

            if (txst != null) {
                System.out.println("      Texture Set: " + txst.editorId);
                System.out.println("      Diffuse: " + txst.diffuse);
                System.out.println("      Normal: " + txst.normal);
            } else {
                System.out.println("      Texture Set FormID: 0x" +
                        Long.toHexString(alt.textureSetFormId));
            }
        }
    }
}
