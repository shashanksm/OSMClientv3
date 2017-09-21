package org.main.sei;

import java.io.File;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;

public interface OSMService {
	public HttpResponse sendRequest(HttpUriRequest request);
	public File getXmlRequestTemplate(String filename);
	public HttpPost prepareRequest(String requestBody, String soapAction);
}
