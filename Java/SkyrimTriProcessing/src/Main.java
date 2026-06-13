import java.io.*;

import Core.GUI;
import GUI.CustomOutputStream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Creating the custom output stream.
        PrintStream printStream = new PrintStream(new CustomOutputStream(GUI.consoleOutput));
        // This sets the outputs.
        System.setOut(printStream);
        System.setErr(printStream);

        GUI.createGUI();
    }
}

