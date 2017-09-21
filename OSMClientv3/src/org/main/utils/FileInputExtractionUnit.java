/**
 * 
 */
package org.main.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author shash
 *
 */
public abstract class FileInputExtractionUnit {
	
	private static final Logger logger = LogManager.getLogger(FileInputExtractionUnit.class.getName());
	
	public static boolean readLinesFromFile(final String path, List<String> unparsedInputStrings){
		logger.traceEntry();
		boolean ret = false;
		
		unparsedInputStrings.clear();
		
		BufferedReader reader = null;
		FileReader fileReader = null;
		File file;
		
		file = new File(path);
		
		if(file.isFile()){
			try {
				
				fileReader = new FileReader(file);
				reader = new BufferedReader(fileReader);
				
				String line = "";
				
				while((line=reader.readLine())!=null){
					unparsedInputStrings.add(line);
				}	
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				logger.error("FileNotFoundException : "+e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("IOException : "+e.getMessage());
			}finally {
				try {
					fileReader.close();
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			logger.error("specified argument is not a file (may or may not be a directory though)");
		}
		
		
		logger.traceExit();
		return ret;
	}
}
