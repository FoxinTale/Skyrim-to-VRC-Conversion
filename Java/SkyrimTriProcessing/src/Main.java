import java.io.PrintStream;
import GUI.CustomOutputStream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static PrintStream standardOut; // This sets the outputs.

    public static void main(String[] args) {
        // Creating the custom output stream.
        PrintStream printStream = new PrintStream(new CustomOutputStream(GUI.consoleOutput));
        standardOut = System.out;
        System.setOut(printStream);
        System.setErr(printStream);

        GUI.createGUI();
    }
}