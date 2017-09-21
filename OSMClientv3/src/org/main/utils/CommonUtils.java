/**
 * 
 */
package org.main.utils;

import java.io.OutputStream;
import java.io.StringWriter;
import java.util.GregorianCalendar;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author shashank.mutgi
 *
 */
public class CommonUtils {

	/**
	 * 
	 */
	
	public static String stringXML(Document document) throws TransformerException{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(document), new StreamResult(writer));
		String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
	
		return output;
	}
	
	public static void printXML(Element element, OutputStream out){
		
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
			
			Source source = new DOMSource(element);
			
			StreamResult output = new StreamResult(out);
			
			transformer.transform(source, output);
			
			
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String theMonth(int month){
	    String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	    return monthNames[month];
	}
	
	public static String getDate(){
		String retval = "";
		
		GregorianCalendar calendar = new GregorianCalendar();
		
		StringBuilder date = new StringBuilder();
		date.append(calendar.get(GregorianCalendar.DAY_OF_MONTH));
		date.append("-");
		date.append(theMonth(calendar.get(GregorianCalendar.MONTH)));
		date.append("-");
		date.append(calendar.get(GregorianCalendar.YEAR));
		date.append(" : ");
		date.append(String.format("%02d", calendar.get(GregorianCalendar.HOUR_OF_DAY)));
		date.append(":");
		date.append(String.format("%02d", calendar.get(GregorianCalendar.MINUTE)));
		date.append(":");
		date.append(String.format("%02d", calendar.get(GregorianCalendar.SECOND)));
		
		retval = date.toString();
		return retval;
	}
	
	public CommonUtils() {
		// TODO Auto-generated constructor stub
	}

}
