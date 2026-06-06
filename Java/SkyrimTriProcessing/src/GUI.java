import javax.swing.*;
import java.awt.*;

public class GUI {

    public static void createGUI(){
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JFrame.setDefaultLookAndFeelDecorated(true);

        Font f = new Font("serif", Font.PLAIN, 18);
        window.setTitle("Tri Extractor - Version 1.0.0");

        JLabel triFileLabel = new JLabel("Select Tri File: ");
        JLabel nifFileLabel = new JLabel("Select Nif File: ");
        JLabel outputFolderLabel = new JLabel("Select Output Folder: ");
        JButton extractButton = new JButton("Extract!");
        JFileChooser triFileSelect = new JFileChooser();
        JFileChooser nifFileSelect = new JFileChooser();
        JFileChooser outputFolderSelect = new JFileChooser();

        //
        // Dealing with the GUI elements
        // Select a reasonable base Y, add the height, plus 5-10. In this case, it's 25, + 10. So add 35.
        triFileLabel.setBounds(25, 35, 250, 25);
        nifFileLabel.setBounds(25, 70, 250, 25);
        outputFolderLabel.setBounds(25, 105, 250, 25);
        extractButton.setBounds(25, 140, 550, 25);
        //Next elements: 175, 210, 245

//        triFileSelect.setBounds(300, 35, 250, 25); // The x padding of 25 plus the width of 25, then 25 more padding.
//        nifFileSelect.setBounds(300, 70, 250, 25);
//        outputFolderSelect.setBounds(300, 105, 250, 25);

        //Setting a font so it's slightly biger than the default.
        triFileLabel.setFont(f);
        nifFileLabel.setFont(f);
        outputFolderLabel.setFont(f);
        extractButton.setFont(f);


        // Adding our content to the main window.
        window.add(triFileLabel);
        window.add(nifFileLabel);
        window.add(outputFolderLabel);
        window.add(extractButton);
//        window.add(triFileSelect);
//        window.add(nifFileSelect);
//        window.add(outputFolderSelect);


        window.setSize(640, 480);
        window.setResizable(false);
        window.setLayout(null);// using no layout managers
        window.setVisible(true);// making the frame visible
    }
}
