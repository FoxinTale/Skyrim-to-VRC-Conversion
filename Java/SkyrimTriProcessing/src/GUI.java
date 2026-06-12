import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class GUI extends Component {

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
        window.setTitle("Tri Toolkit - Version 1.0.0");

        JLabel modeLabel = new JLabel("Select Mode: ");
        JRadioButton faceGenMode = new JRadioButton("FaceGen Tri File");
        JRadioButton bodyslideMode = new JRadioButton("Bodyslide Tri File", true);

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

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(bodyslideMode);
        modeGroup.add(faceGenMode);

        // Dealing with the GUI elements
        // Select a reasonable base Y, add the height, plus 5-10. In this case, it's 25, + 10. So add 35.
        modeLabel.setBounds(25, 45, 250, 25);
        bodyslideMode.setBounds(200, 45, 200, 25);
        faceGenMode.setBounds(450, 45, 250, 25);

        triFileLabel.setBounds(25, 80, 250, 25);
        triFileText.setBounds(200, 80, 600, 25);
        triFileButton.setBounds(835, 80, 75, 24);

        nifFileLabel.setBounds(25, 115, 250, 25); // 70 y pos
        nifFileText.setBounds(200, 115, 600, 25);
        nifFileButton.setBounds(835, 115, 75, 24);

        outputFolderLabel.setBounds(25, 150, 250, 25); // 105 y pos
        outputFolderText.setBounds(200, 150, 600, 25);
        outputFolderButton.setBounds(835, 150, 75, 24);


        extractButton.setBounds(25, 184, 885, 30);
        scroll.setBounds(25, 220, 885, 240); //175 y pos
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
            triFileSelect.setCurrentDirectory(new File(System.getProperty("user.dir")));

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
            nifFileSelect.setCurrentDirectory(new File(System.getProperty("user.dir")));
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
            outputFolderSelect.setCurrentDirectory(new File(System.getProperty("user.dir")));

            int folderOption = outputFolderSelect.showOpenDialog(null);


            if(folderOption == JFileChooser.APPROVE_OPTION){
                outputFolderText.setText(outputFolderSelect.getSelectedFile().getAbsolutePath());
                outputFolder = outputFolderSelect.getSelectedFile();
            }
        };




        ActionListener extractEvent = e ->{
            if (faceGenMode.isSelected()) {
                runExtractor(extractButton, null, triFile, outputFolder, true);
            } else {
                runExtractor(extractButton, nifFile, triFile, outputFolder, false);
            }

        };


        faceGenMode.addActionListener(e -> {
            nifFileText.setEnabled(false);
            nifFileButton.setEnabled(false);
        });

        bodyslideMode.addActionListener(e -> {
            nifFileText.setEnabled(true);
            nifFileButton.setEnabled(true);
        });


        triFileButton.addActionListener(triButtonEvent);
        nifFileButton.addActionListener(nifButtonEvent);
        outputFolderButton.addActionListener(outputButtonEvent);
        extractButton.addActionListener(extractEvent);

        //Setting a font so it's slightly bigger than the default.

        modeLabel.setFont(f);
        bodyslideMode.setFont(f);
        faceGenMode.setFont(f);
        triFileLabel.setFont(f);
        nifFileLabel.setFont(f);
        outputFolderLabel.setFont(f);

        extractButton.setFont(f);


        triFileText.setEditable(false);
        nifFileText.setEditable(false);
        outputFolderText.setEditable(false);
        bodyslideMode.setToolTipText("Click this button if you're extracting a BodySlide export, or a body mesh. Needs a NIF and a TRI file.");
        faceGenMode.setToolTipText("Click this if you're wanting to extract a facegen related tri file.");
        triFileText.setToolTipText("The path to your tri file, click the open button and navigate to it.");
        nifFileText.setToolTipText("The path to your nif file, click the open button and navigate to it.");
        outputFolderText.setToolTipText("Where the extractor puts all the files. Click the open button to set it.");
        scroll.setToolTipText("This is the big ol' window where any output goes. Progress, extracted files, errors, and more!");

        consoleOutput.setEditable(false);

        // Adding our content to the main window.
        window.add(modeLabel);
        window.add(bodyslideMode);
        window.add(faceGenMode);

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

        window.setSize(960, 520);
        window.setResizable(false);
        window.setLayout(null);// using no layout managers
        window.setVisible(true);// making the frame visible
    }

    public static void runExtractor(JButton extractButton, File nifFile, File triFile, File outputFolder, boolean  isFaceGen){
        if(nifFile != null){
            if (!confirmPossibleMismatch(triFile, nifFile)) {
                return;
            }
        }

        if(triFile != null & outputFolder != null){
            extractButton.setEnabled(false);
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    if(isFaceGen){
                        Extractor.extractFaceGenTriFile(triFile, outputFolder);
                    } else{
                        Extractor.extractBodyslideTriFile(triFile, nifFile, outputFolder);
                    }
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
    }


    public static boolean confirmPossibleMismatch(File triFile, File nifFile) {
        String triBase = stripExtension(triFile.getName());
        String nifBase = stripExtension(nifFile.getName());

        String normalizedTri = normalizeMeshName(triBase);
        String normalizedNif = normalizeMeshName(nifBase);

        if (normalizedTri.equalsIgnoreCase(normalizedNif)) {
            return true;
        }

        int result = JOptionPane.showConfirmDialog(
                null,
                "The TRI and NIF names do not appear to match:\n\n"
                        + "TRI: " + triFile.getName() + "\n"
                        + "NIF: " + nifFile.getName() + "\n\n"
                        + "Continue anyway?",
                "Possible TRI/NIF mismatch",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        return result == JOptionPane.YES_OPTION;
    }


    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');

        if (dot <= 0) {
            return name;
        }

        return name.substring(0, dot);
    }

    private static String normalizeMeshName(String name) {
        return name
                .replaceAll("(?i)_0$", "")
                .replaceAll("(?i)_1$", "")
                .replaceAll("[_\\-\\s]", "")
                .toLowerCase();
    }
}
