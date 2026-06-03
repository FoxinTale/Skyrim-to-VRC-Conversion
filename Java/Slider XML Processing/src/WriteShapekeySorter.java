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

    public static void printSliderSorter(Map<String, ArrayList<Slider>> xmlData, ArrayList<String> dividers, ArrayList<String> keys){
        String tab = "\u0009";

        System.out.println("import bpy");
        System.out.println("# A Blender script to easily sort the shapekeys of an outfit, or bodytype when porting from Skyrim");
        System.out.println("\n");
        System.out.println("# Dividers section");


    }




    // Yeah, we're literally writing Python with Java. Imagine a snake coming out of a coffee cup. ...caffinated boop noodle.
    public static void printSorter(ArrayList<String> categoryNames, ArrayList<ArrayList<Map<String, String>>> sliderSets) {
        String tab = "\u0009";
        String varName;
        ArrayList<Map<String,String>> tempList;
        Map<String, ArrayList<Map<String, String>>> sliderCategories = new TreeMap<>();

        for(int a = 0; a < categoryNames.size(); a++){
            sliderCategories.put(categoryNames.get(a),sliderSets.get(a));
        }

        ArrayList<String> keysList = new ArrayList<>();
        ArrayList<String> dividers = new ArrayList<>();
        ArrayList<String> lists = new ArrayList<>();

  //      categoryNames.add("Other");

        for(String key: sliderCategories.keySet()){
            dividers.add("DIV_" + key.replaceAll("\\s+", "").toUpperCase());
        }
//        dividers.add("DIV_OTHER");

        for(int b = 0; b < sliderSets.size(); b++){
            varName = categoryNames.get(b);
            keysList.add(varName.replaceAll("\\s+", "").toUpperCase() + "_KEYS");
        }

        System.out.println("import bpy");
        System.out.println("# A Blender script to easily sort the shapekeys of an outfit, or bodytype when porting from Skyrim");
        System.out.println("\n");
        System.out.println("# Dividers section");

        for(String key: sliderCategories.keySet()){
            System.out.println("DIV_" + key.replaceAll("\\s+", "").toUpperCase() + " = \"----- " + key + " -----\"");
        }

        System.out.println("\n");
        System.out.println("# The actual, important blendshape keys table.");


        System.out.println();

   //     for(int i = 0; i < sliderCategories.size(); i++){

   //     }

 /*
        for(int d = 0; d < sliderSets.size(); d++){
            keys = new ArrayList<>();
            tempList = sliderSets.get(d);
            System.out.println(keysList.get(d) + " = [");

            for(int e = 0; e < tempList.size(); e++){
                keys.add(tempList.get(e).keySet().toString().replace("[", "").replace("]", "").trim());
            }
            Collections.sort(keys);

            for(int m = 0; m < keys.size(); m ++){
                System.out.println(tab + "\"" + keys.get(m) + "\",");
            }
            System.out.println("]");
            System.out.println();
        }
  */
        System.out.println();

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
        
        for(int f = 0; f < dividers.size(); f++){
            System.out.println("ensure_divider(obj, " + dividers.get(f) + ")" );
        }
        System.out.println();


        System.out.println("# Building the lists.");
        for(int g = 0; g < categoryNames.size(); g ++){
            varName = categoryNames.get(g);
            lists.add(varName.replaceAll("\\s+", "").toLowerCase());
        }


        for(int h = 0; h < lists.size() - 1; h++){
            System.out.println(
                    lists.get(h) + " = unique_existing(obj, " + keysList.get(h) + ")"
            );
        }

        System.out.println();
        System.out.println("# What to exclude from everything else we want.");
        System.out.println("divider_set = {");
        for(int i = 0; i < dividers.size(); i++){
            System.out.println(
                    tab + "norm(" + dividers.get(i) + "),"
            );
        }
        System.out.println("}");
        System.out.println();

        StringBuilder sb = new StringBuilder();
        for(int j = 0; j < lists.size() - 1; j++){
            if(j < lists.size() - 2){
                sb.append(lists.get(j) + " + ");
            } else{
                sb.append(lists.get(j));
            }
        }
        System.out.println("listed_set = {norm(n) for n in " + sb.toString() + "}");
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

        for(int k = 0; k < dividers.size() - 1; k++){
            System.out.println("# " + categoryNames.get(k));
            System.out.println("target = move_name_to_index(obj, " + dividers.get(k) + ", target)");
            System.out.println("for name in " + lists.get(k) + ":");
            System.out.println(tab + "target = move_name_to_index(obj, name, target)");
            System.out.println();
        }
        System.out.println();
        System.out.println("print(\"Done: created dividers and reorganized shape keys.\")");
    }
}
