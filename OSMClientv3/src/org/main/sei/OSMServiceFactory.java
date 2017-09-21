package org.main.sei;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.main.sei.OSMServiceImpl;

public class OSMServiceFactory {
	
	private static final Logger logger = LogManager.getLogger(OSMServiceFactory.class.getName());
	private String configPath;
	private OSMServiceImpl service;
	
	public static OSMServiceFactory newInstance(){
		logger.trace("New OSM Service Factory initiated");
		OSMServiceFactory instance = new OSMServiceFactory(); 
		instance.configPath = System.getProperty("user.dir")+"\\config\\OSMService-config.properties";
		
		FileOutputStream output = null;
		try {
			
			FileInputStream input = new FileInputStream(instance.configPath);
			Properties iprop = new Properties();
			iprop.load(input);
			String wsapi=iprop.getProperty("wsapi-path");
			String username=iprop.getProperty("user-name");
			String password=iprop.getProperty("password");
			String soap=iprop.getProperty("soap-action-path");
			input.close();
			
			output = new FileOutputStream(instance.configPath);
			Properties oprop = new Properties();
			oprop.setProperty("request-folder-path", new File(System.getProperty("user.dir")+"\\requests").getAbsolutePath());
			oprop.setProperty("wsapi-path", wsapi);
			oprop.setProperty("user-name", username);
			oprop.setProperty("password", password);
			oprop.setProperty("soap-action-path", soap);
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
	
	public OSMServiceImpl getOSMService(){
		service = null;
		
		logger.trace("OSMService being fetched.");
		InputStream input;
		
		try {
			input = new FileInputStream(new File(configPath));
			
			Properties iprop = new Properties();
			iprop.load(input);
			
			service = new OSMServiceImpl();
			service.setPassword(iprop.getProperty("password"));
			service.setRequestFolderPath(iprop.getProperty("request-folder-path"));
			service.setSoapActionPath(iprop.getProperty("soap-action-path"));
			service.setUsername(iprop.getProperty("user-name"));
			service.setWsapiPath(iprop.getProperty("wsapi-path"));
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
		logger.trace("OSMService fetched successfully");
		return service;
	}
	
	
}
