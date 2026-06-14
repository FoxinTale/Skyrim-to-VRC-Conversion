package GUIHelpers;

import java.util.Set;

public class SelectableAssetItem {
    public String category; // "Race" or "Armor"
    public String displayName;
    public String editorId;

    public Set<String> nifPaths;
    public Set<String> triPaths;
    public Set<String> texturePaths;

    public SelectableAssetItem(
            String category,
            String displayName,
            String editorId,
            Set<String> nifPaths,
            Set<String> triPaths,
            Set<String> texturePaths
    ) {
        this.category = category;
        this.displayName = displayName;
        this.editorId = editorId;
        this.nifPaths = nifPaths;
        this.triPaths = triPaths;
        this.texturePaths = texturePaths;
    }

    public int getTotalAssetCount() {
        return nifPaths.size() + triPaths.size() + texturePaths.size();
    }

    @Override
    public String toString() {
        return category + ": " + displayName + " (" + editorId + ")";
    }
}
