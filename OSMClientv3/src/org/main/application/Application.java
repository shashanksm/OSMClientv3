package org.main.application;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.main.utils.CommonUtils;
import org.main.utils.FileInputExtractionUnit;
import org.main.utils.FileOutputPublishingUnit;
import org.main.logic.WorkaroundsAPI;
import org.main.logic.WorkaroundsAPIImpl;

public class Application {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length == 2){
			
			String path = args[1];
			List<String> unparsedInputStrings = new ArrayList<String>();
			
			FileInputExtractionUnit.readLinesFromFile(path, unparsedInputStrings);
			
			String orderID = null;
			String task = null;
			String orderView = null;
			String tilStatus = null;
			String username = null;
			String password = null;
			
			
			//your processing goes here
			
			Set<String> inputSet = new LinkedHashSet<String>(unparsedInputStrings);
			
			WorkaroundsAPI workaroundsAPI = new WorkaroundsAPIImpl();
			workaroundsAPI.startup();
			String operation = args[0];
			
			switch(operation){
				case "repush":
				//some processing
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							task = parsedStrings[1];
							boolean result = workaroundsAPI.repush(orderID,task);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
			
				case "skip":
					//some processing
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							task = parsedStrings[1];
							boolean result = workaroundsAPI.skip(orderID,task);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
				case "daviesm5":
					//some processing
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							
							boolean result = workaroundsAPI.daviesm5(orderID);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "abort":
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							boolean result = workaroundsAPI.abortOrder(orderID);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "vampirePatch":
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							String imei = parsedStrings[1];
							boolean result = workaroundsAPI.vampirePatch(orderID,imei);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "abortRecreateSOM":
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							
							boolean result = workaroundsAPI.abortRecreateSOM(orderID);
							
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
				case "abortRecreateGeneric":
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							
							boolean result = workaroundsAPI.abortRecreateGeneric(orderID);
							
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
				case "createOrder":
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							orderView = parsedStrings[1];
							boolean result = workaroundsAPI.createOrder(orderID,orderView);
							
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "ICCIDPatch":
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							String iccid = parsedStrings[1];
							boolean result = workaroundsAPI.iccidPatch(orderID, iccid);
							
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "createOrderSOM":
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							boolean result = workaroundsAPI.createOrder(orderID,"SOM_ProvisionOrderFulfillment_OrderDetails");
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "abortRecreateWithoutReconnectionFlag":
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							
							boolean result = workaroundsAPI.abortRecreateWithoutReconnectionFlag(orderID);
							
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "updateTILStatus" :
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							orderView = parsedStrings[1];
							tilStatus = parsedStrings[2];
							boolean result = workaroundsAPI.updateTILstatus(orderID, orderView, tilStatus);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "updateOnCURWorkaround" :
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							orderView = parsedStrings[1];
							tilStatus = parsedStrings[2];
							boolean result = workaroundsAPI.updateOnCURWorkaround(orderID, orderView, tilStatus);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					case "updateOnCURBBPatch" :
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							orderView = parsedStrings[1];
							tilStatus = parsedStrings[2];
							boolean result = workaroundsAPI.updateOnCURBBPatch(orderID, orderView, tilStatus);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "updateUsernamePassword" :
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							username = parsedStrings[1];
							password = parsedStrings[2];
							boolean result = workaroundsAPI.updateUserNamePassword(orderID, username, password);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "addServiceIDPatch" :
					System.out.println("here");
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							task = parsedStrings[1];
							password = parsedStrings[2];
							boolean result = workaroundsAPI.addServiceIDPatch(orderID, task, password);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "pppUsernamePasswordPatch" :
					
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							task = parsedStrings[1];
							username = parsedStrings[2];
							password = parsedStrings[3];
							boolean result = workaroundsAPI.pppUsernamePasswordPatch(orderID, task,username, password);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "skipToComplete" :
					workaroundsAPI.skipToComplete(unparsedInputStrings);
					break;
				case "skipNetworkTasks" :
					workaroundsAPI.skipNetworkTasks(unparsedInputStrings);
					break;
				case "getLastTask" :
					for(String unparsedInputString:unparsedInputStrings){
						workaroundsAPI.getLastTask(unparsedInputString);
						System.out.println(workaroundsAPI.printLastTask());
						FileOutputPublishingUnit.publishLastTask(workaroundsAPI.printLastTask());	
					}
					break;
					
				case "repushAll" :
					String reference = "";
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							String unparsedString = (String)inputSet.toArray()[i];
							String[] parsedStrings = unparsedString.split("[\t,]+");
							reference = parsedStrings[0];
							boolean result = workaroundsAPI.repush(reference);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+reference+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
				case "getOrderByTask":
					String unparsedString = (String)inputSet.toArray()[0];
					String[] parsedStrings = unparsedString.split("[\t,]+");
					orderID = parsedStrings[0];
					task = parsedStrings[1];
					workaroundsAPI.getOrderByTask(orderID, task);
					break;
				case "getPTPIDs":
					for(String unparsedInputString:unparsedInputStrings){
						FileOutputPublishingUnit.publishLastTask(workaroundsAPI.getPTPIDs(unparsedInputString));	
					}
					break;
				case "getASAPError":
					for(String unparsedInputString:unparsedInputStrings){
						String[] oo = unparsedInputString.split("[\t,]+");
						FileOutputPublishingUnit.publishLastTask(workaroundsAPI.getASAPError(oo[0],oo[1]));	
					}
					break;
					
				case "manageBarsPatch" :
					
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						reference = "";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							unparsedString = (String)inputSet.toArray()[i];
							parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							reference = "MANAGE_BARS_PATCH_"+orderID;
							boolean result = workaroundsAPI.manageBarsPatch(orderID, reference);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status+","+reference+",");
					}
					break;
					
					case "fluimpatch" :
					
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						reference = "";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							unparsedString = (String)inputSet.toArray()[i];
							parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							String product = parsedStrings[1];
							String action = parsedStrings[2];
							boolean result = workaroundsAPI.fluimpatch(orderID,product,action);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status+","+reference+",");
					}
					break;
					
					case "failed_FL_Orders" :
						
						for(int i = 0; i<inputSet.size(); i++){
							String status = "unsuccessful";
							reference = "";
							if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
								System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
								
								unparsedString = (String)inputSet.toArray()[i];
								parsedStrings = unparsedString.split("[\t,]+");
								orderID = parsedStrings[0];
								String product = parsedStrings[1];
								String action = parsedStrings[2];
								boolean result = workaroundsAPI.fluimpatch(orderID,product,action);
								if(result){
									status="successful";
								}
							}else{
								status="unsuccessful";
							}
							System.out.println("Status : "+status);
							
							FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status+","+reference+",");
						}
						break;
					
					case "TimerDelayWA" :
					
					for(int i = 0; i<inputSet.size(); i++){
						String status = "unsuccessful";
						if(inputSet.toArray()[i] != null && !inputSet.toArray()[i].equals("")){
							System.out.println("applying workaround for order-id : "+(String)inputSet.toArray()[i]);
							
							unparsedString = (String)inputSet.toArray()[i];
							parsedStrings = unparsedString.split("[\t,]+");
							orderID = parsedStrings[0];
							
							boolean result = workaroundsAPI.timerDelayWA(orderID);
							if(result){
								status="successful";
							}
						}else{
							status="unsuccessful";
						}
						System.out.println("Status : "+status);
						
						FileOutputPublishingUnit.publishData(System.getProperty("user.name")+","+orderID+","+operation+","+CommonUtils.getDate()+","+status);
					}
					break;
					
			}
			
			
			
			workaroundsAPI.cleanup();
		}else{
			System.out.println("usage : java -jar <jar name> <operation> \"<input file name>\"");
		}
	}
}
