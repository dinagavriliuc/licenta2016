import org.w3c.tidy.Tidy;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.lang.model.element.Element;


import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.w3c.dom.Document;


/*
 * 
 * By Gavriliuc Dina 2016
 * 
 */

public class HTMLtoXHTML {


	public String createOutputFilename(String filename){

		int i=0;
		String output="";

		while(filename.charAt(i)!='.')
		{
			output=output+filename.charAt(i);
			i++;
		}

		return output+".xhtml";
	}

	public String Convert(String filePath, String filename){

		String output=createOutputFilename(filename);
		try{
			FileInputStream FIS=new FileInputStream(filePath);

			//FileOutputStream FOS=new FileOutputStream("tidy.xhtml");   
			Tidy tidy=new Tidy();
			tidy.setInputEncoding("UTF-8");
			tidy.setOutputEncoding("UTF-8");
			tidy.setWraplen(Integer.MAX_VALUE);
			tidy.setMakeClean( true );
			tidy.setEncloseBlockText(true);
			//tidy.setLowerLiterals(true);
			//tidy.setQuoteMarks(false);
			//tidy.setQuoteAmpersand(false);
			//tidy.setQuoteNbsp(false);
			//tidy.setFixUri(true);

			tidy.setXmlOut(true);
			tidy.setSmartIndent(true);
			tidy.setForceOutput(true);
			//tidy.parse(FIS,FOS);
			Document xmlDoc = tidy.parseDOM(FIS, null);
			tidy.pprint(xmlDoc,new FileOutputStream(output));
		}
		catch (java.io.FileNotFoundException e)
		{System.out.println(e.getMessage());}   

		return output;
	}

}

