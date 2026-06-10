import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class GUI {

    public static File triFile;
    public static File nifFile;
    public static File outputFolder;
    static JTextArea consoleOutput = new JTextArea();
    static JScrollPane scroll = new JScrollPane(consoleOutput);

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

        consoleOutput.setLineWrap(true);
        window.getContentPane().add(scroll);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setBorder(new LineBorder(Color.black, 1, true));

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
        scroll.setBounds(25, 175, 550, 240);
        //Next elements: 175, 210, 245

        ActionListener triButtonEvent = e -> {
            JFileChooser triFileSelect = new JFileChooser();

            triFileSelect.setFileFilter(
                    new FileNameExtensionFilter(
                            "TRI Files (*.tri)",
                            "tri"
                    )
            );
            triFileSelect.setAcceptAllFileFilterUsed(false);
            int triOption = triFileSelect.showOpenDialog(null);
            if(triOption == JFileChooser.APPROVE_OPTION){
                triFileText.setText(triFileSelect.getSelectedFile().getAbsolutePath());
                triFile = triFileSelect.getSelectedFile();
            }
        };

        ActionListener nifButtonEvent = e -> {
            JFileChooser nifFileSelect = new JFileChooser();

            nifFileSelect.setFileFilter(
                    new FileNameExtensionFilter(
                            "NIF Files (*.nif)",
                            "nif"
                    )
            );

            nifFileSelect.setAcceptAllFileFilterUsed(false);
            int nifOption = nifFileSelect.showOpenDialog(null);

            if(nifOption == JFileChooser.APPROVE_OPTION){
                nifFileText.setText(nifFileSelect.getSelectedFile().getAbsolutePath());
                nifFile = nifFileSelect.getSelectedFile();
            }
        };

        ActionListener outputButtonEvent = e -> {
            JFileChooser outputFolderSelect = new JFileChooser();

            outputFolderSelect.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            outputFolderSelect.setAcceptAllFileFilterUsed(false);

            int folderOption = outputFolderSelect.showOpenDialog(null);


            if(folderOption == JFileChooser.APPROVE_OPTION){
                outputFolderText.setText(outputFolderSelect.getSelectedFile().getAbsolutePath());
                outputFolder = outputFolderSelect.getSelectedFile();
            }
        };




        ActionListener extractEvent = e ->{
            if(nifFile != null & triFile != null & outputFolder != null){
                extractButton.setEnabled(false);
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        Extractor.extractTriFile(triFile, nifFile, outputFolder);
                        return null;
                    }

                    @Override
                    protected void done() {
                        extractButton.setEnabled(true);

                        try {
                            get();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                worker.execute();
            } else{
                if(triFile == null){
                    System.out.println(" You need to set a TRI file");
                }

                if(nifFile == null){
                    System.out.println(" You need to set a NIF file");
                }

                if(outputFolder == null){
                    System.out.println(" You need to set an output folder.");
                }
            }

        };



        triFileButton.addActionListener(triButtonEvent);
        nifFileButton.addActionListener(nifButtonEvent);
        outputFolderButton.addActionListener(outputButtonEvent);
        extractButton.addActionListener(extractEvent);

        //Setting a font so it's slightly bigger than the default.

        triFileLabel.setFont(f);
        nifFileLabel.setFont(f);
        outputFolderLabel.setFont(f);
        extractButton.setFont(f);


        triFileText.setEditable(false);
        nifFileText.setEditable(false);
        outputFolderText.setEditable(false);
        consoleOutput.setEditable(false);

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
        window.add(scroll);

        window.setSize(640, 480);
        window.setResizable(false);
        window.setLayout(null);// using no layout managers
        window.setVisible(true);// making the frame visible
    }
}
