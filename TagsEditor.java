import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TagsEditor {

	private String attributeValue=null;
	private String attribute=null;
	private String fileEdit;
	private String tagName=null;

	public String constructXPathExp(String oldTag){
		int k=0, aux=0;
		String output="//";

		for(int i=0;i<oldTag.length();i++){
			if(oldTag.charAt(i)==' '){

				if(aux==0){

					output=output+oldTag.substring(k,i);
					tagName=oldTag.substring(k,i);
					k=i+1;
					aux++;
				}else if(aux==1){
					output=output+"[@"+oldTag.substring(k,i)+"=";
					k=i+1;
					aux++;
				}
				else{
					output=output+"\""+oldTag.substring(k,oldTag.length()-1)+"\""+"]";
					break;
				}
			}
		}
		//System.out.println(tagName);
		//System.out.println(output);
		return output;
	}

	public void replaceTag (String oldTag, String newTag, String filename) throws TransformerFactoryConfigurationError, ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException{

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(filename));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		XPathExpression expr = xpath.compile(constructXPathExp(oldTag));

		NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

		for (int i = 0; i < nl.getLength(); i++)
		{
			Node currentItem = nl.item(i);
			String attr = currentItem.getAttributes().getNamedItem(attribute).getNodeValue();

			if(attr.equals(attributeValue)){
				NamedNodeMap attributes = currentItem.getAttributes();
				attributes.removeNamedItem(attribute);
				doc.renameNode(currentItem,null, newTag);
			}
		}
		saveTheXMLFile(filename,doc);

	}

	public void deleteTagWithText(String tag, String filename) throws TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException, XPathExpressionException, SAXException, IOException{

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(filename));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		XPathExpression expr = xpath.compile(constructXPathExp(tag));

		NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node currentItem = nl.item(i);
			Element e=(Element)currentItem;
			String attr = currentItem.getAttributes().getNamedItem(attribute).getNodeValue();

			if(attr.equals(attributeValue))
				e.getParentNode().removeChild(e);
		}
		saveTheXMLFile(filename,doc);
	}

	public void deleteTagWithoutText(String tag,String filename) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException{

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(filename));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		XPathExpression expr = xpath.compile(constructXPathExp(tag));

		NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {

			NodeList children = nodes.item(i).getChildNodes();
			Element el=(Element)children;
			String textNode="";
			Node parent=nodes.item(i).getParentNode();
			textNode=nodes.item(i).getTextContent();

			String attr = el.getAttributes().getNamedItem(attribute).getNodeValue();

			if(attr.equals(attributeValue))
				parent.removeChild(nodes.item(i));

			parent.appendChild(doc.createTextNode(textNode));
			doc.normalize();
			saveTheXMLFile(filename,doc);
		}	
	}

	public void deleteALL(String filename, String tag) throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException{

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(filename));


		NodeList nl = doc.getElementsByTagName(tag);
		for (int i = 0; i < nl.getLength(); i++)
		{
			Node currentItem = nl.item(i);
			Element e=(Element)currentItem;
			e.getParentNode().removeChild(e);
		}
		saveTheXMLFile(filename,doc);
	}

	public void editWithRegularExpressions(String tagValue,String attr, String attrValue,String newTag, String filename,int option) throws TransformerFactoryConfigurationError, TransformerException, SAXException, IOException, ParserConfigurationException{

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(filename));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		constructXPathExp(tagValue);

		NodeList nl = doc.getElementsByTagName(tagName);

		for (int i = 0; i < nl.getLength(); i++)
		{
			Node currentItem = nl.item(i);
			String attrN = currentItem.getAttributes().getNamedItem(attr).getNodeValue();


			if(attrN!=null && Pattern.matches(attrValue.substring(0, attrValue.length()-1)+"[a-z]*[0-9]+",attrN)){
				//System.out.println(Pattern.matches(attrValue.substring(0, attrValue.length()-1)+"[0-9]+",attrN));
				Element e=(Element) currentItem;
				
				if(option==1){
					//replace
					NamedNodeMap attributes = e.getAttributes();
					attributes.removeNamedItem(attr);
					doc.renameNode(e,null, newTag);

				}else if(option==2){
					//delete without text
					Node parent=e.getParentNode();
					String textNode=e.getTextContent();
					parent.removeChild(e);

					parent.appendChild(doc.createTextNode(textNode));
					doc.normalize();

				}else if(option==3){
					//delete with text
					e.getParentNode().removeChild(e);

				}

			}
		}
		saveTheXMLFile(filename,doc);	
	}

	public void editTagByAttribute(String tagValue,String attr,String attrValue,String newTag,String filename, int option) throws TransformerFactoryConfigurationError, TransformerException, SAXException, IOException, ParserConfigurationException{

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(filename));
		constructXPathExp(tagValue);
		Set<Element> targetElements = new HashSet<Element>();

		NodeList nodeList = doc.getElementsByTagName("*");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				Element e=(Element)node;
				//System.out.println(node.getNodeName()+"---> "+node.hasAttributes());

				if(node.hasAttributes()){

					String attrb=getAttribute(e,attr);

					//System.out.println(".......> attr: "+attrb);

					if(attrb!=null){ //&& Pattern.matches(tagName+attr+attrValue.substring(0, attrValue.length()-1)+"[0-9]+",attrb)){
						System.out.println(Pattern.matches(tagName+attr+attrValue.substring(0, attrValue.length()-1)+"[a-z]*[0-9]+","spanclassc18"));
						System.out.println(tagName+attr+attrValue.substring(0, attrValue.length()-1));

						System.out.println(node.getTextContent());
						//targetElements.add(e);
					}

				}
			}

		}
		for (Element e: targetElements) {

			if(option==1){
				//replace
				NamedNodeMap attributes = e.getAttributes();
				attributes.removeNamedItem(attr);
				doc.renameNode(e,null, newTag);

			}else if(option==2){
				//delete without text
				Node parent=e.getParentNode();
				String textNode=e.getTextContent();
				parent.removeChild(e);

				parent.appendChild(doc.createTextNode(textNode));
				doc.normalize();

			}else if(option==3){
				//delete with text
				e.getParentNode().removeChild(e);

			}
		}
		saveTheXMLFile("replace.xhtml",doc);
	}

	public static String getAttribute(Node element, String attName) {
		NamedNodeMap attrs = element.getAttributes();
		if (attrs == null) {
			return null;
		}
		Node attN = attrs.getNamedItem(attName);
		if (attN == null) {
			return null;
		}
		return attN.getNodeValue();
	}

	public void readFromFileAndEdit(String file) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, DOMException, TransformerFactoryConfigurationError, TransformerException{

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(file));
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();

		XPathExpression expr = xpath.compile("//filename");
		NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		Node currentItem = nl.item(0);
		Element eElement = (Element) currentItem;
		fileEdit = eElement.getTextContent();

		nl = doc.getElementsByTagName("replaceTags");
		for (int i = 0; i < nl.getLength(); i++)
		{
			currentItem = nl.item(i);
			if (currentItem.getNodeType() == Node.ELEMENT_NODE) {
				eElement = (Element) currentItem;
				String tagValue=eElement.getElementsByTagName("oldTag").item(0).getTextContent();

				attribute=eElement.getElementsByTagName("oldAttr").item(0).getTextContent();
				attributeValue=eElement.getElementsByTagName("attrValue").item(0).getTextContent();

				if(attributeValue.charAt(attributeValue.length()-1)=='*')
				{
					//System.out.println("expresie regulata.");
					editWithRegularExpressions(tagValue,attribute,attributeValue,eElement.getElementsByTagName("newTag").item(0).getTextContent(),
							fileEdit,1);
				}else /*if(userWish(tagName,attributeValue,eElement.getElementsByTagName("newTag").item(0).getTextContent(),1)==0)*/ {
					replaceTag(tagValue,eElement.getElementsByTagName("newTag").item(0).getTextContent(),
							fileEdit);
				}
			System.out.println("REPLACE  succeeded ! Verify "+fileEdit+ " file.");
			}
		}
		nl = doc.getElementsByTagName("deleteTagWithoutText");
		for (int i = 0; i < nl.getLength(); i++)
		{
			currentItem = nl.item(i);
			if (currentItem.getNodeType() == Node.ELEMENT_NODE) {
				Element element1 = (Element) currentItem;
				String tagValue=element1.getTextContent();
				attribute=element1.getElementsByTagName("attribute").item(0).getTextContent();
				attributeValue=element1.getElementsByTagName("attrValue").item(0).getTextContent();
				if(attributeValue.charAt(attributeValue.length()-1)=='*')
				{
					editWithRegularExpressions(tagValue,attribute,attributeValue,"",
							fileEdit,2);
				}else 
				/*if(userWish(tagName,attributeValue,"",2)==0)*/ {
					
					deleteTagWithoutText(tagValue,fileEdit);
				}
					System.out.println("DELETE  succeeded ! Verify "+fileEdit+ " file.");
			}
		}
		nl = doc.getElementsByTagName("deleteTagWithText");
		for (int i = 0; i < nl.getLength(); i++)
		{
			currentItem = nl.item(i);
			if (currentItem.getNodeType() == Node.ELEMENT_NODE) {
				Element element1 = (Element) currentItem;
				String tagValue=element1.getTextContent();

				attribute=element1.getElementsByTagName("attribute").item(0).getTextContent();
				attributeValue=element1.getElementsByTagName("attrValue").item(0).getTextContent();

				if(attributeValue.charAt(attributeValue.length()-1)=='*')
				{
					editWithRegularExpressions(tagValue,attribute,attributeValue,"",
							fileEdit,3);
				}else {
				constructXPathExp(tagValue);
				if(userWish(tagName,attributeValue,"",0)==0 /*&& userWish(tagName,attributeValue,"",3)==0 */){
					
					deleteTagWithText(tagValue,fileEdit);
				}
				}
					System.out.println("DELETE  succeeded ! Verify "+fileEdit+ " file.");
			}
		}

	}

	public void saveTheXMLFile(String file, Document doc) throws TransformerFactoryConfigurationError, TransformerException{

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(file);
		Source input = new DOMSource(doc);
		transformer.transform(input, output);
	}

	public int userWish(String className, String attrName,String newTag, int option) throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException{

		Scanner reader = new Scanner(System.in);  // Reading from System.in
		String input="";
		int output=0;


		if(option==0){
			System.out.println("Do you want to delete all "+className.toUpperCase()+" tags, no matter what attribute has? (yes/no)");
			input = reader.next();
			if(input.equals("yes") && option==0){
				deleteALL(fileEdit,className);
				output=1;
			}
		}else if(option==1){
			//replace
			System.out.println("Do you want to replace all tags with attribute "+attrName.toUpperCase()+" ?(yes/no)");
			input = reader.next();

			if(input.equals("yes")){
				editTagByAttribute("",attribute,attrName,newTag,fileEdit,1);
				output=1;
			}
		}else if(option==2){
			//delete without text
			System.out.println("Do you want to delete WITHOUT text all tags with attribute "+attrName.toUpperCase()+" ?(yes/no)");
			input = reader.next();

			if(input.equals("yes")){
				editTagByAttribute("",attribute,attrName,"",fileEdit,2);
				output=1;
			}
		}else if(option==3){
			//delete with text
			System.out.println("Do you want to delete WITH text all tags with attribute "+attrName.toUpperCase()+" ?(yes/no)");
			input = reader.next();

			if(input.equals("yes")){
				editTagByAttribute("",attribute,attrName,"",fileEdit,3);
				output=1;
			}
		}
		return output;
	}

	public static void main(String[] args) throws XPathExpressionException, TransformerFactoryConfigurationError, ParserConfigurationException, SAXException, IOException, TransformerException{

		//TagsEditor rp=new TagsEditor();
		//rp.readFromFileAndEdit("conf.xml");
		//System.out.println(Pattern.matches("z[a-z]*[0-9]+", "zz123"));
		//System.out.println(Pattern.matches(rp.className+"[.#][a-z]+[0-9]+", "span.c25"));

		//rp.editTagByAttribute("class","c25", "i","replace.xhtml",1);
	}
}

