import ESP.EspFile;
import EspRecords.*;
import GUIHelpers.AssetPathGroup;
import GUIHelpers.AssetPathGrouper;
import GUIHelpers.AssetPresentationBuilder;
import GUIHelpers.SelectableAssetItem;
import Helpers.*;
import Json.JsonObject;
import Json.JsonParser;
import Json.JsonValue;
import RaceMenu.*;
import Readers.EspReader;
import Readers.RaceMenuPresetReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static Helpers.RacialAssets.buildRaceAssetReport;


//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws Exception {

        JsonParser parser = new JsonParser();

     //   JsonValue rootValue = parser.parse(new File("Aileen V3.jslot"));
        RaceMenuPreset preset =
                RaceMenuPresetReader.read(new File("Aileen V3.jslot"));

//        JsonObject root = rootValue.asObject();

        System.out.println("Mods:");
        for (String mod : preset.modNames) {
            System.out.println("  " + mod);
        }

        System.out.println("Headparts:");
        for (RaceMenuHeadPart hp : preset.headParts) {
            System.out.println("  " + hp.formIdentifier +
                    " plugin=" + hp.pluginName +
                    " local=0x" + Long.toHexString(hp.localFormId) +
                    " type=" + hp.type);
        }

        System.out.println("Face textures:");
        for (RaceMenuFaceTexture tex : preset.faceTextures) {
            System.out.println("  [" + tex.index + "] " + tex.texture);
        }

        System.out.println("Body morphs:");
        for (RaceMenuBodyMorph morph : preset.bodyMorphs) {
            System.out.println("  " + morph.name);

            for (RaceMenuBodyMorphKey key : morph.keys) {
                System.out.println("    " + key.key + " = " + key.value);
            }
        }

/*        EspFile esp = EspReader.read(new File("Crimes against Nature.esm"));
        EspDataRecords record = new EspDataRecords(esp);

        ArrayList<SelectableAssetItem> items =
                AssetPresentationBuilder.build(record);
        for (SelectableAssetItem item : items) {
            ArrayList<AssetPathGroup> nifGroups =
                    AssetPathGrouper.groupByFolder(item.nifPaths, 2);

            System.out.println("NIF groups:");
            for (AssetPathGroup group : nifGroups) {
                System.out.println("  " + group.groupName + " (" + group.size() + ")");
            }

        }*/

    }


    private static void printRaceReport(RacialAssets report) {
        Race race = report.race;

        System.out.println("Race: " + race.name + " / " + race.editorId);

        System.out.println("NIFs:");
        for (String path : report.nifPaths) {
            System.out.println("  " + path);
        }

        System.out.println("TRIs:");
        for (String path : report.triPaths) {
            System.out.println("  " + path);
        }

        System.out.println("Textures:");
        for (String path : report.texturePaths) {
            System.out.println("  " + path);
        }

        System.out.println();
    }

}
