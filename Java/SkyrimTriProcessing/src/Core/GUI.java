package Core;

import Lang.Localisation;
import Lang.Strings;
import Tri.TriType;
import Tri.TriTypes;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import static Core.Utils.stripExtension;

public class GUI extends Component {
    private static final Strings strings = Localisation.getStrings();
    public static String versionNumber = "1.0.0";
    public static File triFile;
    public static File nifFile;
    public static File outputFolder;
    public static JTextField outputFolderText = new JTextField();
    public static JTextArea consoleOutput = new JTextArea();
    static JScrollPane scroll = new JScrollPane(consoleOutput);

    public static void createGUI() {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JFrame.setDefaultLookAndFeelDecorated(true);

        Font f = new Font("serif", Font.PLAIN, 18);
        window.setTitle(strings.appTitle() + versionNumber);

        JLabel modeLabel = new JLabel(strings.modeSelect());
        JRadioButton faceGenMode = new JRadioButton(strings.faceGenButton());
        JRadioButton bodyslideMode = new JRadioButton(strings.bodyslideButton(), true);

        JLabel triFileLabel = new JLabel(strings.selectTri());
        JTextField triFileText = new JTextField();
        JButton triFileButton = new JButton(strings.openButton());

        JLabel nifFileLabel = new JLabel(strings.selectNif());
        JTextField nifFileText = new JTextField();
        JButton nifFileButton = new JButton(strings.openButton());

        JLabel outputFolderLabel = new JLabel(strings.selectOutput());
        JButton outputFolderButton = new JButton(strings.openButton());

        JButton extractButton = new JButton(strings.run());

        consoleOutput.setLineWrap(true);
        window.getContentPane().add(scroll);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setBorder(new LineBorder(Color.black, 1, true));

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(bodyslideMode);
        modeGroup.add(faceGenMode);

        // Dealing with the Core.GUI elements
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
            if (triOption == JFileChooser.APPROVE_OPTION) {
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

            if (nifOption == JFileChooser.APPROVE_OPTION) {
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


            if (folderOption == JFileChooser.APPROVE_OPTION) {
                outputFolderText.setText(outputFolderSelect.getSelectedFile().getAbsolutePath());
                outputFolder = outputFolderSelect.getSelectedFile();
            }
        };


        ActionListener extractEvent = e -> {
            if (faceGenMode.isSelected()) {
                try {
                    runExtractor(extractButton, null, triFile, true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                try {
                    runExtractor(extractButton, nifFile, triFile, false);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };


        faceGenMode.addActionListener(e -> {
            nifFileText.setEnabled(false);
            nifFileText.setText("");
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
        bodyslideMode.setToolTipText(strings.bodyslideModeTooltip());
        faceGenMode.setToolTipText(strings.facegenModeTooltip());
        triFileText.setToolTipText(strings.triFileTextTooltip());
        nifFileText.setToolTipText(strings.nifFileTextTooltip());
        outputFolderText.setToolTipText(strings.outputFolderTextTooltip());
        consoleOutput.setToolTipText(strings.consoleWindowTooltip());

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

    public static void runExtractor(JButton extractButton, File nifFile, File triFile, boolean isFaceGen) throws IOException {
        if (triFile == null) {
            JOptionPane.showMessageDialog(
                    null,
                    strings.selectTriFileMessage(),
                    strings.selectTriFileTitle(),
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if(nifFile == null && !isFaceGen){
            JOptionPane.showMessageDialog(
                    null,
                    strings.selectNifFileMessage(),
                    strings.selectNifFileTitle(),
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }


        TriType detectedType = TriTypes.detect(triFile);


        if (isFaceGen && detectedType != TriType.FACEGEN) {
            showTypeMismatchWarning("FaceGen", detectedType);
            return;
        }
        if (!isFaceGen && detectedType != TriType.BODYSLIDE) {
            showTypeMismatchWarning("BodySlide", detectedType);
            return;
        }

        if(nifFile != null){
            if (!confirmPossibleMismatch(triFile, nifFile)) {
                return;
            }
        }


        File outputFolder = determineOutputFolder(triFile);
        if (!confirmOutputFolder(outputFolder)) {
            return;
        }

        if (!outputFolder.exists()) {
            if (!outputFolder.mkdirs()) {
                JOptionPane.showMessageDialog(
                        null,
                        strings.outputFolderErrorMessage()
                                + outputFolder.getAbsolutePath(),
                        strings.outputFolderErrorTitle(),
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        }

        extractButton.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (isFaceGen) {
                    Extractor.extractFaceGenTriFile(triFile, outputFolder);
                } else {
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
    }

    private static void showTypeMismatchWarning(String selectedMode, TriType detectedType) {
        JOptionPane.showMessageDialog(
                null,
                strings.triTypeMismatch01()
                        + strings.triTypeMismatch02() + selectedMode + "\n"
                        + strings.triTypeMismatch03() + detectedType + "\n\n"
                        + strings.triTypeMismatch04(),
                strings.triTypeMismatchTitle(),
                JOptionPane.WARNING_MESSAGE
        );
    }


    public static boolean confirmPossibleMismatch(File triFile, File nifFile) {
        String triBase = stripExtension(triFile.getName());
        String nifBase = stripExtension(nifFile.getName());

        String normalizedTri = Utils.normalizeMeshName(triBase);
        String normalizedNif = Utils.normalizeMeshName(nifBase);

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

    private static File determineOutputFolder(File triFile) {
        String text = outputFolderText.getText();

        if (text != null && !text.trim().isEmpty()) {
            return new File(text.trim());
        }

        String triBaseName = stripExtension(triFile.getName());

        return new File(
                triFile.getParentFile(),
                triBaseName
        );
    }

    private static boolean confirmOutputFolder(File outputFolder) {
        if (!outputFolder.exists()) {
            return true;
        }

        File[] files = outputFolder.listFiles();

        if (files == null || files.length == 0) {
            return true;
        }

        int result = JOptionPane.showConfirmDialog(
                null,
                "The output folder already contains files.\n\n"
                        + outputFolder.getAbsolutePath()
                        + "\n\n"
                        + "Existing files may be overwritten.\n\n"
                        + "Continue?",
                "Output Folder Not Empty",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        return result == JOptionPane.YES_OPTION;
    }



}
