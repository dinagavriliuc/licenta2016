import java.io.File;
import java.io.IOException;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;


/*
 * 
 * By Gavriliuc Dina 2016
 * 
 */


public class HTMLtoXML{
	
	public String createOutputFilename(String filename){

		int i=0;
		String output="";

		while(filename.charAt(i)!='.')
		{
			output=output+filename.charAt(i);
			i++;
		}

		return output+".xml";
	}
	
	public String Convert(String filePath, String filename) throws IOException{
		
		String output=createOutputFilename(filename);
        //FileInputStream fis = new FileInputStream(filePath);
		
		CleanerProperties props = new CleanerProperties();
		 
		// set some properties to non-default values
		props.setTranslateSpecialEntities(true);
		props.setTransResCharsToNCR(true);
		props.setOmitComments(true);
		 String encoding="UTF-8";
		// do parsing
		 File inputFile=new File(filePath);
		TagNode tagNode = new HtmlCleaner(props).clean(inputFile,encoding);
		 
		// serialize to xml file
		new PrettyXmlSerializer(props).writeToFile(
		    tagNode, output, "utf-8");
		return output;
	}
	
}