package docsum.ui;

import docsum.summarizer.DocumentSummarizer;
import docsum.summarizer.KeywordExtractor;
import docsum.summarizer.SentencePreprocessor;
import docsum.summarizer.SentenceSegmenter;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import NMF.NMF;

/**
 * Graphical user interface for summarizer program.
 *
 * @author Evan Dempsey
 */
public class GraphicalInterface extends JFrame {

    final Charset ENCODING = StandardCharsets.UTF_8;
    private static final long serialVersionUID = 6253527329314698074L;
    DocumentSummarizer summarizer;
    KeywordExtractor extractor;
    JPanel panel;
    JTextArea sourceTextArea;
    JTextArea keywordTextArea;
    JTextArea summaryTextArea;
    JLabel percentLabel;
    JSlider percentSlider;
    JLabel sourceCharsLabel;
    JLabel sourceWordsLabel;
    JLabel sourceLinesLabel;
    JLabel summaryCharsLabel;
    JLabel summaryWordsLabel;
    JLabel summaryLinesLabel;
    JRadioButton jRadioButton1;
    JRadioButton jRadioButton2;
    ButtonGroup buttonGroup1;
    NMF lsa;

    /**
     * Constructor.
     *
     * @param summarizer	DocumentSummarizer instance.
     * @param extractor	KeywordExtractor instance.
     */
    public GraphicalInterface(DocumentSummarizer summarizer,
            KeywordExtractor extractor) {
        this.summarizer = summarizer;
        this.extractor = extractor;
        initUI();
        try {
            lsa = new NMF();
        } catch (Exception ex) {
            Logger.getLogger(GraphicalInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Sets up the user interface.
     */
    public void initUI() {

        // Set up the menu bar
        JMenuBar menuBar = new JMenuBar();

        // Set up the file menu
        JMenu fileMenu = new JMenu("File");

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setToolTipText("Open a text document.");
        openMenuItem.addActionListener(new OpenActionListener());

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setToolTipText("Save the summarys.");
        saveMenuItem.addActionListener(new SaveActionListener());

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setToolTipText("Exit application.");
        exitMenuItem.addActionListener(new QuitActionListener());

        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(exitMenuItem);

        // Set up the edit menu
        JMenu editMenu = new JMenu("Edit");

        JMenuItem cutMenuItem = new JMenuItem(new DefaultEditorKit.CutAction());
        cutMenuItem.setText("Cut");
        cutMenuItem.setToolTipText("Cut the current selection.");

        JMenuItem copyMenuItem = new JMenuItem(new DefaultEditorKit.CopyAction());
        copyMenuItem.setText("Copy");
        copyMenuItem.setToolTipText("Copy the current selection.");

        JMenuItem pasteMenuItem = new JMenuItem(new DefaultEditorKit.PasteAction());
        pasteMenuItem.setText("Paste");
        pasteMenuItem.setToolTipText("Paste the contents of the clipboard.");

        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);

        // Set up the help menu
        JMenu helpMenu = new JMenu("Help");

        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.setToolTipText("About the document summarizer.");
        aboutMenuItem.addActionListener(new AboutActionListener());

        helpMenu.add(aboutMenuItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        // Make a JScrollPane for the source text and put a JTextArea in it.
        JScrollPane sourcePane = new JScrollPane();
        sourcePane.setAlignmentX(Component.LEFT_ALIGNMENT);
        sourcePane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sourceTextArea = new JTextArea();
        sourceTextArea.setLineWrap(true);
        sourceTextArea.setWrapStyleWord(true);
        sourceTextArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        sourcePane.getViewport().add(sourceTextArea);

        // Make a JScrollPane for the keyword text and put a JTextArea in it.
        JScrollPane keywordPane = new JScrollPane();
        keywordPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        keywordPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        keywordTextArea = new JTextArea();
        keywordTextArea.setLineWrap(true);
        keywordTextArea.setWrapStyleWord(true);
        keywordTextArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        keywordPane.getViewport().add(keywordTextArea);
        
        //herer
        keywordPane.setPreferredSize(new Dimension(
                keywordPane.getMaximumSize().width, 200));
        keywordPane.setMinimumSize(keywordPane.getPreferredSize());
        keywordPane.setMaximumSize(keywordPane.getPreferredSize());


        // Make a JScrollPane for the summary text and put a JTextArea in it.
        JScrollPane summaryPane = new JScrollPane();
        summaryPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        summaryTextArea = new JTextArea();
        summaryTextArea.setLineWrap(true);
        summaryTextArea.setWrapStyleWord(true);
        summaryTextArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        summaryPane.getViewport().add(summaryTextArea);

        // Add document listeners to the source and summary text areas.
        sourceTextArea.getDocument().addDocumentListener(new TextChangeListener());
        sourceTextArea.getDocument().putProperty("name", "source");
        summaryTextArea.getDocument().addDocumentListener(new TextChangeListener());
        summaryTextArea.getDocument().putProperty("name", "summary");

        // Make title labels for the three text areas.
        JLabel sourceTitleLabel = new JLabel("Source");
        sourceTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel keywordTitleLabel = new JLabel("Factorized Data");
        keywordTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel summaryTitleLabel = new JLabel("Optimum Summary");
        summaryTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Make labels for document statistics.
        sourceCharsLabel = new JLabel("Characters: ");
        sourceCharsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sourceWordsLabel = new JLabel("Words: ");
        sourceWordsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sourceLinesLabel = new JLabel("Lines: ");
        sourceLinesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        summaryCharsLabel = new JLabel("Characters: ");
        summaryCharsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryWordsLabel = new JLabel("Words: ");
        summaryWordsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryLinesLabel = new JLabel("Lines: ");
        summaryLinesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Set up two JPanels for the left and right of the centerPanel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Add widgets to the leftPanel
        leftPanel.add(sourceTitleLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(sourcePane);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(sourceCharsLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(sourceWordsLabel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(sourceLinesLabel);

        // Add widgets to the rightPanel
        rightPanel.add(keywordTitleLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(keywordPane);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(summaryTitleLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(summaryPane);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightPanel.add(summaryCharsLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(summaryWordsLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        rightPanel.add(summaryLinesLabel);

        // Set up the center JPanel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(1, 2));

        // Add widgets to the center JPanel
        centerPanel.add(leftPanel, BorderLayout.WEST);
        centerPanel.add(rightPanel, BorderLayout.EAST);

        // Set up the percentage slider
        percentSlider = new JSlider();
        percentSlider.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        percentSlider.setBorder(BorderFactory.createTitledBorder("Range of Summarization"));
        percentSlider.setMajorTickSpacing(10);
        percentSlider.setMinorTickSpacing(5);
        percentSlider.setPaintLabels(true);
        percentSlider.setPaintTicks(true);
        percentSlider.setSnapToTicks(true);
        percentSlider.setValue(50);
        percentSlider.addChangeListener(new SliderChangeListener());

//        jSlider1.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
//        jSlider1.setMajorTickSpacing(10);
//        jSlider1.setMinorTickSpacing(5);
//        jSlider1.setPaintLabels(true);
//        jSlider1.setPaintTicks(true);
//        jSlider1.setSnapToTicks(true);
//        jSlider1.setToolTipText("range");
//        jSlider1.setValue(0);
//        jSlider1.setBorder(javax.swing.BorderFactory.createTitledBorder("Range of Summarization"));

        // Set up the percentage TextField
        percentLabel = new JLabel("50%");
        percentLabel.setText("50%");

        //Create Radio Button

        jRadioButton1 = new JRadioButton("NMF Summarization");
        jRadioButton1.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        jRadioButton1.setBounds(450, 510, 150, 25);

        jRadioButton2 = new JRadioButton("NMF ABC Summarization");
        jRadioButton2.setFont(new java.awt.Font("Century Gothic", 1, 12)); // NOI18N
        jRadioButton2.setBounds(600, 510, 190, 25);

        buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(jRadioButton1);
        buttonGroup1.add(jRadioButton2);

        // Create the summarize button
        JButton summarizeButton = new JButton("Summarize");
        summarizeButton.setBounds(50, 60, 80, 30);
        summarizeButton.setToolTipText("Summarize the document.");
        summarizeButton.addActionListener(new SummarizeActionListener());


        // Set up the bottom JPanel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Add widgets to bottom panel
        bottomPanel.add(percentSlider);
        bottomPanel.add(percentLabel);
        bottomPanel.add(Box.createHorizontalGlue());
       // bottomPanel.add(jRadioButton1);
        bottomPanel.add(jRadioButton2);
        bottomPanel.add(summarizeButton);

        // Set up the main JPanel
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add widgets to main panel
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        setJMenuBar(menuBar);
        add(panel);
        setTitle("Document Summarizer");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Reads a specified file and return its contents as a string
     *
     * @param file	File object.
     * @return	String with file contents.
     */
    public String readFile(File file) {

        StringBuffer fileBuffer = null;
        String fileString = null;
        String line = null;

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            fileBuffer = new StringBuffer();

            while ((line = bufferedReader.readLine()) != null) {
                fileBuffer.append(line).append(
                        System.getProperty("line.separator"));
            }

            fileReader.close();
            fileString = fileBuffer.toString();

        } catch (IOException e) {
            return null;
        }

        return fileString;
    }

    /**
     * Change listener: updates percentage figure in text field in response to
     * slider changes.
     *
     * @author Evan Dempsey
     */
    public class SliderChangeListener implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            int val = source.getValue();
            percentLabel.setText(Integer.toString(val) + "%");
        }
    }

    // Action listeners: executed in response to
    // graphical user interface events.
    /**
     * Takes text from the sourceTextArea and the percentage from the
     * percentSlider and generates a summary and keyword list. Puts the summary
     * into the summaryTextArea and the keyword list into the keywordTextArea.
     *
     * @author Evan Dempsey
     */
    public class SummarizeActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {
                if (sourceTextArea.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(rootPane, "Nothing to summarize");
                    return;
                }
                int percentage = percentSlider.getValue();
                int lc = Integer.parseInt(sourceLinesLabel.getText().split(": ")[1]);
                int lin = (int) ((percentage / 100.0) * lc);

                System.out.println(lin);


                String summary = summarizer.summarize(sourceTextArea.getText(), percentage);
                Summarizer sm = new Summarizer(sourceTextArea.getText(), lin);
                String sumup = sm.Summarize(sourceTextArea.getText(), lin);
                // String keywords = extractor.extract(sumup.trim());

                writeFile("data/document.txt", sourceTextArea.getText());
                String okdata = lsa.trainData("data");


                if (jRadioButton1.isSelected()) {
                    String keywords = extractor.extract(summary.trim());
//                    summaryTextArea.setText(okdata);
                    summaryTextArea.setText(summary);
                    
                    keywordTextArea.setText(okdata);
                   // keywordTextArea.setText(keywords);
                } else if (jRadioButton2.isSelected()) {
                    String keywords = extractor.extract(sumup.trim());
                     summaryTextArea.setText(sumup);
//                    summaryTextArea.append(sumup);
                   
                    keywordTextArea.setText(okdata);
//                    keywordTextArea.setText(keywords);
                    //                sumup = sm.Summarize(sumup, lin);
                    //                keywords = sumup;
                    //                keywords = stripeSpecialChar(keywords);
                    //                keywords = StripRepeatedWord(keywords);
                    //                sumup = sm.Summarize(sumup, lin);
                    //                keywords = sm.coolected;

                } else {
                    JOptionPane.showConfirmDialog(rootPane, "Please Select and algorithm");
                }
            } catch (Exception ex) {
                Logger.getLogger(GraphicalInterface.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Listens for the Open menu item, gets a file from the file chooser dialog,
     * reads its contents, and puts it in the sourceTextArea.
     *
     * @author Evan Dempsey
     */
    public class OpenActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileOpen = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
            fileOpen.setFileFilter(filter);

            int returnValue = fileOpen.showDialog(panel, "Open File");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                // Read the file
                File file = fileOpen.getSelectedFile();
                String text = readFile(file);

                // Put the file contents into the text area
                sourceTextArea.setText(text);
            }
        }
    }

    /**
     * Listens for the Save menu item and saves the summary.
     *
     * @author Evan Dempsey
     */
    public class SaveActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileSave = new JFileChooser();

            int returnValue = fileSave.showDialog(panel, "Save Summary");
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileSave.getSelectedFile();
                try {
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(summaryTextArea.getText());
                    fileWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * Listens for the Exit menu item and exits the application.
     *
     * @author Evan Dempsey
     */
    public class QuitActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    /**
     * Listens for the About menu item and displays dialog.
     *
     * @author Evan Dempsey
     */
    public class AboutActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            // Make information string.
            StringBuilder info = new StringBuilder();
            info.append("<html><body style='width: 200px; text-align: center'>");
            info.append("Document Summarizer<br><br>");
            info.append("Automatic document summarization program by Evan Dempsey.<br><br>");
            info.append("Penn Treebank tokenizer provided by Stanford NLP toolkit.");
            info.append("</body></html>");
            String infoString = info.toString();

            // Create JOptionPane with no icon and custom title.
            JOptionPane.showMessageDialog(panel,
                    infoString,
                    "About Document Summarizer",
                    JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * Listens for changes in documents, recalculates document statistics and
     * updates stats widgets.
     *
     * @author Evan Dempsey
     */
    public class TextChangeListener implements DocumentListener {

        public void changedUpdate(DocumentEvent e) {
            updateStats(e);
        }

        public void insertUpdate(DocumentEvent e) {
            updateStats(e);
        }

        public void removeUpdate(DocumentEvent e) {
            updateStats(e);
        }

        /**
         * Recomputes document statistics and updates widgets.
         *
         * @param e	DocumentEvent.
         */
        private void updateStats(DocumentEvent e) {
            Document doc = (Document) e.getDocument();
            String source = (String) doc.getProperty("name");
            String text = (source.equals("source"))
                    ? sourceTextArea.getText()
                    : summaryTextArea.getText();

            // Stats counter based on wc command sample implementation.
            // http://www.gnu.org/software/cflow/manual/html_node/Source-of-wc-command.html
            int ccount = 0;
            int wcount = 0;
            int lcount = 0;

            int pos = 0;
            while (pos < text.length()) {
                while (pos < text.length()) {
                    if (Character.isLetter(text.charAt(pos))) {
                        wcount++;
                        break;
                    }
                    ccount++;
                    if (text.charAt(pos) == '\n') {
                        lcount++;
                    }
                    pos++;
                }

                while (pos < text.length()) {
                    ccount++;
                    if (text.charAt(pos) == '\n') {
                        lcount++;
                    }

                    if (!Character.isLetter(text.charAt(pos))) {
                        break;
                    }
                    pos++;
                }
                pos++;
            }

            // Put figures into labels.
            if (source.equals("source")) {
                // Update source statistics widgets.
                sourceCharsLabel.setText("Characters: " + ccount);
                sourceWordsLabel.setText("Words: " + wcount);
                sourceLinesLabel.setText("Lines: " + lcount);
            } else {
                // Update summary statistics widgets.
                summaryCharsLabel.setText("Characters: " + ccount);
                summaryWordsLabel.setText("Words: " + wcount);
                summaryLinesLabel.setText("Lines: " + lcount);
            }

        }
    }

    /**
     * Main entry point for application.
     *
     * @param args	Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                // The summarizer and keyword extractor share
                // an instance of the preprocessor and segmenter.
                SentenceSegmenter seg = new SentenceSegmenter();
                SentencePreprocessor prep = new SentencePreprocessor();
                DocumentSummarizer docsum = new DocumentSummarizer(seg, prep);
                KeywordExtractor keyext = new KeywordExtractor(seg, prep);

                GraphicalInterface mainWindow = new GraphicalInterface(docsum, keyext);
                mainWindow.setVisible(true);
            }
        });
    }

    private String StripRepeatedWord(String n1) {
        for (String str : n1.split(" ")) {
            n1 = n1.replaceAll(str, "") + str + " ";
        }
        return n1.trim();
    }

    float testCorpus(String aFileName, String wordA, String wordB) {
        Path path = Paths.get(aFileName);
        String eachLine = "";
        float count = 0;
        try {
            Scanner scanner = new Scanner(path, ENCODING.name());
            while (scanner.hasNextLine()) {
                eachLine = scanner.nextLine() + ":";
                if (eachLine.contains(wordA + ":") && eachLine.contains(wordB + ":")) {
                    //corpusList += eachLine + "\n";
                    count++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    String stripeSpecialChar(String stripeStr) {
        String specialChar = "*.,!?;:%(){}[]\n";
        for (char str : specialChar.toCharArray()) {
            if (stripeStr.contains(str + "")) {
                // punt += str + "  ";
                stripeStr = stripeStr.replace(str + "", "");
            }
        }
        return stripeStr;
    }

    private String compileCompound(String aFileName, String n1) throws IOException {
        Path path = Paths.get(aFileName);
        String eachLine = "";
        String WordN1[] = n1.split(" ");
        String compoundList = "";
        try {
            Scanner scanner = new Scanner(path, ENCODING.name());
            while (scanner.hasNextLine()) {
                eachLine = scanner.nextLine();

                for (int i = 0; i < n1.split(" ").length - 1; i++) {
                    if (eachLine.equalsIgnoreCase(WordN1[i] + WordN1[i + 1])) {
                        n1 = n1.replaceAll(WordN1[i] + " " + WordN1[i + 1], eachLine);
                        compoundList += eachLine + "\t";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return n1;
    }

    void writeFile(String aFileName, String text) throws IOException {
        try {
            PrintWriter writer = new PrintWriter(aFileName, "UTF-8");
            writer.println(text);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
