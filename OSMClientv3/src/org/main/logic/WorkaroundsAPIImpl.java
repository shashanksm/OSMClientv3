package org.main.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.main.sei.OSMService;
import org.main.sei.OSMServiceFactory;
import org.main.sei.XmlApiService;
import org.main.sei.XmlApiServiceFactory;
import org.main.utils.CommonUtils;

public class WorkaroundsAPIImpl implements WorkaroundsAPI{

	private static final Logger logger = LogManager.getLogger(WorkaroundsAPIImpl.class.getName());
	private static final OSMService service = OSMServiceFactory.newInstance().getOSMService();
	private static final XmlApiService xmlapi = XmlApiServiceFactory.newInstance().getXmlServiceApi();
	
	private String rOrderId;
	private String rOrderHistId;
	//private String rOrderState;
	private String rTask;
	//private String rStartDate;
	private String rNamespace;
	private String rVersion;
	private String rCreationView;
	private String rOrderSource;
	private String rOrderType;
	private String rSpecificationPath;
	private String rNewOrderId;
	
	private List<String> rStatuses;
	
	private String user;
	private String[] repushMnemonics;
	private String[] skipMnemonics;
	private String[] exceptionTasks;
	private String[] skipExceptions;
	private String[] networkTasks;
	
	private int throttleAvoidanceCount;
	private int throttleAvoidanceInterval;
	
	private String lastTaskString;
	@Override
	public boolean startup(){
		
		logger.trace("loading repush-configurations");
		
		Properties properties = new Properties();
		
		File input = new File(System.getProperty("user.dir")+"\\config\\repush-config.properties");
		
		if(!input.exists()){
			logger.error("repush configuration file not found");
			return false;
		}
		
		try {
			properties.load(new FileInputStream(input));
			
			String unparsedInputString1 = "";
			String unparsedInputString2 = "";
			String unparsedInputString3 = "";
			String unparsedInputString4 = "";
			String unparsedInputString5 = "";
			
			user = "admin";
			throttleAvoidanceCount = 0;
			throttleAvoidanceInterval=0;
			
			if(properties.containsKey("repush-mnemonics")){
				unparsedInputString1 = properties.getProperty("repush-mnemonics");
				repushMnemonics = unparsedInputString1.split(";");
			}
			
			if(properties.containsKey("skip-mnemonics")){
				unparsedInputString2 = properties.getProperty("skip-mnemonics");
				skipMnemonics = unparsedInputString2.split(";");
			}
			
			if(properties.containsKey("task-exceptions")){
				unparsedInputString3 = properties.getProperty("task-exceptions");
				exceptionTasks = unparsedInputString3.split(";");
			}
			
			if(properties.containsKey("skip-exceptions")){
				unparsedInputString4 = properties.getProperty("skip-exceptions");
				skipExceptions = unparsedInputString4.split(";");
			}
			
			if(properties.containsKey("network-tasks")){
				unparsedInputString5 = properties.getProperty("network-tasks");
				networkTasks = unparsedInputString5.split(";");
			}
			
			if(properties.containsKey("user")){
				user = properties.getProperty("user");
			}
			
			if(properties.containsKey("throttle-avoidance-count")){
				throttleAvoidanceCount = Integer.parseInt(properties.getProperty("throttle-avoidance-count"));
			}
			
			if(properties.containsKey("throttle-avoidance-interval")){
				throttleAvoidanceInterval = Integer.parseInt(properties.getProperty("throttle-avoidance-interval"));
			}
			
//			String unparsedInputString1 = properties.getProperty("repush-mnemonics");
//			repushMnemonics = unparsedInputString1.split(";");
//			
//			String unparsedInputString2 = properties.getProperty("skip-mnemonics");
//			skipMnemonics = unparsedInputString2.split(";");
//			
//			String unparsedInputString3 = properties.getProperty("task-exceptions");
//			exceptionTasks = unparsedInputString3.split(";");
//			
//			String unparsedInputString4 = properties.getProperty("skip-exceptions");
//			skipExceptions = unparsedInputString4.split(";");
//			
//			String unparsedInputString5 = properties.getProperty("network-tasks");
//			networkTasks = unparsedInputString5.split(";");
//			
//			user = properties.getProperty("user");
//			
//			throttleAvoidanceCount = Integer.parseInt(properties.getProperty("throttle-avoidance-count"));
//			throttleAvoidanceInterval = Integer.parseInt(properties.getProperty("throttle-avoidance-interval"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		boolean ret = xmlapi.login();
		
		
		
		return ret;
	}
	
	@Override
	public boolean cleanup(){
		boolean ret = xmlapi.logout();
		
		
		return ret;
	}
	
	
	
	/**
	 * 
	 * @param response
	 * @return
	 */
	
	
	
	public static Element getEBMData(Element response){
		logger.trace("call made to extract EBM element");
		Element retval = null;
		///   //Envelope/Body/GetOrderResponse/Data/_root/messageXmlData/ProcessProvisioningOrderEBM
		//      /*[local-name()=\"\"]
		
		
		logger.trace("extracting using xpath");
		String expression = "//*[local-name()=\"Envelope\"]"
				+ " /*[local-name()=\"Body\"]"
				+ "/*[local-name()=\"GetOrderResponse\"]"
				+ "/*[local-name()=\"Data\"]"
				+ "/*[local-name()=\"_root\"]"
				+ " /*[local-name()=\"messageXmlData\"]"
				+ "/*[local-name()=\"ProcessProvisioningOrderEBM\"]";
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		NodeList nodes;
		try {
			nodes = (NodeList) xpath.compile(expression).evaluate(response, XPathConstants.NODESET);
			
			
			if (nodes.getLength() == 1){
				retval = (Element)nodes.item(0);
				logger.trace("ebm extraction successful");
			}else{
				logger.error("CAUTION : number of ebm elements is other than one : "+nodes.getLength()+" elements found");
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return retval;
	}
	
	
	@Override
	public void getOrder(String orderId, String view){
		
		logger.trace("getOrder operation called on order-id : "+orderId+" and view : "+view);
		logger.trace("requesting xml template : ");
		
		Document requestDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_getOrder.xml"))));
			logger.trace("parsing and generating getOrderXML");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				orderIdElements.item(0).setTextContent(orderId);
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				return;
			}
			
			NodeList viewElements = root.getElementsByTagName("ord:View");
			if(viewElements.getLength() == 1){
				viewElements.item(0).setTextContent(view);
			}else{
				logger.error("CAUTION : number of view elements is other than 1");
				return;
			}
			
			requestBody = CommonUtils.stringXML(requestDocument);
			
			
			
			request = service.prepareRequest(requestBody,"GetOrder");
			
			logger.trace("invoking soap-action");
			
			response = (CloseableHttpResponse) service.sendRequest(request);
			
			System.out.println(response.getStatusLine());
			
			Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(response.getEntity().getContent());
			
			CommonUtils.printXML(responseDocument.getDocumentElement(), new FileOutputStream(new File("response.xml")));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
	@Override
	public boolean abortOrder(String orderID){
		boolean ret = false;
		
		logger.trace("AbortOrder called for orderID : "+orderID);
		
		Document requestDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		try {
			
			//ChangeRequiredHere
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_abortOrder.xml"))));
			logger.trace("parsing and generating AbortOrderXML");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				
				orderIdElements.item(0).setTextContent(orderID);
				requestBody = CommonUtils.stringXML(requestDocument);
				
				
				
				request = service.prepareRequest(requestBody, "AbortOrder");
				
				logger.trace("invoking soap-action");
				
				response = (CloseableHttpResponse) service.sendRequest(request);
				
				logger.trace(response.getStatusLine());
				if(response.getStatusLine().getStatusCode() == 200){
					ret = true;
				}else{
					ret=false;
				}
					
				
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				return false;
			}
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	
	@Override
	public boolean createOrder(Document responseDocument) {
		// TODO Auto-generated method stub
		boolean ret = false;
		
		
		Document requestDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		try {
			
			//ChangeRequiredHere
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_createOrder.xml"))));
			logger.trace("parsing and generating CreateOrderXML");
			Element root = requestDocument.getDocumentElement();
			
			NodeList createOrderElements = root.getElementsByTagName("ord:CreateOrder");
			if(createOrderElements.getLength() == 1){
				
				createOrderElements.item(0).appendChild(requestDocument.importNode(getEBMData(responseDocument.getDocumentElement()), true));
				
				logger.trace("invoking SOAP action");
				
				requestBody = CommonUtils.stringXML(requestDocument);
				
				request = service.prepareRequest(requestBody, "CreateOrder");
				
				logger.trace("invoking soap-action");
				
				response = (CloseableHttpResponse) service.sendRequest(request);
				
				logger.trace(response.getStatusLine());
				if(response.getStatusLine().getStatusCode() == 200){
					ret = true;
				}else{
					ret=false;
				}
				
			}else{
				logger.error("CAUTION : number of CreateOrder elements is other than 1");
				ret=false;
			}
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	@Override
	public boolean createOrder(String orderId, String view) {
		// TODO Auto-generated method stub
		
		boolean ret = false;
		
		logger.trace("CreateOrder called for orderID : "+orderId+ " with view : "+view);
		
		Document requestDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		try {
			
			//ChangeRequiredHere
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_getOrder.xml"))));
			logger.trace("parsing and generating GetOrderXML");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				orderIdElements.item(0).setTextContent(orderId);
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				ret=false;
			}
			
			NodeList viewElements = root.getElementsByTagName("ord:View");
			if(viewElements.getLength() == 1){
				viewElements.item(0).setTextContent(view);
			}else{
				logger.error("CAUTION : number of view elements is other than 1");
				ret=false;
			}
			
			requestBody = CommonUtils.stringXML(requestDocument);
			
			
			
			request = service.prepareRequest(requestBody, "GetOrder");
			
			logger.trace("invoking soap-action");
			
			response = (CloseableHttpResponse) service.sendRequest(request);
			
			if(response.getStatusLine().getStatusCode() == 200){
				
				Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
				
				createOrder(responseDocument);
				
			}else{
				ret=false;
			}
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}

	
	@Override
	public boolean abortRecreateSOM(String orderId) {
		
		boolean ret = false;
		// TODO Auto-generated method stub
		String view = "SOM_ProvisionOrderFulfillment_OrderDetails";
		
		logger.trace("AbortRecreateSOM called for order-id : "+orderId);
		
		logger.trace("requesting xml template : ");
		
		Document requestDocument = null;
		Document responseDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_getOrder.xml"))));
			logger.trace("parsing and generating getOrderXML");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				orderIdElements.item(0).setTextContent(orderId);
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				ret=false;
			}
			
			NodeList viewElements = root.getElementsByTagName("ord:View");
			if(viewElements.getLength() == 1){
				viewElements.item(0).setTextContent(view);
			}else{
				logger.error("CAUTION : number of view elements is other than 1");
				ret=false;
			}
			
			requestBody = CommonUtils.stringXML(requestDocument);
			
			
			
			request = service.prepareRequest(requestBody, "GetOrder");
			
			logger.trace("invoking soap-action");
			
			response = (CloseableHttpResponse) service.sendRequest(request);
			
			if(response.getStatusLine().getStatusCode() == 200){
				
				
				responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
				logger.trace("extracting EBM data");
				
				Element ebmElement = getEBMData(responseDocument.getDocumentElement());
				
				if(ebmElement!=null){
					logger.trace("aborting order");
					
					ret = abortOrder(orderId);
					
					if(ret){
						
						logger.trace("proceeding to recreate");
						
						ret = createOrder(responseDocument);
						
					}else{
						logger.error("AbortOrder failed. did not recreate order");
						ret=false;
					}	
					
					
				}else{
					logger.error("EBM Element not found. did not abort the order or recreate the order");
					return false;
				}
				
				
			}else{
				logger.error("invalid response received from server");
				return false;
			}
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
		
	}


	@Override
	public boolean abortRecreateWithoutReconnectionFlag(String orderId) {
		boolean ret = false;
		// TODO Auto-generated method stub
		String view = "SOM_ProvisionOrderFulfillment_OrderDetails";
		
		logger.trace("AbortRecreateWithoutReconnectionFlag called for order-id : "+orderId);
		
		logger.trace("requesting xml template : ");
		
		Document requestDocument = null;
		Document responseDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_getOrder.xml"))));
			logger.trace("parsing and generating getOrderXML");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				orderIdElements.item(0).setTextContent(orderId);
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				ret=false;
			}
			
			NodeList viewElements = root.getElementsByTagName("ord:View");
			if(viewElements.getLength() == 1){
				viewElements.item(0).setTextContent(view);
			}else{
				logger.error("CAUTION : number of view elements is other than 1");
				ret=false;
			}
			
			requestBody = CommonUtils.stringXML(requestDocument);
			
			
			
			request = service.prepareRequest(requestBody, "GetOrder");
			
			logger.trace("invoking soap-action");
			
			response = (CloseableHttpResponse) service.sendRequest(request);
			
			if(response.getStatusLine().getStatusCode() == 200){
				
				
				responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
				logger.trace("extracting EBM data");
				
				Element ebmElement = getEBMData(responseDocument.getDocumentElement());
				
				if(ebmElement!=null){
					
					logger.trace("removing reconnection flag");
					
					XPath xpath = XPathFactory.newInstance().newXPath();
					String expression = "//*[local-name()=\"ReasonCode\" and text()=\"Reconnection\"]";
					
					try {
						NodeList nodes = (NodeList) xpath.compile(expression).evaluate(ebmElement, XPathConstants.NODESET);
						logger.trace("number of reconnection nodes found : "+nodes.getLength());
						for(int i = 0; i<nodes.getLength(); i++){
							Element delement = (Element)nodes.item(i);
							
							delement.getParentNode().removeChild(delement);
						}
					} catch (XPathExpressionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					logger.trace("aborting order");
					
					ret = abortOrder(orderId);
					
					if(ret){
						
						logger.trace("proceeding to recreate");
						
						
						
						
						ret = createOrder(responseDocument);
						
					}else{
						logger.error("AbortOrder failed. did not recreate order");
						return false;
					}	
					
					
				}else{
					logger.error("EBM Element not found. did not abort the order or recreate the order");
					return false;
				}
				
				
			}else{
				logger.error("invalid response received from server");
				return false;
			}
		
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}


	@Override
	public boolean updateTILstatus(String orderId, String view, String targetStatus) {
		// TODO Auto-generated method stub
		boolean ret = false;
		
		logger.trace("updateTILstatus called for order-id : "+orderId);
		
		logger.trace("requesting xml template : ");
		
		Document requestDocument = null;
		Document responseDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_getOrder.xml"))));
			
			logger.trace("parsing and generating getOrderXML");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				orderIdElements.item(0).setTextContent(orderId);
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				ret=false;
			}
			
			NodeList viewElements = root.getElementsByTagName("ord:View");
			if(viewElements.getLength() == 1){
				viewElements.item(0).setTextContent(view);
			}else{
				logger.error("CAUTION : number of view elements is other than 1");
				ret=false;
			}
			
			requestBody = CommonUtils.stringXML(requestDocument);
			
			
			
			request = service.prepareRequest(requestBody, "GetOrder");
			
			logger.trace("invoking soap-action");
			
			response = (CloseableHttpResponse) service.sendRequest(request);
			
			if(response.getStatusLine().getStatusCode() == 200){
				
				responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
				XPath xpath = XPathFactory.newInstance().newXPath();
				
				String expression = "//*[local-name()=\"TILStatus\"]";
				String retVal;
				
				NodeList nodes = (NodeList) xpath.compile(expression).evaluate(responseDocument.getDocumentElement(), XPathConstants.NODESET);
				if(nodes.getLength()==1){
					logger.trace("number of TILStatus elements found : " + nodes.getLength());
					Element element = (Element)nodes.item(0);
					retVal = element.getTextContent();
					
					if(!retVal.equals(targetStatus)){
						
						/*
						 * //OrderData/FixedService/TILStatus[index='1475488039331']
						 */
						logger.trace("TILStatus found : "+retVal);
						StringBuilder data = new StringBuilder();
						data.append("//OrderData/FixedService/TILStatus[index=\'");
						data.append(element.getAttribute("index"));
						data.append("\']");
						
						ret = updateOrderwithUpdateData(orderId, view, data.toString(), targetStatus);
					}else{
						logger.trace("TILStatus already in required status");
						ret=true;
					}
				}else{
					logger.error("WARNING : number of TILStatus elements found : " +nodes.getLength());
					ret=false;
				}
			}else{
				logger.error("GetOrder not successful");
				ret=false;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	@Override
	public boolean updateUserNamePassword(String orderId, String username, String password) {
		// TODO Auto-generated method stub
		boolean ret = false;
		
		logger.trace("updateTILstatus called for order-id : "+orderId);
		
		logger.trace("requesting xml template : ");
		
		Document requestDocument = null;
		Document responseDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_getOrder.xml"))));
			
			logger.trace("parsing and generating getOrderXML");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				orderIdElements.item(0).setTextContent(orderId);
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				ret=false;
			}
			
			NodeList viewElements = root.getElementsByTagName("ord:View");
			if(viewElements.getLength() == 1){
				viewElements.item(0).setTextContent("Fixed006.CreationTask_EquipmentProvision");
			}else{
				logger.error("CAUTION : number of view elements is other than 1");
				ret=false;
			}
			
			requestBody = CommonUtils.stringXML(requestDocument);
			
			
			
			request = service.prepareRequest(requestBody, "GetOrder");
			
			logger.trace("invoking soap-action");
			
			response = (CloseableHttpResponse) service.sendRequest(request);
			
			if(response.getStatusLine().getStatusCode() == 200){
				
				responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
				XPath xpath = XPathFactory.newInstance().newXPath();
				
				String expression = "//*[@index[not(. < //@index)]]";
				//String retVal;
				
				NodeList nodes = (NodeList) xpath.compile(expression).evaluate(responseDocument.getDocumentElement(), XPathConstants.NODESET);
				Element maxValueOfIndexElement = (Element)nodes.item(0);
				
				long maxValueOfIndex = Long.parseLong(maxValueOfIndexElement.getAttribute("index"));
				
				expression = "//*[local-name()=\"EquipmentDetails\"]";
				xpath = XPathFactory.newInstance().newXPath();
				nodes = (NodeList) xpath.compile(expression).evaluate(responseDocument.getDocumentElement(), XPathConstants.NODESET);
				if(nodes.getLength()==1){
					logger.trace("number of EquipmentDetails elements found : " + nodes.getLength());
					Element element = (Element)nodes.item(0);
					
					
					//if(!retVal.equals(targetStatus)){
						
						/*
						 * //OrderData/FixedService/EquipmentDetails[index='1475488039331']
						 */
						//logger.trace("TILStatus found : "+retVal);
						StringBuilder data1 = new StringBuilder();
						StringBuilder data2 = new StringBuilder();
						data1.append("//OrderData/FixedService/EquipmentDetails[index=\'");
						data1.append(element.getAttribute("index"));
						data1.append("\']");
						data2.append("<osmc:PPPUsername index=\""+maxValueOfIndex+1+"\">"+username+"</osmc:PPPUsername>");
						data2.append("<osmc:PPPPassword index=\""+maxValueOfIndex+2+"\">"+password+"</osmc:PPPPassword>");
						
						ret = updateOrderwithAddData(orderId, "Fixed006.CreationTask_EquipmentProvision", data1.toString(), data2.toString());
//					}else{
//						logger.trace("TILStatus already in required status");
//						ret=true;
//					}
				}else{
					logger.error("WARNING : number of TILStatus elements found : " +nodes.getLength());
					ret=false;
				}
				
				
			}else{
				logger.error("GetOrder not successful");
				ret=false;
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}


	@Override
	public boolean updateOrderwithUpdateData(String orderId, String view, String data, String content) {
		// TODO Auto-generated method stub
		boolean ret = false;
		
		logger.trace("updateOrderwithUpdateData called for orderID : "+orderId+" with view : "+view +" and data : "+data);
		
		Document requestDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		try {
			
			//ChangeRequiredHere
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_updateOrder_update_node.xml"))));
			logger.trace("parsing and generating request xml");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				
				orderIdElements.item(0).setTextContent(orderId);
				
				NodeList viewElements = root.getElementsByTagName("ord:View");
				
				if(viewElements.getLength() == 1){
					viewElements.item(0).setTextContent(view);
					
					NodeList updateNodes = root.getElementsByTagName("ord:Update");
					if(updateNodes.getLength() == 1){
						Element updateElement = (Element)updateNodes.item(0);
						updateElement.setAttribute("Path", data);
						updateElement.setTextContent(content);
						
						
						requestBody = CommonUtils.stringXML(requestDocument);
						
						
						
						request = service.prepareRequest(requestBody, "UpdateOrder");
						
						logger.trace("invoking soap-action");
						
						response = (CloseableHttpResponse) service.sendRequest(request);
						
						logger.trace(response.getStatusLine());
						if(response.getStatusLine().getStatusCode() == 200){
							ret = true;
						}else{
							ret=false;
						}
					}else {
						logger.trace("CAUTION : number of update-elements is other than one");
						ret=false;
					}
					
				}else{
					logger.error("CAUTION : number of view elements is other than 1");
					ret=false;
				}
				//Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(response.getEntity().getContent());	
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				ret=false;
			}
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	@Override
	public boolean updateOrderwithDeleteData(String orderId, String view, String data) {
		// TODO Auto-generated method stub
		boolean ret = false;
		
		logger.trace("updateOrderwithDeleteData called for orderID : "+orderId+" with view : "+view +" and data : "+data);
		
		Document requestDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		try {
			
			//ChangeRequiredHere
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_updateOrder.xml"))));
			logger.trace("parsing and generating request xml");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				
				orderIdElements.item(0).setTextContent(orderId);
				
				NodeList viewElements = root.getElementsByTagName("ord:View");
				
				if(viewElements.getLength() == 1){
					viewElements.item(0).setTextContent(view);
					
					NodeList deleteElements = root.getElementsByTagName("ord:Delete");
					if(deleteElements.getLength() == 1){
						Element deleteElement = (Element)deleteElements.item(0);
						deleteElement.setAttribute("Path", data);
						
						
						requestBody = CommonUtils.stringXML(requestDocument);
						
						
						
						request = service.prepareRequest(requestBody, "UpdateOrder");
						
						logger.trace("invoking soap-action");
						
						response = (CloseableHttpResponse) service.sendRequest(request);
						
						logger.trace(response.getStatusLine());
						if(response.getStatusLine().getStatusCode() == 200){
							
							ret = true;
						}else{
							ret=false;
						}
					}else {
						logger.trace("CAUTION : number of update-elements is other than one");
						ret=false;
					}
					
				}else{
					logger.error("CAUTION : number of delete-elements is other than one");
					ret=false;
				}
				//Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(response.getEntity().getContent());	
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				ret=false;
			}
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}

	@Override
	public boolean getOrderAtTask(String orderId, String task) {
		
		
		boolean ret = false;
		logger.trace("getOrderAtTask called for order-id : "+orderId+" and task : "+task);
		
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("GetOrderAtTask.xml"))));
			
			NodeList orderIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderID");
			
			if(orderIdElements.getLength() != 1){
				logger.error("number of order-id elements in request is other than one : "+orderIdElements.getLength());
				return false;
			}
			
			orderIdElements.item(0).setTextContent(orderId);
			
			
			NodeList taskElements = requestDocument.getDocumentElement().getElementsByTagName("Task");
			
			if(taskElements.getLength() != 1){
				logger.error("number of task elements in request is other than one : "+taskElements.getLength());
				return false;
			}
			
			taskElements.item(0).setTextContent(task);
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return false;
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			if(root.getTagName().equals("GetOrderAtTask.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return false;
			}
			
			rOrderId = orderId;
			rOrderHistId = root.getElementsByTagName("OrderHistID").item(0).getTextContent();
			rTask = task;
			rNamespace = root.getElementsByTagName("Namespace").item(0).getTextContent();
			rVersion = root.getElementsByTagName("Version").item(0).getTextContent();
			rOrderSource = root.getElementsByTagName("OrderSource").item(0).getTextContent();
			rOrderType = root.getElementsByTagName("OrderType").item(0).getTextContent();
		
			//OrderData/FixedService/LineItem[0]/SpecificationGroup/Specification[0]
			///*[local-name()=\"Body\"]"
			
			
			
			ret = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	private boolean getOrderAtTaskFL(String orderId, String task) {
		// TODO Auto-generated method stub
		
		boolean ret = false;
		logger.trace("getOrderAtTaskFL called for order-id : "+orderId+" and task : "+task);
		
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("GetOrderAtTask.xml"))));
			
			NodeList orderIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderID");
			
			if(orderIdElements.getLength() != 1){
				logger.error("number of order-id elements in request is other than one : "+orderIdElements.getLength());
				return false;
			}
			
			orderIdElements.item(0).setTextContent(orderId);
			
			
			NodeList taskElements = requestDocument.getDocumentElement().getElementsByTagName("Task");
			
			if(taskElements.getLength() != 1){
				logger.error("number of task elements in request is other than one : "+taskElements.getLength());
				return false;
			}
			
			taskElements.item(0).setTextContent(task);
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return false;
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			if(root.getTagName().equals("GetOrderAtTask.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return false;
			}
			
			rOrderId = orderId;
			rOrderHistId = root.getElementsByTagName("OrderHistID").item(0).getTextContent();
			rTask = task;
			rNamespace = root.getElementsByTagName("Namespace").item(0).getTextContent();
			rVersion = root.getElementsByTagName("Version").item(0).getTextContent();
			rOrderSource = root.getElementsByTagName("OrderSource").item(0).getTextContent();
			rOrderType = root.getElementsByTagName("OrderType").item(0).getTextContent();
			
			int index1 = 0;
			int index2 = 0;
			
			//OrderData/FixedService/LineItem[0]/SpecificationGroup/Specification[0]
			///*[local-name()=\"Body\"]"
			
			/*
			 * //OrderData/FixedService/EquipmentDetails[index='1475488039331']
			 */
			String expression = "//*[local-name()=\"Specification\"]";
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			NodeList nodes;
			try {
				nodes = (NodeList) xpath.compile(expression).evaluate(responseDocument.getDocumentElement(), XPathConstants.NODESET);
				
				
				if (nodes.getLength() > 1){
					
					Element e = (Element)nodes.item(0);
					index2 = Integer.parseInt(e.getAttribute("index"));
					e = (Element) (e.getParentNode().getParentNode());
					index1 = Integer.parseInt(e.getAttribute("index"));
					rSpecificationPath = "/OrderData/FixedService/LineItem[@index=\'"+index1+"\']/SpecificationGroup/Specification[@index=\'"+index2+"\']";
				}
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			expression = "//*[local-name()=\"EquipmentDetails\"]";
			
			//String rEquipmentDetailsPath = "/OrderData/FixedService/EquipmentDetails"
			
			
			
			ret = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	@Override
	public boolean getCreationView(String orderSource, String orderType, String namespace, String version) {
		// TODO Auto-generated method stub
		
		boolean ret = false;
		logger.trace("getCreationView called for order-source : "+orderSource+" and order-type : "+orderType+" and namespace : "+namespace+" and version : "+version);
		
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("ListViews.xml"))));
			
			NodeList orderSourceElements = requestDocument.getDocumentElement().getElementsByTagName("OrderSource");
			
			if(orderSourceElements.getLength() != 1){
				logger.error("number of order-id elements in request is other than one : "+orderSourceElements.getLength());
				return false;
			}
			
			orderSourceElements.item(0).setTextContent(orderSource);
			
			
			NodeList orderTypeElements = requestDocument.getDocumentElement().getElementsByTagName("OrderType");
			
			if(orderTypeElements.getLength() != 1){
				logger.error("number of orderType elements in request is other than one : "+orderTypeElements.getLength());
				return false;
			}
			
			orderTypeElements.item(0).setTextContent(orderType);
			
			NodeList namespaceElements = requestDocument.getDocumentElement().getElementsByTagName("Namespace");
			
			if(namespaceElements.getLength() != 1){
				logger.error("number of namespace elements in request is other than one : "+namespaceElements.getLength());
				return false;
			}
			
			namespaceElements.item(0).setTextContent(namespace);
			
			NodeList versionElements = requestDocument.getDocumentElement().getElementsByTagName("Version");
			
			if(versionElements.getLength() != 1){
				logger.error("number of version elements in request is other than one : "+versionElements.getLength());
				return false;
			}
			
			versionElements.item(0).setTextContent(version);
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return false;
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			if(root.getTagName().equals("ListViews.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return false;
			}
			
			
			Element rElement =  (Element)(root.getElementsByTagName("View").item(0));
			rCreationView = rElement.getAttribute("mnemonic");
			
			
			ret = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	@Override
	public boolean getTaskStatuses(String task, String namespace, String version) {
		// TODO Auto-generated method stub
		
		boolean ret = false;
		logger.trace("getTaskStatuses called for task : "+task+" and namespace : "+namespace+ " and version : "+version);
		
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("GetTaskStatuses.xml"))));
			
			
			NodeList taskElements = requestDocument.getDocumentElement().getElementsByTagName("Task");
			
			if(taskElements.getLength() != 1){
				logger.error("number of task elements in request is other than one : "+taskElements.getLength());
				return false;
			}
			
			taskElements.item(0).setTextContent(task);
			
			NodeList namespaceElements = requestDocument.getDocumentElement().getElementsByTagName("Namespace");
			
			if(namespaceElements.getLength() != 1){
				logger.error("number of namespace elements in request is other than one : "+namespaceElements.getLength());
				return false;
			}
			
			namespaceElements.item(0).setTextContent(namespace);
			
			NodeList versionElements = requestDocument.getDocumentElement().getElementsByTagName("Version");
			
			if(versionElements.getLength() != 1){
				logger.error("number of version elements in request is other than one : "+versionElements.getLength());
				return false;
			}
			
			versionElements.item(0).setTextContent(version);
			
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return false;
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			if(root.getTagName().equals("GetTaskStatuses.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return false;
			}
			
			
			NodeList statuses = root.getElementsByTagName("Status");
			rStatuses = new ArrayList<String>();
			
			for(int i = 0; i<statuses.getLength(); i++){
				rStatuses.add(statuses.item(i).getTextContent());
			}
			
			ret = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	@Override
	public boolean assignOrder(String orderId, String orderhistId, String user) {
		// TODO Auto-generated method stub
		
		boolean ret = false;
		logger.trace("assignOrder called for order-id : "+orderId+" and orderhistId : "+orderhistId+ " and user : "+user);
		
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("AssignOrder.xml"))));
			
			
			NodeList orderIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderID");
			
			if(orderIdElements.getLength() != 1){
				logger.error("number of orderId elements in request is other than one : "+orderIdElements.getLength());
				return false;
			}
			
			orderIdElements.item(0).setTextContent(orderId);
			
			NodeList orderhistIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderHistID");
			
			if(orderhistIdElements.getLength() != 1){
				logger.error("number of orderhistId elements in request is other than one : "+orderhistIdElements.getLength());
				return false;
			}
			
			orderhistIdElements.item(0).setTextContent(orderhistId);
			
			NodeList userElements = requestDocument.getDocumentElement().getElementsByTagName("User");
			
			if(userElements.getLength() != 1){
				logger.error("number of user elements in request is other than one : "+userElements.getLength());
				return false;
			}
			
			userElements.item(0).setTextContent(user);
			
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return false;
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			if(root.getTagName().equals("AssignOrder.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return false;
			}
			
			rOrderHistId = root.getElementsByTagName("OrderHistID").item(0).getTextContent();
			ret = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	
	@Override
	public boolean completeOrder(String orderId, String orderhistId, String status) {
		// TODO Auto-generated method stub
		
		boolean ret = false;
		logger.trace("completeOrder called for order-id : "+orderId+" and orderhistId : "+orderhistId+ " and status : "+status);
		
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("CompleteOrder.xml"))));
			
			
			NodeList orderIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderID");
			
			if(orderIdElements.getLength() != 1){
				logger.error("number of orderId elements in request is other than one : "+orderIdElements.getLength());
				return false;
			}
			
			orderIdElements.item(0).setTextContent(orderId);
			
			NodeList orderhistIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderHistID");
			
			if(orderhistIdElements.getLength() != 1){
				logger.error("number of orderhistId elements in request is other than one : "+orderhistIdElements.getLength());
				return false;
			}
			
			orderhistIdElements.item(0).setTextContent(orderhistId);
			
			NodeList statusElements = requestDocument.getDocumentElement().getElementsByTagName("Status");
			
			if(statusElements.getLength() != 1){
				logger.error("number of status elements in request is other than one : "+statusElements.getLength());
				return false;
			}
			
			statusElements.item(0).setTextContent(status);
			
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return false;
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			if(root.getTagName().equals("CompleteOrder.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return false;
			}
			
			ret = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	@Override
	public boolean receiveOrder(String orderId, String orderhistId) {
		// TODO Auto-generated method stub
		
		boolean ret = false;
		logger.trace("ReceiveOrder called for order-id : "+orderId+" and orderhistId : "+orderhistId);
		
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("ReceiveOrder.xml"))));
			
			
			NodeList orderIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderID");
			
			if(orderIdElements.getLength() != 1){
				logger.error("number of orderId elements in request is other than one : "+orderIdElements.getLength());
				return false;
			}
			
			orderIdElements.item(0).setTextContent(orderId);
			
			NodeList orderhistIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderHistID");
			
			if(orderhistIdElements.getLength() != 1){
				logger.error("number of orderhistId elements in request is other than one : "+orderhistIdElements.getLength());
				return false;
			}
			
			orderhistIdElements.item(0).setTextContent(orderhistId);
			
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return false;
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			if(root.getTagName().equals("ReceiveOrder.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return false;
			}
			
			rOrderHistId = root.getElementsByTagName("OrderHistID").item(0).getTextContent();
			ret = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	@Override
	public boolean acceptOrder(String orderId, String orderhistId) {
		// TODO Auto-generated method stub
		
		boolean ret = false;
		logger.trace("acceptOrder called for order-id : "+orderId+" and orderhistId : "+orderhistId);
		
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("AcceptOrder.xml"))));
			
			
			NodeList orderIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderID");
			
			if(orderIdElements.getLength() != 1){
				logger.error("number of orderId elements in request is other than one : "+orderIdElements.getLength());
				return false;
			}
			
			orderIdElements.item(0).setTextContent(orderId);
			
			NodeList orderhistIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderHistID");
			
			if(orderhistIdElements.getLength() != 1){
				logger.error("number of orderhistId elements in request is other than one : "+orderhistIdElements.getLength());
				return false;
			}
			
			orderhistIdElements.item(0).setTextContent(orderhistId);
			
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return false;
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			if(root.getTagName().equals("GetOrder.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return false;
			}
			
			
			rOrderHistId = root.getElementsByTagName("OrderHistID").item(0).getTextContent();
			ret = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	@Override
	public boolean repush(String orderId, String task){
		boolean ret = false;
		
		logger.trace("calling GetOrderAtTask");
		ret = getOrderAtTask(orderId, task);
		
		if(ret){
			
			ret = getTaskStatuses(task, rNamespace, rVersion);
			
			if(ret){
				
				ret = assignOrder(rOrderId, rOrderHistId, user);

				ret = acceptOrder(rOrderId, rOrderHistId);
				
				boolean matchFound = false;
				int matchId = 0;
				for(int i=0;i<rStatuses.size();i++){
					String status = rStatuses.get(i);
					for(int j = 0; j<repushMnemonics.length;j++){
						
						if(status.equals(repushMnemonics[j])){
							matchFound = true;
							matchId = j;
							break;
						}
					}
					
					if(matchFound == true){
						break;
					}
				}
				
				if(matchFound){
					
					System.out.println("using menemonic : "+repushMnemonics[matchId]);
					logger.trace("using menemonic : "+repushMnemonics[matchId]);
					
					ret = completeOrder(rOrderId, rOrderHistId, repushMnemonics[matchId]);
					
				}else{
					System.out.println("no repush mnemonic found for task : "+task+"; statuses found : "+rStatuses);
					logger.trace("no repush mnemonic found for task : "+task+"; statuses found : "+rStatuses);
					logger.trace("changing states of order");
					ret = receiveOrder(rOrderId, rOrderHistId);
				}
				
			}else{
				logger.error("GetTaskStatuses failed");
			}
			
		}else{
			logger.error("GetOrderAtTask failed");
		}
		
		
		return ret;
	}
	
	@Override
	public boolean skip(String orderId, String task){
		boolean ret = false;
		
		logger.trace("checking task-exceptions");
		for(int i = 0; i<exceptionTasks.length;i++){
			
			if(task.contains(exceptionTasks[i])){
				logger.error("task exception : "+task);
				System.out.println("task exception : "+task);
				return false;
			}
			
		}
		
		logger.trace("calling GetOrderAtTask");
		ret = getOrderAtTask(orderId, task);
		
		if(ret){
			
			ret = getTaskStatuses(task, rNamespace, rVersion);
			
			if(ret){
				
				ret = assignOrder(rOrderId, rOrderHistId, user);
				
				ret = acceptOrder(rOrderId, rOrderHistId);
				
				boolean matchFound = false;
				int matchId = 0;
				for(int i=0;i<rStatuses.size();i++){
					String status = rStatuses.get(i);
					for(int j = 0; j<skipMnemonics.length;j++){
						
						if(status.equals(skipMnemonics[j])){
							matchFound = true;
							matchId = j;
							break;
						}
					}
					
					if(matchFound == true){
						break;
					}
				}
				
				if(matchFound){
					
					System.out.println("using menemonic : "+skipMnemonics[matchId]);
					logger.trace("using menemonic : "+skipMnemonics[matchId]);
					
					ret = completeOrder(rOrderId, rOrderHistId, skipMnemonics[matchId]);
					
				}else{
					System.out.println("no skip mnemonic found for task : "+task+"; statuses found : "+rStatuses);
					logger.trace("no skip mnemonic found for task : "+task+"; statuses found : "+rStatuses);
					loadTasktoSkipException(task);
					//logger.trace("repushing the order");
					//ret = repush(rOrderId, rTask);
				}
				
			}else{
				logger.error("GetTaskStatuses failed");
			}
			
		}else{
			logger.error("GetOrderAtTask failed");
		}
		return ret;
	}

	private void loadTasktoSkipException(String task) {
		// TODO Auto-generated method stub
		
		String[] skipExceptions1 = new String[skipExceptions.length+1];
		for(int i = 0; i<skipExceptions.length;i++){
			skipExceptions1[i] = skipExceptions[i];
		}
		
		skipExceptions1[skipExceptions1.length-1] = task;
		
		skipExceptions = skipExceptions1;
		
		FileOutputStream output = null;
		try {
			
			FileInputStream input = new FileInputStream(System.getProperty("user.dir")+"\\config\\repush-config.properties");
			Properties iprop = new Properties();
			iprop.load(input);
			
			String unparsedInputString1 = iprop.getProperty("repush-mnemonics");
			//repushMnemonics = unparsedInputString1.split(";");
			
			String unparsedInputString2 = iprop.getProperty("skip-mnemonics");
			//skipMnemonics = unparsedInputString2.split(";");
			
			String unparsedInputString3 = iprop.getProperty("task-exceptions");
			//exceptionTasks = unparsedInputString3.split(";");
			
			String unparsedInputString4 = iprop.getProperty("skip-exceptions");
			//skipExceptions = unparsedInputString4.split(";");
			
			String unparsedInputString5 = iprop.getProperty("network-tasks");
			//networkTasks = unparsedInputString5.split(";");
			
			String username = iprop.getProperty("user");
		
			input.close();
			
			output = new FileOutputStream(System.getProperty("user.dir")+"\\config\\repush-config.properties");
			Properties oprop = new Properties();
			
			oprop.setProperty("repush-mnemonics",unparsedInputString1);
			oprop.setProperty("skip-mnemonics", unparsedInputString2);
			oprop.setProperty("task-exceptions", unparsedInputString3);
			oprop.setProperty("skip-exceptions", unparsedInputString4+";"+task);
			oprop.setProperty("network-tasks", unparsedInputString5);
			oprop.setProperty("user", username);
			
			oprop.store(output, null);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.trace("Exception : FileNotFound");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.trace("Exception : IO");
			e.printStackTrace();
		}finally {
			if(output != null){
				try {
					output.close();
					logger.trace("Factory initiated successfully");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
		}
	}

	@Override
	public boolean getLastTask(String reference) {
		// TODO Auto-generated method stub
		boolean ret = false;
		
		logger.trace("GetLastTask called for reference : "+reference);
		
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("LastTaskQuery.xml"))));
			
			
			NodeList referenceElements = requestDocument.getDocumentElement().getElementsByTagName("Reference");
			
			if(referenceElements.getLength() != 1){
				logger.error("number of reference elements in request is other than one : "+referenceElements.getLength());
				return false;
			}
			
			referenceElements.item(0).setTextContent(reference);
			
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return false;
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			
			
			
			if(root.getTagName().equals("Query.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return false;
			}
			
			//processing to be done here
			
			NodeList orderDataElements = root.getElementsByTagName("Orderdata");
			
			if(orderDataElements.getLength() == 0){
				
				rOrderId = "#N/A";
				rOrderHistId = "#N/A";
				//rOrderState = "#N/A";
				rNamespace = "#N/A";
				rVersion = "#N/A";
				rTask = "#N/A";
				
				lastTaskString = reference+"\t#N/A\t#N/A\t#N/A";
			}else{
				
				int w = 0;
				
				
				
				while(w < orderDataElements.getLength()){
					Element node = (Element)orderDataElements.item(w);
					if(!exceptionSkip(node.getElementsByTagName("_task_id").item(0).getTextContent())){
						break;
					}else{
						System.out.println("latest task : "+node.getElementsByTagName("_task_id").item(0).getTextContent()+" is a defined task/subprocess with no skip-mnemonic. looking for next latest task..");
						w++;
					}
				}
				
				if(w == orderDataElements.getLength()){
					rOrderId = "#N/A";
					rOrderHistId = "#N/A";
					//rOrderState = "#N/A";
					rNamespace = "#N/A";
					rVersion = "#N/A";
					rTask = "#N/A";
					
					lastTaskString = reference+"\t#N/A\t#N/A\t#N/A";
				}else{
					Element lastTaskNode = (Element)orderDataElements.item(w);
					
					rOrderId = lastTaskNode.getElementsByTagName("_order_seq_id").item(0).getTextContent();
					rOrderHistId = lastTaskNode.getElementsByTagName("_order_hist_seq_id").item(0).getTextContent();
					//rOrderState = lastTaskNode.getElementsByTagName("_order_state").item(0).getTextContent();
					rNamespace = lastTaskNode.getElementsByTagName("_namespace").item(0).getTextContent();
					rVersion = lastTaskNode.getElementsByTagName("_version").item(0).getTextContent();
					rTask = lastTaskNode.getElementsByTagName("_task_id").item(0).getTextContent();
					
					lastTaskString = reference+"\t"+rOrderHistId+"\t"+rOrderId+"\t"+rTask;
				}
				
				
			}
			
			
			ret = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}

	@Override
	public String printLastTask() {
		// TODO Auto-generated method stub
		return lastTaskString;
	}

	@Override
	public void skipToComplete(List<String> references) {
		// TODO Auto-generated method stub
		logger.trace("skipToComplete called for "+references.size()+" instances");
		
		boolean[] haltSkippingRef = new boolean[references.size()];
		String[] previousTasks = new String[references.size()];
		String[] haltNotes = new String[references.size()];
		int[] samePreviousTaskCount = new int[references.size()];
		
		for(int i = 0; i<previousTasks.length; i++){
			previousTasks[i] = "#N/A";
			haltNotes[i] = "still in osm";
			samePreviousTaskCount[i] = 0;
		}
		
		
		int countOfRefsDone = 0;
		
		Arrays.fill(haltSkippingRef, Boolean.FALSE);
		
		int round = 1;
		
		while(countOfRefsDone != references.size() && round < 26){
			System.out.println("--------------- Starting Round "+round+" -----------------");
			
			for(int i = 0; i<references.size(); i++){
				
				if(!haltSkippingRef[i]){
					
					getLastTask(references.get(i));
					
					if(rTask == null || rTask.equals("#N/A")){
						System.out.println("Reference : "+references.get(i)+ " has exhausted.");
						haltNotes[i] = "Reference : "+references.get(i)+ " has exhausted.";
						haltSkippingRef[i] = true;
						countOfRefsDone++;
					}else if(exceptionTask(rTask)){
						System.out.println("Reference : "+references.get(i)+ " has met task-exception.");
						haltNotes[i] = "Reference : "+references.get(i)+ " has met task-exception.";
						haltSkippingRef[i] = true;
						countOfRefsDone++;
					}else if (samePreviousTaskCount[i] >= 10){
						System.out.println("Reference : "+references.get(i)+ " has been skipped 10 times but has not moved from the task.");
						
						haltNotes[i] = "Reference : "+references.get(i)+ " has been skipped 10 times but has not moved from the task.";
						haltSkippingRef[i] = true;
						countOfRefsDone++;
					}else{
						
						if(rTask.equals(previousTasks[i])){
							samePreviousTaskCount[i]++;
						}
						previousTasks[i] = rTask;
						
						System.out.println("Reference : "+references.get(i)+ " found with task : " + rTask);
						skip(rOrderId,rTask);
						
					}
				}
			}
			
			System.out.println("----------------- Ending Round "+round+" ------------------");
			
			round++;
			
			System.out.println("Standing by..");
			for(int i = 0; i<5;i++){
				System.out.print(i+1+"...");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("GO!");
			
			if(round == 25)
				System.out.println("LAST ROUND!");
		} 
		
		System.out.println("\n\n\nSummary : \n\n\n");
		
		for(int j = 0; j<haltNotes.length; j++){
			System.out.println(haltNotes[j]);
		}
		
	}
	
	@Override
	public void skipNetworkTasks(List<String> references) {
		// TODO Auto-generated method stub
		logger.trace("skipNetworkTasks called for "+references.size()+" instances");
		
		boolean[] haltSkippingRef = new boolean[references.size()];
		String[] previousTasks = new String[references.size()];
		String[] haltNotes = new String[references.size()];
		int[] samePreviousTaskCount = new int[references.size()];
		
		for(int i = 0; i<previousTasks.length; i++){
			previousTasks[i] = "#N/A";
			haltNotes[i] = "still in osm";
			samePreviousTaskCount[i] = 0;
		}
		
		
		int countOfRefsDone = 0;
		
		Arrays.fill(haltSkippingRef, Boolean.FALSE);
		
		int round = 1;
		
		while(countOfRefsDone != references.size() && round < 26){
			System.out.println("--------------- Starting Round "+round+" -----------------");
			
			for(int i = 0; i<references.size(); i++){
				
				if(i%throttleAvoidanceCount == 0){
					logger.trace("sleeping for "+throttleAvoidanceInterval+"ms. to avoid throttle (after "+i+" inputs)");
					System.out.println("sleeping for "+throttleAvoidanceInterval+"ms. to avoid throttle (after "+i+" inputs)");
					try {
						Thread.sleep(throttleAvoidanceInterval);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
				if(!haltSkippingRef[i]){
					
					getLastTask(references.get(i));
					
					if(rTask == null || rTask.equals("#N/A")){
						System.out.println("Reference : "+references.get(i)+ " has exhausted.");
						haltNotes[i] = "Reference : "+references.get(i)+ " has exhausted.";
						haltSkippingRef[i] = true;
						countOfRefsDone++;
					}else if(exceptionTask(rTask)){
						System.out.println("Reference : "+references.get(i)+ " has met task-exception.");
						haltNotes[i] = "Reference : "+references.get(i)+ " has met task-exception.";
						haltSkippingRef[i] = true;
						countOfRefsDone++;
					}else if (samePreviousTaskCount[i] >= 10){
						System.out.println("Reference : "+references.get(i)+ " has been skipped 10 times but has not moved from the task.");
						
						haltNotes[i] = "Reference : "+references.get(i)+ " has been skipped 10 times but has not moved from the task.";
						haltSkippingRef[i] = true;
						countOfRefsDone++;
					}else{
						
						if(rTask.equals(previousTasks[i])){
							samePreviousTaskCount[i]++;
						}
						previousTasks[i] = rTask;
						
						System.out.println("Reference : "+references.get(i)+ " found with task : " + rTask);
						
						if(isNetworkTask(rTask)){
							skip(rOrderId,rTask);
						}else{
							System.out.println(rTask+" is not a network task.");
							haltNotes[i] = "Reference : "+references.get(i)+ " with " +rTask+" is not on a network task.";
							haltSkippingRef[i] = true;
							countOfRefsDone++;
						}
		
					}
				}
			}
			
			System.out.println("----------------- Ending Round "+round+" ------------------");
			
			round++;
			
			System.out.println("Standing by..");
			for(int i = 0; i<5;i++){
				System.out.print(i+1+"...");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("GO!");
			
			if(round == 25)
				System.out.println("LAST ROUND!");
		} 
		
		System.out.println("\n\n\nSummary : \n\n\n");
		
		for(int j = 0; j<haltNotes.length; j++){
			System.out.println(haltNotes[j]);
		}
		
	}

	private boolean exceptionTask(String rTask2) {
		// TODO Auto-generated method stub
		for(int i = 0;i<exceptionTasks.length;i++){
			if(rTask2.contains(exceptionTasks[i])){
				return true;
			}
		}
		return false;
	}
	
	private boolean exceptionSkip(String rTask2) {
		// TODO Auto-generated method stub
		for(int i = 0;i<skipExceptions.length;i++){
			if(rTask2.contains(skipExceptions[i])){
				return true;
			}
		}
		return false;
	}
	
	private boolean isNetworkTask(String rTask2) {
		// TODO Auto-generated method stub
		for(int i = 0;i<networkTasks.length;i++){
			if(rTask2.contains(networkTasks[i])){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean repush(String reference) {
		// TODO Auto-generated method stub
		getLastTask(reference);
		System.out.println("Reference : "+ reference + "\tTask : "+rTask);
		if(!rTask.equals("#N/A"))
			return repush(rOrderId, rTask);
		return false;
	}

	@Override
	public void getOrderByTask(String orderId, String task) {
		// TODO Auto-generated method stub
		getOrderAtTask(orderId, task);
		getCreationView(rOrderSource, rOrderType, rNamespace, rVersion);
		getOrder(orderId, rCreationView);
		
	}
	
	@Override
	public String getPTPIDs(String reference){
		
		getLastTask(reference);
		getOrderAtTask(rOrderId, rTask);
		getCreationView(rOrderSource, rOrderType, rNamespace, rVersion);
		
		Document requestDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_getOrder.xml"))));
			logger.trace("parsing and generating getOrderXML");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				orderIdElements.item(0).setTextContent(rOrderId);
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				return "";
			}
			
			NodeList viewElements = root.getElementsByTagName("ord:View");
			if(viewElements.getLength() == 1){
				viewElements.item(0).setTextContent(rCreationView);
			}else{
				logger.error("CAUTION : number of view elements is other than 1");
				return "";
			}
			
			requestBody = CommonUtils.stringXML(requestDocument);
			
			
			
			request = service.prepareRequest(requestBody, "GetOrder");
			
			logger.trace("invoking soap-action");
			
			response = (CloseableHttpResponse) service.sendRequest(request);
			
			System.out.println(response.getStatusLine());
			
			Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(response.getEntity().getContent());
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			String expression = "//*[local-name()=\"PTP_ID\"]";
			//String retVal;
			
			NodeList nodes = (NodeList) xpath.compile(expression).evaluate(responseDocument.getDocumentElement(), XPathConstants.NODESET);
			
			StringBuilder sb = new StringBuilder();
			
			for(int i = 0; i<nodes.getLength(); i++){
				sb.append("\n"+reference);
				sb.append("\t"+nodes.item(i).getTextContent());
			}
			
			return sb.toString();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return "";
	}

	@Override
	public boolean updateOrderwithAddData(String orderId, String view, String path, String data) {
		// TODO Auto-generated method stub
		boolean ret = false;
		
		logger.trace("updateOrderwithAddData called for orderID : "+orderId+" with view : "+view +" and data : "+data);
		
		Document requestDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		try {
			
			//ChangeRequiredHere
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("req_updateOrderAdd.xml"))));
			logger.trace("parsing and generating request xml");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("ord:OrderId");
			
			if(orderIdElements.getLength() == 1){
				
				orderIdElements.item(0).setTextContent(orderId);
				
				NodeList viewElements = root.getElementsByTagName("ord:View");
				
				if(viewElements.getLength() == 1){
					viewElements.item(0).setTextContent(view);
					
					NodeList addElements = root.getElementsByTagName("ord:Add");
					if(addElements.getLength() == 1){
						Element addElement = (Element)addElements.item(0);
						addElement.setAttribute("Path", path);
						addElement.setTextContent(data);
						
						requestBody = CommonUtils.stringXML(requestDocument);
						
						
						
						request = service.prepareRequest(requestBody, "UpdateOrder");
						
						logger.trace("invoking soap-action");
						
						response = (CloseableHttpResponse) service.sendRequest(request);
						
						logger.trace(response.getStatusLine());
						if(response.getStatusLine().getStatusCode() == 200){
							
							ret = true;
						}else{
							ret=false;
						}
					}else {
						logger.trace("CAUTION : number of update-elements is other than one");
						ret=false;
					}
					
				}else{
					logger.error("CAUTION : number of delete-elements is other than one");
					ret=false;
				}
				//Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(response.getEntity().getContent());	
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				ret=false;
			}
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}

	@Override
	public boolean updateOrderAddNode(String orderId, String view, String path, String nodeName, String nodeValue) {
		// TODO Auto-generated method stub
		boolean ret = false;
		
		logger.trace("calling updateOrderAddNode with orderId"+orderId+" and view "+view +" and node "+nodeName);
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("UpdateOrderAddNode.xml"))));
			
			
			NodeList orderIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderID");
			
			if(orderIdElements.getLength() != 1){
				logger.error("number of orderId elements in request is other than one : "+orderIdElements.getLength());
				return false;
			}
			
			orderIdElements.item(0).setTextContent(orderId);
			
			NodeList viewElements = requestDocument.getDocumentElement().getElementsByTagName("View");
			
			if(viewElements.getLength() != 1){
				logger.error("number of view elements in request is other than one : "+viewElements.getLength());
				return false;
			}
			
			viewElements.item(0).setTextContent(view);
			
			NodeList addElements = requestDocument.getDocumentElement().getElementsByTagName("Add");
			
			if(addElements.getLength() != 1){
				logger.error("number of add elements in request is other than one : "+addElements.getLength());
				return false;
			}
			
			//addElements.item(0).setTextContent(node);
			Element addElement = (Element) addElements.item(0);
			addElement.setAttribute("path", path);
			Element nodeToBeAdded = requestDocument.createElement(nodeName);
			nodeToBeAdded.setTextContent(nodeValue);
			addElement.appendChild(nodeToBeAdded);
			
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return false;
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			if(root.getTagName().equals("UpdateOrder.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return false;
			}
			
			ret = true;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return ret;
	}

	@Override
	public boolean addServiceIDPatch(String orderId, String task, String serviceId) {
		// TODO Auto-generated method stub
		logger.trace("addRouterPatch called for order-ID : " + orderId + " and task " + task + " and router-number "+serviceId);
		boolean ret = false;
		
		ret = getOrderAtTaskFL(orderId, task);
		if(ret){
			
			ret = getCreationView(rOrderSource, rOrderType, rNamespace, rVersion);
			
			if(ret){
				
				String path = rSpecificationPath;
				ret = updateOrderAddNode(orderId, rCreationView, path, "Value", serviceId);
			}else{
				logger.error("getCreationView failed");
				return false;
			}
			
		}else{
			logger.error("getOrderAtTask failed");
			return false;
		}
		
		return ret;
	}

	@Override
	public boolean pppUsernamePasswordPatch(String orderId, String task, String uname, String pwd) {
		logger.trace("pppUsernamePasswordPatch called for order-ID : " + orderId + " and task " + task + " and username "+uname+" and password "+pwd);
		boolean ret = false;
		
		ret = getOrderAtTask(orderId, task);
		if(ret){
			
			ret = getCreationView(rOrderSource, rOrderType, rNamespace, rVersion);
			
			if(ret){
				
				String path = "/OrderData/FixedService/EquipmentDetails";
				ret = updateOrderAddNode(orderId, rCreationView, path, "PPPUsername", uname);
				ret = updateOrderAddNode(orderId, rCreationView, path, "PPPPassword", pwd);
			}else{
				logger.error("getCreationView failed");
				return false;
			}
			
		}else{
			logger.error("getOrderAtTask failed");
			return false;
		}
		
		return ret;
	}

	@Override
	public String getASAPError(String orderId, String task) {
		String ret = "";
		logger.trace("getASAPError called for order-ID : " + orderId + " and task " + task);
		
		logger.trace("getOrderAtTask called for order-id : "+orderId+" and task : "+task);
		
		Document requestDocument = null;
		HttpPost request = null;
		CloseableHttpResponse response = null;
		Document responseDocument = null;
		try {
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(xmlapi.getXmlRequestTemplate("GetOrderAtTask.xml"))));
			
			NodeList orderIdElements = requestDocument.getDocumentElement().getElementsByTagName("OrderID");
			
			if(orderIdElements.getLength() != 1){
				logger.error("number of order-id elements in request is other than one : "+orderIdElements.getLength());
				return "";
			}
			
			orderIdElements.item(0).setTextContent(orderId);
			
			
			NodeList taskElements = requestDocument.getDocumentElement().getElementsByTagName("Task");
			
			if(taskElements.getLength() != 1){
				logger.error("number of task elements in request is other than one : "+taskElements.getLength());
				return "";
			}
			
			taskElements.item(0).setTextContent(task);
			
			logger.trace("sending xml request");
			String requestBody = CommonUtils.stringXML(requestDocument);
			request = xmlapi.prepareRequest(requestBody);
			response = (CloseableHttpResponse) xmlapi.sendXMLRequest(request);
			
			logger.trace("response status : "+response.getStatusLine());
			if(response.getStatusLine().getStatusCode() != 200){
				return "";
			}
			
			responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
			
			Element root = responseDocument.getDocumentElement();
			
			if(root.getTagName().equals("GetOrderAtTask.Error")){
				logger.error(root.getElementsByTagName("Error").item(0).getTextContent());
				return "";
			}
			
			String expression = "//*[local-name()=\"orderFailEvent\"]";
			
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			NodeList nodes;
			try {
				nodes = (NodeList) xpath.compile(expression).evaluate(response, XPathConstants.NODESET);
				
				
				if (nodes.getLength() > 1){
					SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					
					
					
					for(int i = 0; i<nodes.getLength();i++){
						StringBuilder msg = new StringBuilder();
						msg.append(orderId+"\t");
						Element r = (Element)nodes.item(i);
						
						NodeList eventDataNodes = r.getElementsByTagName("EventData");
						NodeList detailedParameters = r.getElementsByTagName("DetailedParameters");
						
						if(eventDataNodes.getLength() != 0 && detailedParameters.getLength() !=0){
							Element eventDataElement = (Element)eventDataNodes.item(0);
							NodeList eventTimeNodes = eventDataElement.getElementsByTagName("eventTime");
							if(eventTimeNodes.getLength() >0){
								Element eventTimeElement = (Element)eventTimeNodes.item(0);
								msg.append(eventTimeElement.getTextContent()+"\t");
							}
							
							Element detailedParametersElement = (Element)detailedParameters.item(0);
							NodeList infoParmNodes = detailedParametersElement.getElementsByTagName("infoParm");
							if(infoParmNodes.getLength() >0){
								for(int k = 0; k<infoParmNodes.getLength(); k++){
									Element infoParmElement = (Element) infoParmNodes.item(0);
									NodeList dataValueNodes = infoParmElement.getElementsByTagName("dataValue");
									if(dataValueNodes.getLength() >0){
										Element dataValueElement = (Element)dataValueNodes.item(0);
										msg.append(dataValueElement.getTextContent()+"\t");
									}
									
								}
							}
							
						}
						
						ret = msg.toString();
						
						
					}
					
					logger.trace("ebm extraction successful");
				}else{
					logger.error("CAUTION : number of orderFailEvent elements is less than one : "+nodes.getLength()+" elements found");
				}
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(response != null)
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret.trim();
	}

	@Override
	public boolean updateOnCURWorkaround(String orderID, String task, String target) {
		boolean ret = false;
		logger.trace("updateOnCURWorkaround called for order-id : "+orderID, " and task : "+task + " to modify status : "+target);
		ret = getOrderAtTask(orderID, task);
		if(ret){
			
			ret = getCreationView(rOrderSource, rOrderType, rNamespace, rVersion);
			
			if(ret){
				
				String path = "/OrderData/CURAction";

				ret=updateOrderwithUpdateData(orderID, rCreationView, path, target);
			}else{
				logger.error("getCreationView failed");
				return false;
			}
			
		}else{
			logger.error("getOrderAtTask failed");
			return false;
		}
		
		return ret;
	}

	@Override
	public boolean updateOnCURBBPatch(String orderID, String task, String target) {
		logger.trace("updateOnCURBBPatch called for order-ID : " + orderID + " and task " + task + " and target "+target);
		boolean ret = false;
		
		ret = getOrderAtTask(orderID, task);
		if(ret){
			
			ret = getCreationView(rOrderSource, rOrderType, rNamespace, rVersion);
			
			if(ret){
				
				String path = "/OrderData/SubscriberData/MSISDNData";
				ret = updateOrderAddNode(orderID, rCreationView, path, "TelephoneNumber", target);
				
			}else{
				logger.error("getCreationView failed");
				return false;
			}
			
		}else{
			logger.error("getOrderAtTask failed");
			return false;
		}
		
		return ret;
	}

	
	
	@Override
	public boolean copyOrder(String originalOrderID, String orderSource, String orderType, String reference,
			String priority, String namespace, String version) {
		Document requestDocument = null;
		HttpPost request;
		CloseableHttpResponse response = null;
		String requestBody;
		boolean ret = false;
		try {
			
			//ChangeRequiredHere
			requestDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new FileInputStream(service.getXmlRequestTemplate("CopyOrder.xml"))));
			logger.trace("parsing and generating CopyOrder.xml");
			Element root = requestDocument.getDocumentElement();
			
			NodeList orderIdElements = root.getElementsByTagName("OriginalOrderID");
			NodeList orderSourceElements = root.getElementsByTagName("OrderSource"); 
			NodeList orderTypeElements = root.getElementsByTagName("OrderType");
			NodeList referenceElements = root.getElementsByTagName("Reference");
			NodeList priorityElements = root.getElementsByTagName("Priority");
			NodeList namespaceElements = root.getElementsByTagName("Namespace");
			NodeList versionElements = root.getElementsByTagName("Version");
			
			
			if(orderIdElements.getLength() == 1){
				
				orderIdElements.item(0).setTextContent(originalOrderID);
				orderSourceElements.item(0).setTextContent(orderSource);
				orderTypeElements.item(0).setTextContent(orderType);
				referenceElements.item(0).setTextContent(reference);
				priorityElements.item(0).setTextContent(priority);
				namespaceElements.item(0).setTextContent(namespace);
				versionElements.item(0).setTextContent(version);
				
				
				requestBody = CommonUtils.stringXML(requestDocument);
				
				
				
				request = xmlapi.prepareRequest(requestBody);
				
				logger.trace("invoking soap-action");
				
				response = (CloseableHttpResponse) service.sendRequest(request);
				
				logger.trace(response.getStatusLine());
				if(response.getStatusLine().getStatusCode() == 200){
					
					Document responseDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(response.getEntity().getContent()));
					
					rOrderId = responseDocument.getDocumentElement().getElementsByTagName("OrderID").item(0).getTextContent();
					logger.trace("old-order-id : "+originalOrderID+"\tnew-order-id : "+rOrderId);
					rOrderHistId = responseDocument.getDocumentElement().getElementsByTagName("OrderHistID").item(0).getTextContent();
					
					ret = true;
				}else{
					ret=false;
				}
					
				
			}else{
				logger.error("CAUTION : number of order-id elements is other than 1");
				return false;
			}
			
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ret;
	}

	@Override
	public boolean manageBarsPatch(String orderID, String reference) {
		boolean ret = false;
		String orderSource = "Mobile_Service_010_Manage_Bars";
		String orderType = "Mobile_Service_010_Manage_Bars";
		String priority = "5";
		String namespace = "VFUK_OSM_SOM_RFS_NewCo_Solution";
		String version = "15.3.1.0.0";
		
		ret = copyOrder(orderID, orderSource, orderType, reference, priority, namespace, version);
		if(ret)
			ret = completeOrder(rOrderId, rOrderHistId, "submit");
		else
			logger.error("copyOrder failed");
		return ret;
	}
}

