package GUIHelpers;

import java.util.ArrayList;

public class AssetPathGroup {
    public String groupName;
    public ArrayList<String> paths = new ArrayList<String>();

    public AssetPathGroup(String groupName) {
        this.groupName = groupName;
    }

    public int size() {
        return paths.size();
    }
}
