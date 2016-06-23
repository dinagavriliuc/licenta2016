import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class TagsEditorGraphics extends JFrame
{ 
	
	private String oldTag=null,newTag=null,filename=null;

	public void createAndShowGUI() {
	    final String[] labels = {"Old tag: ", "New tag: ", "Filename: "};
	    int labelsLength = labels.length;
	    final JTextField[] textField = new JTextField[labels.length];
	    //Create and populate the panel.
	    JPanel p = new JPanel(new SpringLayout());
	    for (int i = 0; i < labelsLength; i++) {
	        JLabel l = new JLabel(labels[i], JLabel.TRAILING);
	        p.add(l);
	        textField[i] = new JTextField(10);
	        l.setLabelFor(textField[i]);
	        p.add(textField[i]);
	    }
	    JButton button = new JButton("Submit");
	    p.add(new JLabel());
	    p.add(button);

	    //Lay out the panel.
	   SpringUtilities.makeCompactGrid(p,
	                                    labelsLength + 1, 2, //rows, cols
	                                    7, 7,        //initX, initY
	                                    7, 7);       //xPad, yPad

	    button.addActionListener(new ActionListener() {

	        public void actionPerformed(ActionEvent e)
	        {
	           
	                oldTag=textField[0].getText();
	                newTag=textField[1].getText();
	                filename=textField[2].getText();
	                
	                TagsEditor repl=new TagsEditor();
	                try {
						repl.replaceTag(oldTag, newTag, filename);
						System.out.println("Tag successfully replaced !");
					} catch (XPathExpressionException | TransformerFactoryConfigurationError
							| ParserConfigurationException | SAXException | IOException | TransformerException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            
	        }
	    });  
	    //Create and set up the window.
	    JFrame frame = new JFrame("ReplaceTag");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    //Set up the content pane.
	    p.setOpaque(true);  //content panes must be opaque
	    frame.setContentPane(p);

	    //Display the window.
	    frame.pack();
	    frame.setVisible(true);
	}

}