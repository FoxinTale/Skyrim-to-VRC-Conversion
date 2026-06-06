import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class WriteShapekeySorter {

    public static void writeSorter() throws IOException {
        FileWriter skw = new FileWriter("file.py");

        skw.write("import bpy");
        skw.write("# A Blender script to easily sort the shapekeys of an outfit, or bodytype when porting from Skyrim");
        skw.write("\\n");
        skw.write("# Dividers section");

        skw.close();
    }

    public static ArrayList<String> makeDividers(Map<String, ArrayList<Slider>> xmlData){
        ArrayList<String> dividers = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Slider>> entry : xmlData.entrySet()) {
            dividers.add("DIV_" + entry.getKey().replaceAll("\\s+", "").toUpperCase());
        }
        return dividers;
    }

    public static ArrayList<String> makeKeys(Map<String, ArrayList<Slider>> xmlData){
        ArrayList<String> keys = new ArrayList<>();
        for (Map.Entry<String, ArrayList<Slider>> entry : xmlData.entrySet()) {
           keys.add(entry.getKey().replaceAll("\\s+", "").toUpperCase() + "_KEYS");
        }
        return keys;
    }



    // Yeah, we're literally writing Python with Java. Imagine a snake coming out of a coffee cup. ...caffinated boop noodle.
    public static void printSliderSorter(Map<String, ArrayList<Slider>> xmlData, ArrayList<String> dividers, ArrayList<String> keys){
        String tab = "\u0009";
        ArrayList<String> divNames = new ArrayList<>();
        ArrayList<Slider> category;
        ArrayList<String> sliders;
        ArrayList<String> categories = new ArrayList<>();
        Set<Map.Entry<String, ArrayList<Slider>>> entries;
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, ArrayList<Slider>> entry : xmlData.entrySet()) {
            divNames.add(entry.getKey());
        }
        entries = xmlData.entrySet();
        System.out.println("import bpy");
        System.out.println("# A Blender script to easily sort the shapekeys of an outfit, or bodytype when porting from Skyrim");
        System.out.println("\n");
        System.out.println("# Dividers section");


        for(int a = 0; a < dividers.size(); a++){
            System.out.println(dividers.get(a) + " = \"----- " + divNames.get(a) + " -----\"");
        }
        // We do hardcode this one as this will not change depending on the file we're reading. It's a catch-all.
        System.out.println("DIV_OTHER = \" ----- Other -----\"");

        System.out.println();

        int iterator = 0;
        for(Map.Entry<String, ArrayList<Slider>> entry: entries){
            category = entry.getValue();
            sliders = new ArrayList<>();
            System.out.println(keys.get(iterator) + " = [");

            for(Slider slider: category){
                sliders.add(slider.getName());
            }
            Collections.sort(sliders);

            for (String slider : sliders) {
                System.out.println(tab + "\"" + slider + "\",");
            }
            System.out.println("]\n");
            iterator += 1;
        }

        System.out.println("CASE_INSENSITIVE = True");
        System.out.println("# ----------------------------");
        System.out.println("# Helpers");
        System.out.println("# ----------------------------");
        System.out.println();
        System.out.println("def norm(s: str) -> str:");
        System.out.println(tab + "return s.casefold() if CASE_INSENSITIVE else s");
        System.out.println();
        System.out.println("def ensure_divider(obj, name: str):");
        System.out.println(tab + "# Create a dummy shape key divider if it doesn't exist.");
        System.out.println(tab + "kb = obj.data.shape_keys.key_blocks");
        System.out.println(tab + "if any(norm(k.name) == norm(name) for k in kb):");
        System.out.println(tab + tab + "return");
        System.out.println(tab + "obj.shape_key_add(name=name, from_mix=False)");
        System.out.println();
        System.out.println("def index_map(obj):");
        System.out.println(tab + "kb = obj.data.shape_keys.key_blocks");
        System.out.println(tab + "return {norm(k.name): i for i, k in enumerate(kb)}");
        System.out.println();
        System.out.println("def move_key_to_index(obj, from_index, to_index):");
        System.out.println(tab + "obj.active_shape_key_index = from_index");
        System.out.println(tab + "while obj.active_shape_key_index > to_index:");
        System.out.println(tab + tab + "bpy.ops.object.shape_key_move(type='UP')");
        System.out.println();
        System.out.println("def move_name_to_index(obj, name: str, target_index: int) -> int:");
        System.out.println(tab + "# Move key (if exists) to target_index. Returns next target_index.");
        System.out.println(tab + "m = index_map(obj)");
        System.out.println(tab + "idx = m.get(norm(name))");
        System.out.println(tab + "if idx is None:");
        System.out.println(tab + tab + "return target_index");
        System.out.println(tab + "if idx != target_index:");
        System.out.println(tab + tab + "move_key_to_index(obj, idx, target_index)");
        System.out.println(tab + "return target_index + 1");
        System.out.println();
        System.out.println("def unique_existing(obj, names):");
        System.out.println(tab + "# Return list of names in 'names' that exist on obj, de-duped while preserving order.");
        System.out.println(tab + "kb = obj.data.shape_keys.key_blocks");
        System.out.println(tab + "existing = {norm(k.name) for k in kb}");
        System.out.println(tab + "out = []");
        System.out.println(tab + "seen = set()");
        System.out.println(tab + "for n in names:");
        System.out.println(tab + tab + "nn = norm(n)");
        System.out.println(tab + tab + "if nn in existing and nn not in seen:");
        System.out.println(tab + tab + tab + "out.append(n)");
        System.out.println(tab + tab + tab + "seen.add(nn)");
        System.out.println(tab + "return out");
        System.out.println();
        System.out.println("# ----------------------------");
        System.out.println("# Main Scripty Bits");
        System.out.println("# ----------------------------");
        System.out.println("obj = bpy.context.active_object");
        System.out.println("if not obj or obj.type != \"MESH\" or not obj.data.shape_keys:");
        System.out.println(tab + "raise RuntimeError(\"Select a mesh object with shape keys as the active object.\")");
        System.out.println();
        System.out.println("if bpy.context.mode != 'OBJECT':");
        System.out.println(tab + "bpy.ops.object.mode_set(mode='OBJECT')");
        System.out.println();
        System.out.println("# Ensure Basis is at index 0 (Blender normally does this), but also handle if someone renamed Basis.");
        System.out.println("kb = obj.data.shape_keys.key_blocks");
        System.out.println("if norm(kb[0].name) != norm(\"Basis\"):");
        System.out.println(tab + "pass");
        System.out.println();
        System.out.println("# Create divider keys (if needed)");

        for (String divider : dividers) {
            System.out.println("ensure_divider(obj, " + divider + ")");
        }
        System.out.println("ensure_divider(obj, DIV_OTHER)");

        System.out.println();
        System.out.println();
        for (String key : keys) {
            categories.add(key.toLowerCase().replace("_keys", "").trim());
        }

        if(keys.size() == categories.size()){
            for(int i = 0; i < keys.size(); i++){
                System.out.println(categories.get(i) + " = unique_existing(obj, " + keys.get(i) + ")");
            }
        } else {
            System.out.println("List sizes do not match. (Keys and Categories)");
        }

        System.out.println();
        System.out.println();

        System.out.println("divider_set = {");
        for (String divider : dividers) {
            System.out.println(tab + "norm(" + divider + "),");
        }
        System.out.println(tab + "norm(DIV_OTHER),");
        System.out.println("}");

        System.out.println();
        System.out.println();
        sb.append("listed_set = {norm(n) for n in ");
        for(int i = 0; i < categories.size() - 1; i++){
            sb.append(categories.get(i) + " + ");
        }

        sb.append(categories.get(categories.size() - 1) + "}");

        System.out.println(sb.toString());
        System.out.println("listed_set |= divider_set");
        System.out.println("listed_set.add(norm(\"Basis\"))\n");
        System.out.println();
        System.out.println("# Collect \"everything else\"");
        System.out.println("all_names = [k.name for k in obj.data.shape_keys.key_blocks]");
        System.out.println("everything_else = [n for n in all_names if norm(n) not in listed_set]");

        System.out.println();
        System.out.println("# Now perform moves in one pass from top to bottom");
        System.out.println("target = 1  # index after Basis");
        System.out.println();

        if(dividers.size() == categories.size()){
            for(int i = 0; i < dividers.size(); i++){
                System.out.println("target = move_name_to_index(obj, " + dividers.get(i) + ", target)");
                System.out.println("for name in " + categories.get(i) + ":");
                System.out.println(tab + "target = move_name_to_index(obj, name, target)");
                System.out.println();
            }
        } else {
            System.out.println("List sizes do not match. (Dividers and Categories)");
        }

        System.out.println("target = move_name_to_index(obj, DIV_OTHER, target)");
        System.out.println("for name in everything_else:");
        System.out.println(tab + "target = move_name_to_index(obj, name, target)");
        System.out.println();
        System.out.println("print(\"Done: created dividers and reorganized shape keys.\")");
    }
}
