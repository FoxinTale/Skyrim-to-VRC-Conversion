package Helpers;

import EspRecords.*;

public class RacialAssetsCollector {

    public static RacialAssetsBundle collect(Race race, EspDataRecords db) {
        RacialAssetsBundle bundle = new RacialAssetsBundle(race);

        addRaceAssets(bundle, race);
        addSkinAssets(bundle, race, db);
        addHeadPartAssets(bundle, race, db);
        return bundle;
    }

    private static void addRaceAssets(RacialAssetsBundle bundle, Race race) {
        for (String path : race.modelPaths) {
            bundle.addNif(path);
        }
    }

    private static void addSkinAssets(
            RacialAssetsBundle bundle,
            Race race,
            EspDataRecords db
    ) {
        Armor skin = db.armorsById.get(race.skinFormId);

        if (skin == null) {
            return;
        }

        for (Long addonId : skin.armorAddonFormIds) {
            ArmorAddon addon = db.armorAddonsById.get(addonId);

            if (addon == null) {
                continue;
            }

            addArmorAddonAssets(bundle, addon, db);
        }
    }

    private static void addArmorAddonAssets(
            RacialAssetsBundle bundle,
            ArmorAddon addon,
            EspDataRecords db
    ) {
        for (ModelSlot slot : addon.getModelSlots()) {
            if (!slot.hasData()) {
                continue;
            }

            bundle.addNif(slot.modelPath);

            for (AlternateTexture alt : slot.alternateTextures) {
                TextureSet txst =
                        db.textureSetsById.get(alt.textureSetFormId);

                if (txst != null) {
                    addTextureSetAssets(bundle, txst);
                }
            }
        }
    }

    private static void addHeadPartAssets(
            RacialAssetsBundle bundle,
            Race race,
            EspDataRecords db
    ) {
        for (HeadPart hp : db.headParts) {
            if (!hp.isProbablyForRacePlugin(race, db)) {
                continue;
            }

            bundle.addNif(hp.modelPath);

            for (String tri : hp.triPaths) {
                bundle.addTri(tri);
            }

            TextureSet txst = db.textureSetsById.get(hp.textureSetFormId);

            if (txst != null) {
                addTextureSetAssets(bundle, txst);
            }
        }
    }

    private static void addTextureSetAssets(
            RacialAssetsBundle bundle,
            TextureSet txst
    ) {
        bundle.addTexture(txst.diffuse);
        bundle.addTexture(txst.normal);
        bundle.addTexture(txst.environment);
        bundle.addTexture(txst.glow);
        bundle.addTexture(txst.height);
        bundle.addTexture(txst.detail);
        bundle.addTexture(txst.subsurface);
        bundle.addTexture(txst.backlight);
    }
}
