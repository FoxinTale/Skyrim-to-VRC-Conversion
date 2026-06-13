package Helpers;

import EspRecords.*;

import java.util.ArrayList;

public class RacialAssets {
    public Race race;

    public ArrayList<String> nifPaths = new ArrayList<String>();
    public ArrayList<String> triPaths = new ArrayList<String>();
    public ArrayList<String> texturePaths = new ArrayList<String>();

    public RacialAssets(Race race) {
        this.race = race;
    }

    public void addNif(String path) {
        addUnique(nifPaths, path);
    }

    public void addTri(String path) {
        addUnique(triPaths, path);
    }

    public void addTexture(String path) {
        addUnique(texturePaths, path);
    }

    private void addUnique(ArrayList<String> list, String value) {
        if (value == null || value.length() == 0) {
            return;
        }

        if (!list.contains(value)) {
            list.add(value);
        }
    }


    public static RacialAssets buildRaceAssetReport(
            Race race,
            EspDataRecords db
    ) {
        RacialAssets report = new RacialAssets(race);

        for (String path : race.modelPaths) {
            report.addNif(path);
        }

        Armor skin = db.armorsById.get(race.skinFormId);

        if (skin != null) {
            for (Long addonId : skin.armorAddonFormIds) {
                ArmorAddon addon = db.armorAddonsById.get(addonId);

                if (addon == null) {
                    continue;
                }

                addArmorAddonAssets(report, addon, db);
            }
        }

        for (HeadPart hp : db.headParts) {
            if (!hp.isProbablyForRacePlugin(race, db)) {
                continue;
            }

            report.addNif(hp.modelPath);

            for (String tri : hp.triPaths) {
                report.addTri(tri);
            }

            TextureSet txst = db.textureSetsById.get(hp.textureSetFormId);

            if (txst != null) {
                addTextureSetAssets(report, txst);
            }
        }

        return report;
    }


    private static void addArmorAddonAssets(
            RacialAssets report,
            ArmorAddon addon,
            EspDataRecords db
    ) {
        for (ModelSlot slot : addon.getModelSlots()) {
            if (!slot.hasData()) {
                continue;
            }

            report.addNif(slot.modelPath);

            for (AlternateTexture alt : slot.alternateTextures) {
                TextureSet txst =
                        db.textureSetsById.get(alt.textureSetFormId);

                if (txst != null) {
                    addTextureSetAssets(report, txst);
                }
            }
        }
    }

    private static void addTextureSetAssets(
            RacialAssets report,
            TextureSet txst
    ) {
        report.addTexture(txst.diffuse);
        report.addTexture(txst.normal);
        report.addTexture(txst.environment);
        report.addTexture(txst.glow);
        report.addTexture(txst.height);
        report.addTexture(txst.detail);
        report.addTexture(txst.subsurface);
        report.addTexture(txst.backlight);
    }
}
