import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/*
 * 
 * By Gavriliuc Dina 2016
 * 
 */

public class Graphics extends JPanel implements ActionListener {
  static private final String newline = "\n";
  private String fileforEdit; 
  private String fileName, fileOutput;
  private HTMLtoXHTML convertToXHTML=new HTMLtoXHTML();
  private HTMLtoXML convertToXML=new HTMLtoXML();
  
  JButton openButton, convertButton1, convertButton2,editTag;
  
  JTextArea log;

  JFileChooser fc;

  public Graphics() {
    super(new BorderLayout());

    //Create the log first, because the action listeners
    //need to refer to it.
    log = new JTextArea(5, 20);
    log.setMargin(new Insets(5, 5, 5, 5));
    log.setEditable(false);
    
    
    JScrollPane logScrollPane = new JScrollPane(log);

    //Create a file chooser
    fc = new JFileChooser();

    //Uncomment one of the following lines to try a different
    //file selection mode. The first allows just directories
    //to be selected (and, at least in the Java look and feel,
    //shown). The second allows both files and directories
    //to be selected. If you leave these lines commented out,
    //then the default mode (FILES_ONLY) will be used.
    //
    //fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    //fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    //Create the open button. We use the image from the JLF
    //Graphics Repository (but we extracted it from the jar).
    openButton = new JButton("Open a File...");
    openButton.addActionListener(this);

    //Create the save button. We use the image from the JLF
    //Graphics Repository (but we extracted it from the jar).
    convertButton1 = new JButton("Convert file with JTidy ...");
    convertButton1.addActionListener(this);
    
    convertButton2 = new JButton("Convert file with HTMLCleaner ...");
    convertButton2.addActionListener(this);
    
    editTag = new JButton("Edit Tags ...");
    editTag.addActionListener(this);

    //For layout purposes, put the buttons in a separate panel
    JPanel buttonPanel = new JPanel(); //use FlowLayout
    buttonPanel.add(openButton);
    buttonPanel.add(convertButton1);
    buttonPanel.add(convertButton2);
    buttonPanel.add(editTag);

    //Add the buttons and the log to this panel.
    add(buttonPanel, BorderLayout.PAGE_START);
    add(logScrollPane, BorderLayout.CENTER);
  }

  public void actionPerformed(ActionEvent e) {

    //Handle open button action.
    if (e.getSource() == openButton) {
      int returnVal = fc.showOpenDialog(Graphics.this);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        //This is where a real application would open the file.
        log.append("Opening: " + file.getName() + "." + newline);
        fileName=file.getName();
        fileforEdit=file.getPath();
        
        //System.out.println(fileName);
      } else {
        log.append("Open command cancelled by user." + newline);
      }
      log.setCaretPosition(log.getDocument().getLength());

      //Handle save button action.
    } else if (e.getSource() == convertButton1) {
    	
    	 fileOutput= convertToXHTML.Convert(fileforEdit, fileName);
    	
    	log.append("File converted with JTidy saved : " + fileOutput + newline);
    } else if (e.getSource() == convertButton2) {
    	
    	try {
			fileOutput=convertToXML.Convert(fileforEdit, fileName);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	log.append("File converted with HTMLCleaner saved : " + fileOutput + newline);
    }else if (e.getSource()==editTag){
    	
    	TagsEditor editT=new TagsEditor();
    	try {
			editT.readFromFileAndEdit(fileforEdit);
		} catch (XPathExpressionException | DOMException | ParserConfigurationException | SAXException | IOException
				| TransformerFactoryConfigurationError | TransformerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	log.append("Tags editet. Commands taken from file: " + fileName + newline);
    	
    	/*TagsEditorGraphics userIn=new TagsEditorGraphics();
    	 javax.swing.SwingUtilities.invokeLater(new Runnable() {
 	        public void run() {
 	            userIn.createAndShowGUI();
 	        }
 	    });
 	    */
    }
  }

  /**
   * Create the GUI and show it.
   */
  private static void createAndShowGUI() {
    //Make sure we have nice window decorations.
    JFrame.setDefaultLookAndFeelDecorated(true);
    JDialog.setDefaultLookAndFeelDecorated(true);

    //Create and set up the window.
    JFrame frame = new JFrame("HTMLParser");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.
    JComponent newContentPane = new Graphics();
    newContentPane.setOpaque(true); //content panes must be opaque
    frame.setContentPane(newContentPane);

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }
}