package org.main.logic;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface WorkaroundsAPI {
	
	public boolean startup();
	public boolean cleanup();
	
	
	//OSM WebService API
	public void getOrder(String orderId, String view);  //Developed for testing purposes
	public boolean abortOrder(String orderId);
	public boolean abortRecreateSOM(String orderId);
	public boolean abortRecreateWithoutReconnectionFlag(String orderId);
	public boolean createOrder(Document responseDocument);
	public boolean createOrder(String orderId, String view);
	public boolean updateTILstatus(String orderId, String view, String targetStatus);
	public boolean updateUserNamePassword(String orderId, String username, String password);
	public boolean updateOrderwithUpdateData(String orderId, String view, String data, String content);
	public boolean updateOrderwithDeleteData(String orderId, String view, String data);
	public boolean updateOrderwithAddData(String orderId, String view, String path, String data);
	public boolean getCreationView(String orderSource, String orderType, String namespace, String version);
	public void getOrderByTask(String orderId, String task);
	
	//Repush API
	public boolean getOrderAtTask(String orderId, String task);
	public boolean getTaskStatuses(String task, String namespace, String version);
	public boolean assignOrder(String orderId, String orderhistId, String user);
	public boolean completeOrder(String orderId, String orderhistId, String status);
	public boolean receiveOrder(String orderId, String orderhistId);
	public boolean acceptOrder(String orderId, String orderhistId);
	public boolean updateOrderAddNode(String orderId, String view, String path, String nodeName, String nodeValue);
	public boolean copyOrder(String originalOrderID, String orderSource, String orderType, String reference, String priority, String namespace, String version);
	
	
	//Final Repush Methods
	public boolean repush(String orderId, String task);
	public boolean repush(String reference);
	public boolean skip(String orderId, String task);
	
	//Useful utility API
	public boolean getLastTask(String reference);
	public String printLastTask();
	
	//The Ultimate Goal
	public void skipToComplete(List<String> references);
	
	//Adding one more
	public void skipNetworkTasks(List<String> references);
	
	//Misc
	public String getPTPIDs(String reference);
	public boolean addServiceIDPatch(String orderId, String task, String serviceId);
	public boolean pppUsernamePasswordPatch(String orderId, String task, String uname, String pwd);
	public String getASAPError(String orderID, String task);
	public boolean updateOnCURWorkaround(String orderID, String task, String target);
	public boolean updateOnCURBBPatch(String orderID, String task, String target);
	public boolean manageBarsPatch(String orderID, String reference);
}
