/**
 * 
 */
package org.main.sei;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author shash
 *
 */
public class XmlApiServiceFactory {
	private static final Logger logger = LogManager.getLogger(XmlApiServiceFactory.class.getName());
	private String configPath;
	private XmlApiServiceImpl service;
	
	public static XmlApiServiceFactory newInstance(){
		logger.trace("New xml api service Factory initiated");
		XmlApiServiceFactory instance = new XmlApiServiceFactory(); 
		instance.configPath = System.getProperty("user.dir")+"\\config\\xml-api-config.properties";
		if(SystemUtils.IS_OS_WINDOWS) {
			instance.configPath = System.getProperty("user.dir")+"\\config\\xml-api-config.properties";
		}else {
			instance.configPath = System.getProperty("user.dir")+"/config/xml-api-config.properties";
		}
		
		FileOutputStream output = null;
		try {
			
			FileInputStream input = new FileInputStream(instance.configPath);
			Properties iprop = new Properties();
			iprop.load(input);
			
			String username="admin";
			String password="password1";
			String xmlapi="";
			
			if(iprop.containsKey("user-name"))
				username=iprop.getProperty("user-name");
			
			if(iprop.containsKey("password"))
				password=iprop.getProperty("password");
			
			if(iprop.containsKey("end-point-url"))
				xmlapi=iprop.getProperty("end-point-url");
			
			input.close();
			
			
			output = new FileOutputStream(instance.configPath);
			
			Properties oprop = new Properties();
			
			oprop.setProperty("request-folder-path", new File(System.getProperty("user.dir")+"\\requests").getAbsolutePath());
			if(SystemUtils.IS_OS_WINDOWS) {
				instance.configPath = System.getProperty("user.dir")+"\\config\\xml-api-config.properties";
			}else {
				instance.configPath = System.getProperty("user.dir")+"/config/xml-api-config.properties";
			}
			oprop.setProperty("user-name", username);
			oprop.setProperty("password", password);
			oprop.setProperty("end-point-url", xmlapi);
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
		
		return instance;
	}
	
	public XmlApiServiceImpl getXmlServiceApi(){
		service = null;
		
		logger.trace("xml-api service being fetched.");
		InputStream input;
		
		try {
			input = new FileInputStream(new File(configPath));
			
			Properties iprop = new Properties();
			iprop.load(input);
			
			service = new XmlApiServiceImpl();
			service.setPassword(iprop.getProperty("password"));
			service.setRequestFolderPath(iprop.getProperty("request-folder-path"));
			service.setEndPointUrl(iprop.getProperty("end-point-url"));
			service.setUsername(iprop.getProperty("user-name"));
			input.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("Exception : FileNotFound");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Exception : IO");
			e.printStackTrace();
		}
		logger.trace("OSMService fetched successfully with user : "+service.getUsername() + " and password : "+service.getPassword());
		
		return service;
	}
}
