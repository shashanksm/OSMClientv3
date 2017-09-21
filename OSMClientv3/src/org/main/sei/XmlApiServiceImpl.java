/**
 * 
 */
package org.main.sei;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author shash
 *
 */
public class XmlApiServiceImpl implements XmlApiService {

	/* (non-Javadoc)
	 * @see org.main.sei.XmlApiService#login()
	 */
	
	private static final Logger logger = LogManager.getLogger(XmlApiServiceImpl.class.getName());
	private static final CloseableHttpClient httpclient = HttpClients.createDefault();
	
	private String endPointUrl;
	private String username;
	private String password;
	private String sessionId;
	private boolean loggedIn = false;
	private String requestFolderPath;
	
	
	/**
	 * @return the requestFolderPath
	 */
	public String getRequestFolderPath() {
		return requestFolderPath;
	}

	/**
	 * @param requestFolderPath the requestFolderPath to set
	 */
	public void setRequestFolderPath(String requestFolderPath) {
		this.requestFolderPath = requestFolderPath;
	}

	public String getEndPointUrl() {
		return endPointUrl;
	}

	public void setEndPointUrl(String endPointUrl) {
		this.endPointUrl = endPointUrl;
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

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public boolean login() {
		// TODO Auto-generated method stub
		logger.trace("logging into xmlapi");
		boolean ret = false;
		HttpPost loginRequest = new HttpPost(endPointUrl+"/login");
		
		List<BasicNameValuePair> formParams = new ArrayList<BasicNameValuePair>();
		
		formParams.add(new BasicNameValuePair("username", username));
		formParams.add(new BasicNameValuePair("password", password));
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams,Consts.UTF_8);
		
		loginRequest.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			 response = httpclient.execute(loginRequest);
			 if(response.getStatusLine().getStatusCode() == 200){
				 sessionId = response.getFirstHeader("Set-Cookie").getValue().split(";")[0];
				 ret = true;
				 loggedIn = true;
				 logger.trace("session-id retrieved : "+sessionId);
			 }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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

	/* (non-Javadoc)
	 * @see org.main.sei.XmlApiService#logout()
	 */
	@Override
	public boolean logout() {
		// TODO Auto-generated method stub
		logger.trace("logging out of xmlapi");
		boolean ret = false;
		HttpPost logoutRequest = new HttpPost(endPointUrl+"/logout");
		logoutRequest.addHeader("Cookie", sessionId);
		
		
		CloseableHttpResponse response = null;
		try {
			 response = httpclient.execute(logoutRequest);
			 if(response.getStatusLine().getStatusCode() == 200){
				 ret = true;
				 loggedIn = false;
				 logger.trace("logged out");
			 }
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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

	/* (non-Javadoc)
	 * @see org.main.sei.XmlApiService#sendXMLRequest(org.apache.http.client.methods.HttpUriRequest)
	 */
	@Override
	public HttpResponse sendXMLRequest(HttpUriRequest request) {
		// TODO Auto-generated method stub
		CloseableHttpResponse ret = null;
		logger.trace("sending xml request");
		try {
			ret = httpclient.execute(request);
			return ret;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.main.sei.XmlApiService#getXmlRequestTemplate(java.lang.String)
	 */
	@Override
	public File getXmlRequestTemplate(String filename) {
		// TODO Auto-generated method stub
		return new File(requestFolderPath+"\\"+filename);
	}

	/* (non-Javadoc)
	 * @see org.main.sei.XmlApiService#prepareRequest(java.lang.String)
	 */
	@Override
	public HttpPost prepareRequest(String requestBody) {
		// TODO Auto-generated method stub
		HttpPost ret = null;
		
		ret = new HttpPost(endPointUrl);
		
		ret.addHeader("Cookie", sessionId);
		ret.addHeader("Content-Type","text/xml");
		
		ByteArrayEntity entity = new ByteArrayEntity(requestBody.getBytes());
		
		ret.setEntity(entity);
		
		return ret;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
}
