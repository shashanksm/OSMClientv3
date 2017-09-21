package org.main.sei;


import java.io.File;
import java.io.IOException;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class OSMServiceImpl implements OSMService{
	
	private String requestFolderPath;
	private String username;
	private String password;
	private String soapActionPath;
	private String wsapiPath;
	
	private static final Logger logger = LogManager.getLogger(OSMServiceImpl.class.getName());
	private static final CloseableHttpClient httpclient = HttpClients.createDefault();
	
	
	public String getRequestFolderPath() {
		return requestFolderPath;
	}

	public void setRequestFolderPath(String requestFolderPath) {
		this.requestFolderPath = requestFolderPath;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSoapActionPath() {
		return soapActionPath;
	}

	public void setSoapActionPath(String soapActionPath) {
		this.soapActionPath = soapActionPath;
	}

	public String getWsapiPath() {
		return wsapiPath;
	}

	public void setWsapiPath(String wsapiPath) {
		this.wsapiPath = wsapiPath;
	}
	
	@Override
	public HttpResponse sendRequest(HttpUriRequest request) {
		// TODO Auto-generated method stub
		CloseableHttpResponse response = null;
		logger.traceEntry();
		
		try {
			response = httpclient.execute(request);
			logger.trace("HTTP Status : "+response.getStatusLine());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		logger.traceExit();
		return response;
	}

	@Override
	public File getXmlRequestTemplate(String filename) {
		// TODO Auto-generated method stub
		return new File(requestFolderPath+"\\"+filename);
	}
	
	
	@Override
	public HttpPost prepareRequest(String requestBody, String soapAction){
		
		logger.trace("preparing header");
		logger.traceEntry();
		HttpPost httppost = new HttpPost(wsapiPath);
		
		
		String userpass = username+":"+password;
		String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
		
		
		httppost.addHeader("Authorization", basicAuth);
		httppost.addHeader("Content-Type","text/xml");
		httppost.addHeader("SOAPAction", soapActionPath+"/"+soapAction);
		httppost.addHeader("Operation", soapAction);
		
		ByteArrayEntity entity = new ByteArrayEntity(requestBody.getBytes());
		
		httppost.setEntity(entity);
		
		logger.traceExit();
		return httppost;
	}


}