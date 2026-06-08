import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

public class GUI {

    public static void createGUI(){
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JFrame.setDefaultLookAndFeelDecorated(true);

        Font f = new Font("serif", Font.PLAIN, 18);
        window.setTitle("Tri Extractor - Version 1.0.0");

        JLabel triFileLabel = new JLabel("Select Tri File: ");
        JTextField triFileText = new JTextField();
        JButton triFileButton = new JButton("Open");

        JLabel nifFileLabel = new JLabel("Select Nif File: ");
        JTextField nifFileText = new JTextField();
        JButton nifFileButton = new JButton("Open");

        JLabel outputFolderLabel = new JLabel("Select Output Folder: ");
        JTextField outputFolderText = new JTextField();
        JButton outputFolderButton = new JButton("Open");

        JButton extractButton = new JButton("Extract!");


        // Dealing with the GUI elements
        // Select a reasonable base Y, add the height, plus 5-10. In this case, it's 25, + 10. So add 35.
        triFileLabel.setBounds(25, 35, 250, 25);
        triFileText.setBounds(200, 35, 300, 25);
        triFileButton.setBounds(510, 35, 65, 24);

        nifFileLabel.setBounds(25, 70, 250, 25);
        nifFileText.setBounds(200, 70, 300, 25);
        nifFileButton.setBounds(510, 70, 65, 24);

        outputFolderLabel.setBounds(25, 105, 250, 25);
        outputFolderText.setBounds(200, 105, 300, 25);
        outputFolderButton.setBounds(510, 105, 65, 24);


        extractButton.setBounds(25, 140, 550, 30);
        //Next elements: 175, 210, 245

//        triFileSelect.setBounds(300, 35, 250, 25); // The x padding of 25 plus the width of 25, then 25 more padding.
//        nifFileSelect.setBounds(300, 70, 250, 25);
//        outputFolderSelect.setBounds(300, 105, 250, 25);

        ActionListener triButtonEvent = e -> {
            JFileChooser triFileSelect = new JFileChooser();
            int op = triFileSelect.showOpenDialog(null);
        };

        ActionListener nifButtonEvent = e -> {
            JFileChooser nifFileSelect = new JFileChooser();
            int op = nifFileSelect.showOpenDialog(null);
        };

        ActionListener outputButtonEvent = e -> {
            JFileChooser outputFolderSelect = new JFileChooser();
            int op = outputFolderSelect.showOpenDialog(null);
        };



        triFileButton.addActionListener(triButtonEvent);
        nifFileButton.addActionListener(nifButtonEvent);
        outputFolderButton.addActionListener(outputButtonEvent);

        //Setting a font so it's slightly biger than the default.

        triFileLabel.setFont(f);
        nifFileLabel.setFont(f);
        outputFolderLabel.setFont(f);
        extractButton.setFont(f);


        triFileText.setEditable(false);
        nifFileText.setEditable(false);
        outputFolderText.setEditable(false);

        // Adding our content to the main window.
        window.add(triFileLabel);
        window.add(triFileText);
        window.add(triFileButton);

        window.add(nifFileLabel);
        window.add(nifFileText);
        window.add(nifFileButton);

        window.add(outputFolderLabel);
        window.add(outputFolderText);
        window.add(outputFolderButton);

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
