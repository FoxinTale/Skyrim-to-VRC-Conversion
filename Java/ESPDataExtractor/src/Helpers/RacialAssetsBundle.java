package Helpers;

import EspRecords.Race;

import java.util.LinkedHashSet;
import java.util.Set;

public class RacialAssetsBundle {
    public Race race;

    public Set<String> nifPaths = new LinkedHashSet<String>();
    public Set<String> triPaths = new LinkedHashSet<String>();
    public Set<String> texturePaths = new LinkedHashSet<String>();

    public RacialAssetsBundle(Race race) {
        this.race = race;
    }

    public void addNif(String path) {
        addPath(nifPaths, path);
    }

    public void addTri(String path) {
        addPath(triPaths, path);
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
