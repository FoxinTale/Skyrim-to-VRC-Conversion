import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");
        File xmlFile = new File("HIMBO.xml");


        Map<String, ArrayList<Slider>> xmlData = ReadXML.readSliderFile(xmlFile);
        printKeyRenamer(xmlData);
 //       ArrayList<String> dividers = WriteShapekeySorter.makeDividers(xmlData);
 //       ArrayList<String> keys = WriteShapekeySorter.makeKeys(xmlData);
 //       WriteShapekeySorter.printSliderSorter(xmlData, dividers, keys);

        System.out.println();
    }

    public static void printKeyRenamer(Map<String, ArrayList<Slider>> xmlData){
        Set<Map.Entry<String, ArrayList<Slider>>> entries;
        entries = xmlData.entrySet();
        ArrayList<Slider> category;
        String tab = "\u0009";

        System.out.println("import bpy");
        System.out.println("# Blender script file to rename skapekeys into something we can make more sense of.");
        System.out.println("# ----------------------------");
        System.out.println("CASE_INSENSITIVE = True");
        System.out.println("ONLY_ACTIVE_OBJECT = True   # False = rename on all selected meshes");
        System.out.println("SKIP_IF_TARGET_EXISTS = True  # don't overwrite if the target name already exists");
        System.out.println();
        System.out.println("RENAME_MAP = {");
        for(Map.Entry<String, ArrayList<Slider>> entry: entries){
            category = entry.getValue();
            Slider slider;

            for(int i = 0; i < category.size(); i++){
                slider = category.get(i);
                System.out.println(tab + "\"" + slider.getName() + "\": \"" + slider.displayName + "\",");
            }
            System.out.println();
        }
        System.out.println("}");
        System.out.println();

    }

}