package org.main.application;

import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.main.utils.CommonUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		try {
			
			String iccid = "ICCID";
			Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element root1 = responseDocument.createElement("provord:ProvisioningOrderItemInstance");
			
			Element identificationNode = responseDocument.createElement("corecom:Identification");
			Attr attr = responseDocument.createAttribute("xmlns:corecom");
			attr.setValue("http://xmlns.oracle.com/EnterpriseObjects/Core/Common/V2");
			identificationNode.setAttributeNodeNS(attr);
			Element idElement = responseDocument.createElement("corecom:ID");
			idElement.setTextContent(iccid);
			identificationNode.appendChild(idElement);
			root1.appendChild(identificationNode);
			
			responseDocument.appendChild(root1);
			OutputStream out = System.out;
			CommonUtils.printXML(responseDocument.getDocumentElement(), out);
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
				
	}

}
