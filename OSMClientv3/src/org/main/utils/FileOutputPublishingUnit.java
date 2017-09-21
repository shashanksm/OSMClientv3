/**
 * 
 */
package org.main.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author shash
 *
 */
public class FileOutputPublishingUnit {
	private static Logger logger = LogManager.getLogger(FileOutputPublishingUnit.class.getName());
	private static Logger tasklogger = LogManager.getLogger("LastTaskLogger");
	public static void publishData(String message){
		logger.trace(message);
	}
	public static void publishLastTask(String message){
		tasklogger.trace(message);
	}
}
