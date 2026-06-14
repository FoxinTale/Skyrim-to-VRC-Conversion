package GUIHelpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public class AssetPathGrouper {

    public static ArrayList<AssetPathGroup> groupByFolder(
            Set<String> paths,
            int depth
    ) {
        LinkedHashMap<String, AssetPathGroup> groups =
                new LinkedHashMap<String, AssetPathGroup>();

        for (String path : paths) {
            String groupName = getFolderKey(path, depth);

            AssetPathGroup group = groups.get(groupName);

            if (group == null) {
                group = new AssetPathGroup(groupName);
                groups.put(groupName, group);
            }

            group.paths.add(path);
        }

        return new ArrayList<AssetPathGroup>(groups.values());
    }

    private static String getFolderKey(String path, int depth) {
        if (path == null || path.length() == 0) {
            return "(none)";
        }

        String normalized = path.replace('\\', '/').toLowerCase();

        String[] parts = normalized.split("/");

        if (parts.length <= 1) {
            return "(root)";
        }

        StringBuilder sb = new StringBuilder();

        int folderCount = Math.min(depth, parts.length - 1);

        for (int i = 0; i < folderCount; i++) {
            if (i > 0) {
                sb.append("/");
            }

            sb.append(parts[i]);
        }

        return sb.toString();
    }
}
