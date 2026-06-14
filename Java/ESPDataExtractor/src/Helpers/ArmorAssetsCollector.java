package Helpers;

import EspRecords.*;

public class ArmorAssetsCollector {


    public static ArmorAssetBundle collect(
            Armor armor,
            EspDataRecords db
    ) {
        ArmorAssetBundle bundle = new ArmorAssetBundle(armor);

        addArmorAssets(bundle, armor, db);

        return bundle;
    }

    private static void addArmorAssets(
            ArmorAssetBundle bundle,
            Armor armor,
            EspDataRecords db
    ) {
        for (Long addonId : armor.armorAddonFormIds) {
            ArmorAddon addon = db.armorAddonsById.get(addonId);

            if (addon == null) {
                continue;
            }

            addArmorAddonAssets(bundle, addon, db);
        }
    }

    private static void addArmorAddonAssets(
            ArmorAssetBundle bundle,
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

    private static void addTextureSetAssets(
            ArmorAssetBundle bundle,
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
