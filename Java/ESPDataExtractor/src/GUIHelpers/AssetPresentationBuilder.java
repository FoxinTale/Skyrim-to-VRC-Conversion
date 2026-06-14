package GUIHelpers;

import EspRecords.Armor;
import EspRecords.Race;
import Helpers.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class AssetPresentationBuilder {

    public static ArrayList<SelectableAssetItem> build(EspDataRecords db) {
        ArrayList<SelectableAssetItem> items =
                new ArrayList<SelectableAssetItem>();

        for (Race race : db.races) {
            RacialAssetsBundle bundle =
                    RacialAssetsCollector.collect(race, db);

            items.add(new SelectableAssetItem(
                    "Race",
                    safeName(race.name, race.editorId),
                    race.editorId,
                    bundle.nifPaths,
                    bundle.triPaths,
                    bundle.texturePaths
            ));
        }

        for (Armor armor : db.armors) {
            ArmorAssetBundle bundle =
                    ArmorAssetsCollector.collect(armor, db);

            items.add(new SelectableAssetItem(
                    "Armor",
                    safeName(armor.name, armor.editorId),
                    armor.editorId,
                    bundle.nifPaths,
                    new LinkedHashSet<String>(),
                    bundle.texturePaths
            ));
        }

        return items;
    }

    private static String safeName(String name, String editorId) {
        if (name != null && name.length() > 0) {
            return name;
        }

        if (editorId != null && editorId.length() > 0) {
            return editorId;
        }

        return "(Unnamed)";
    }
}
