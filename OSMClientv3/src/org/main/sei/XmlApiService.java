/**
 * 
 */
package org.main.sei;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * @author shash
 *
 */
public interface XmlApiService {
	public boolean login();
	public boolean logout();
	public HttpResponse sendXMLRequest(HttpUriRequest request);
	public File getXmlRequestTemplate(String filename);
	public HttpPost prepareRequest(String requestBody);
}
