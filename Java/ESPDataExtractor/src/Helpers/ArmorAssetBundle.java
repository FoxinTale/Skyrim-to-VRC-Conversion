package Helpers;

import EspRecords.Armor;

import java.util.LinkedHashSet;
import java.util.Set;

public class ArmorAssetBundle {

    public Armor armor;

    public Set<String> nifPaths = new LinkedHashSet<String>();
    public Set<String> texturePaths = new LinkedHashSet<String>();

    public ArmorAssetBundle(Armor armor) {
        this.armor = armor;
    }

    public void addNif(String path) {
        addPath(nifPaths, path);
    }

    public void addTexture(String path) {
        addPath(texturePaths, path);
    }

    private void addPath(Set<String> set, String path) {
        if (path == null) {
            return;
        }

        path = path.trim();

        if (path.length() == 0) {
            return;
        }

        set.add(path);
    }
}
