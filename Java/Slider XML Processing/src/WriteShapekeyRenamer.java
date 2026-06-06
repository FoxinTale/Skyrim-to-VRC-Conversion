import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class WriteShapekeyRenamer {


    public static void printKeyRenamer(Map<String, ArrayList<Slider>> xmlData){
        Set<Map.Entry<String, ArrayList<Slider>>> entries;
        entries = xmlData.entrySet();

        System.out.println("import bpy");
        System.out.println("# Blender script file to rename skapekeys into something we can make more sense of.");
        System.out.println("# ----------------------------");
        System.out.println("CASE_INSENSITIVE = True");
        System.out.println("ONLY_ACTIVE_OBJECT = True   # False = rename on all selected meshes");
        System.out.println("SKIP_IF_TARGET_EXISTS = True  # don't overwrite if the target name already exists");
        System.out.println();

        for(Map.Entry<String, ArrayList<Slider>> entry: entries){
            System.out.println(entry.getKey());
        }

    }
}
