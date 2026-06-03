import java.io.File;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");
        File xmlFile = new File("CBBE 3BA.xml");

 //       ReadXML.readXML(xmlFile);
        Map<String, ArrayList<Slider>> xmlData = ReadXML.readSliderFile(xmlFile);
        ArrayList<String> dividers = WriteShapekeySorter.makeDividers(xmlData);
        ArrayList<String> keys = WriteShapekeySorter.makeKeys(xmlData);
        printSliderSorter(xmlData, dividers, keys);

        System.out.println();
    }

    public static void printSliderSorter(Map<String, ArrayList<Slider>> xmlData, ArrayList<String> dividers, ArrayList<String> keys){
        String tab = "\u0009";
        ArrayList<String> divNames = new ArrayList<>();
        Set<String> names = xmlData.keySet();
        ArrayList<Slider> category;
        Set<Map.Entry<String, ArrayList<Slider>>> entries = new TreeSet<>();

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

        System.out.println();

        int iterator = 0;
        for(Map.Entry<String, ArrayList<Slider>> entry: entries){
            category = entry.getValue();

            System.out.println(keys.get(iterator) + " = [");

            for(Slider slider: category){
                for (ArrayList<Slider> sliders : sliderCategories.values()) {
                    sliders.sort((a, b) -> a.getName().compareTo(b.getName()));
                }
                System.out.println(tab + "\"" + slider.name + "\",");
            }
            System.out.println("]\n");
            iterator += 1;
        }

        System.out.println();


    }
}