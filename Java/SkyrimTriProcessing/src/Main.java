import java.io.File;
import java.io.IOException;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        File triFile = new File("torso.tri");
        File nifFile = new File("torso_1.nif");
        File outputFolder = new File("Output");

    //    Extractor.extractTriFile(triFile, nifFile, outputFolder);
        GUI.createGUI();
    }
}