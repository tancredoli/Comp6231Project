import java.awt.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import org.omg.CORBA.ORB;
import org.omg.CORBA.PUBLIC_MEMBER;

import CorbaApp.DEMSInterfPOA;

import java.util.ArrayList;
import java.util.Date;

public class DEMSImpl extends DEMSInterfPOA{  //one local server function implement
	private ORB orb;

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}
	
	class CusClient {		//Customer
		String customerID = "";
		int[] takeEventTypeArray = { 0, 0, 0 }; // 0:conference 1:tradeshow  2:seminar
		HashMap<String, String> bookedEvent=new HashMap<>(); // key:courseID+Type value: Type
	}

	class Event {
		public String eventID = "";
		public int capacity = 100;
		public int cusNumb = 0; // booked customers number
		public LinkedList<String> bookedCus = new LinkedList<>(); //Customers in this event
		public String topic = "";
		public boolean avaialbe = true;
	}

	class ManClient {		//Manager
		String managerID = "";
	}
    
	boolean oldNotLocal = false;
	boolean removeflag = false;
	LinkedList<CusClient> cusClients = new LinkedList<>();
	LinkedList<ManClient> manClients = new LinkedList<>();


	HashMap<String, HashMap<String, Event>> eventMap = new HashMap<String, HashMap<String, Event>>();

	private String City = "";

	protected DEMSImpl() throws RemoteException {
		super();

	}

	public static void Log(String Id, String psw) throws IOException {

		String path ="D://eclipse-workspace/DEMS1.3/ServerLog/"+ Id+".txt";
		FileWriter fileWriter = new FileWriter(path,true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		bufferedWriter.write(psw + "\n");
		bufferedWriter.close();
	}

	public String getDate() {
		Date date = new Date();
		long times = date.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timeString = format.format(date);
		return timeString;
	}

	public void StartServer(String C)  {
		City = C;
		try {
			Log(City, getDate() + " Server for " + C + " started");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean manLogin(String manID) {

		Boolean login = false;

		for (int i = 0; i < manClients.size(); i++) {
			if (manClients.get(i).managerID.equals(manID)) {
				login = true;
				break;
			}
		}

		if (!login) {
			ManClient manager = new ManClient();
			manager.managerID = manID;
			manClients.add(manager);
		}

		System.out.println("Manager: " + manID + " login");

		try {
			Log(City, getDate()+" Manager: " + manID + " login.");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;

	}

	public boolean cusLogin(String cusID)  {

		Boolean exist = false;

		for (int i = 0; i < cusClients.size(); i++) {
			if (cusClients.get(i).customerID.equals(cusID)) {
				exist = true;
				break;
			}
		}

		if (!exist) {
			CusClient customer = new CusClient();
			customer.customerID = cusID;
			cusClients.add(customer);
		}
		System.out.println("Customer: " + cusID + " Login");

		try {
			Log(City, getDate()+ " Customer: " + cusID + "Login");
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return true;

	}

	@Override
	public boolean addEvent(String eventID, String eventType, String manID, int capacity)
    {

		Boolean create = false;
		String eventCity=eventID.substring(0, 3);
		String manCity=manID.substring(0, 3);
		if(eventCity.equals(manCity)) {
		
			if(addEventLocal(eventID, eventType, manID, capacity)) {
				create=true;
				try {
					System.out.println( "manager " + manID + " add event " + eventID + " in " + eventType);
					Log(City, getDate()+ " manager " + manID + " add event " + eventID + " in " + eventType);
				} catch (Exception e) {
					e.printStackTrace();
				}
						
			}
		}
		if(!create) {
			try {
				System.out.println(" manager " + manID + " failed to add this event " + eventID + " in " + eventType);
				Log(City, " manager " + manID + " failed to add this event " + eventID + " in " + eventType);
			} catch (Exception e) {
				e.printStackTrace();
			}
				
		}
		return create;
	}

	public boolean addEventLocal(String eventID, String eventType, String manID, int capacity) {
		synchronized(this) {
			if (eventMap.containsKey(eventType)) {               //all event types are in eventMap
				if (eventMap.get(eventType).containsKey(eventID)) {      //this event already in the eventMap
					Event curEvent1= new Event();
					curEvent1=eventMap.get(eventType).get(eventID);
					curEvent1.capacity++;		//capacity+1
					System.out.println( "This event " + eventID + " in " + eventType+" has already existed! Its capcaity plus one!");
					try {
						Log(City, getDate()+ " manager " + manID + "made the event " + eventID 
								+ " in " + eventType + " type, its capacity has plus one");
					} catch (Exception e) {
						e.printStackTrace();
					}				
					return false;
				} else {                 //this event not exist in the eventMap
					Event curEvent = new Event();
					curEvent.eventID = eventID;
					curEvent.capacity = capacity;
					/*
					for (int i = 0; i < eventDetail.size(); i++) {
						String curInf = eventDetail.get(i).toString();

						if (curInf.contains("capacity")) {
							int cap = 100;
							String[] cur = curInf.split(" ");
							if (cur.length > 1) {
								cap = Integer.valueOf(cur[1]);
							}
							curEvent.capacity = cap;
						}

						if (curInf.contains("topic")) {
							String topic = curInf.replaceAll("topic", "");
							curEvent.topic = topic;
						}

					}
					*/
					eventMap.get(eventType).put(eventID, curEvent);   

					try {
						Log(City, getDate()+ " manager " + manID + " add event " + eventID + " in " + eventType);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return true;

				}
			} else {   //eventMap does not have this type
				HashMap<String, Event> eventInf = new HashMap<String, Event>();
				Event curEvent = new Event();
				curEvent.eventID = eventID;
				curEvent.capacity = capacity;
				/*
				for (int i = 0; i < eventDetail.size(); i++) {
					String curInf = eventDetail.get(i).toString();

					if (curInf.contains("capacity")) {
						int cap = 100;
						String[] cur = curInf.split(" ");
						if (cur.length > 1) {
							cap = Integer.valueOf(cur[1]);
						}
						curEvent.capacity = cap;
					}

					if (curInf.contains("topic")) {
						String topic = curInf.replaceAll("topic", "");
						curEvent.topic = topic;
					}

				}
				*/
				eventInf.put(eventID, curEvent);
				eventMap.put(eventType, eventInf);
				return true;
			
			}
		}
			
	}
	
	

	@Override
	public boolean removeEvent(String eventID, String eventType, String manID) {
		boolean remove = false;
		int number = 0;
		if (eventID.substring(0, 3).equals(manID.substring(0, 3))) {
			synchronized (this) {
				if (eventMap.containsKey(eventType)) {
					if (eventMap.get(eventType).containsKey(eventID)) {
						Event curEvent = new Event();
						curEvent = eventMap.get(eventType).get(eventID);
						number = curEvent.cusNumb; // if customers booked this event

						if (number != 0) {

							while (curEvent.bookedCus.size() > 0) {

								removeflag = false;
								if (!(manID.substring(0, 3).equals(curEvent.bookedCus.get(0).substring(0, 3)))) {
									removeflag = true;
								}

								cancelEvent(curEvent.bookedCus.get(0), eventID, eventType); // call the cancelEvent
																							// function to delete
																							// customer booked in this
																							// event
							}

							/*
							 * 
							 * for (int i = 0; i < curEvent.bookedCus.size(); ++i) {
							 * System.out.println("Customer need to remove:"+i+":::"+curEvent.bookedCus.get(
							 * i)); removeflag=false; if(!(manID.substring(0,
							 * 3).equals(curEvent.bookedCus.get(i).substring(0, 3)))) { removeflag=true; }
							 * cancelEvent(curEvent.bookedCus.get(i), eventID,eventType); // call the
							 * cancelEvent function to delete customer booked in this event }
							 */
						}
						eventMap.get(eventType).remove(eventID); // remove that record
						remove = true;

						try {
							System.out.println(
									" manager " + manID + " successfully remove event " + eventID + " in " + eventType);
							Log(City, getDate() + " manager " + manID + " successfully remove event " + eventID + " in "
									+ eventType);
						} catch (Exception e1) {
							e1.printStackTrace();
						}

					}

				}
			}
		}
		if (!remove) {
			try {
				System.out.println(" manager " + manID + " failed to remove event " + eventID + " in " + eventType);
				Log(City, getDate() + " manager " + manID + " failed to remove event " + eventID + " in " + eventType);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return remove;

	}
	

	public String listEventALocal(String eventType) {  
		String result = " ";
		String curResult="";

		synchronized (this) {
			if (eventMap.containsKey(eventType)) {
				HashMap<String, Event> curTypeMap = new HashMap<>();
				curTypeMap = eventMap.get(eventType);
				Iterator iterator = curTypeMap.keySet().iterator();
				while (iterator.hasNext()) {
					int restNum = 0;
					Event curEvent = new Event();
					curEvent = curTypeMap.get(iterator.next());
					restNum = curEvent.capacity - curEvent.cusNumb;
					curResult =curResult+ curEvent.eventID + " " + Integer.toString(restNum) + ", ";
				}
			}
		}
		//System.out.println("Done, the result of Event Availability in "+City+" city is "+ curResult);
		result=curResult;
		return result;
	}

	@Override
	public String listEventA(String manID, String eventType) {

		String result = eventType + " - " + listEventALocal(eventType);
		String city = manID.substring(0, 3);
		String command = "listEventA(" + eventType + ")";    

		try {
			if (city.equals("TOR")) {
				int serverport1 = 2223; // MTL
				int serverport2 = 3334; // OTW
				
				System.out.println("Searching for MTL event availbility");
				String result1=UDPRequest.UDPlistEventA(command, serverport1);
				
				System.out.println("Searching for OTW event availbility");
				String result2=UDPRequest.UDPlistEventA(command, serverport2);
				
				result = result + " " + result1+" "+result2;
				System.out.println("DEMS ListEventA TORresult: "+result);
				
			} else if (city.equals("MTL")) {
				int serverport3 = 1112; // TOR
				int serverport4 = 3334; // OTW
				System.out.println("Searching for TOR event availbility");
				String result3 =  UDPRequest.UDPlistEventA(command, serverport3);
				
				System.out.println("Searching for OTW event availbility");
				String result4 = UDPRequest.UDPlistEventA(command, serverport4);
				
				result =result + " "+ result3 +" "+result4;
				System.out.println("DEMS ListEventA MTLresult: "+result);
				
			} else {
				int serverport5 = 1112; // TOR
				int serverport6 = 2223; // MTL
				
				System.out.println("Searching for TOR event availbility");
				String result5 = UDPRequest.UDPlistEventA(command, serverport5);
				
				System.out.println("Searching for MTL event availbility");
				String result6 = UDPRequest.UDPlistEventA(command, serverport6);
				
				result =result + " "+ result5 +" "+result6;
				System.out.println("DEMS ListEventA OTWresult: "+result);
			}

		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			Log(City, getDate() + " Manager ID " + manID + " check event aviability in " + eventType
					+" the result is"+ result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("we are in DEMSImpl: listEventA:  "+result);
		return result;
	}

	@Override
	public boolean bookEvent(String cusID, String eventID, String eventType) {
		String cusCity=cusID.substring(0, 3);
		String eventCity=eventID.substring(0,3);
		String eventmonth=eventID.substring(6,8);
		String eventyear=eventID.substring(8);
		String command = "bookEvent(" + cusID + "," + eventID + "," + eventType + ")" ;
		int otherCityNumb=0;
		
		String result=" ";

		String curType=eventType.toLowerCase();
		boolean exist=false;
		
		for(int i=0;i<cusClients.size();i++) {
			if(cusClients.get(i).customerID.equals(eventType)) {
				exist=true;
			}
		}
		if (!exist) {
			CusClient newCus=new CusClient();
			newCus.customerID=cusID;
			cusClients.add(newCus);
		}
		
		//System.out.println("The size of cusClients : "+cusClients.size());
		for(int i=0;i<cusClients.size();i++) {
			if(cusClients.get(i).customerID.equals(cusID) ){     //when customer login, it will have record there
				String mapKey=eventID+eventType;
				if(cusClients.get(i).bookedEvent.containsKey(mapKey)) {
					System.out.println("Customer already in this event.");
				}else {				
					try {
						String curResult=" ";
						if(cusCity.toLowerCase().equals(eventCity.toLowerCase())) {
							curResult = insertBook(cusID, eventID, curType);   // if the customer is own city customer

						}else {
							HashMap<String, String> curBookedMap=new HashMap<>();  //if the customer is booked in other cities
							curBookedMap=cusClients.get(i).bookedEvent;
							Iterator<String> iterator=curBookedMap.keySet().iterator();

							while(iterator.hasNext()) {     
								String key = iterator.next();
								String curc=key.substring(0, 3);          //eventID's city
								String curm=key.substring(6, 8); 
								String cury=key.substring(8,10);
								
								if(!curc.equals(cusCity)&&(cury.equals(eventyear))&&(curm.equals(eventmonth))) {
									otherCityNumb++;
								}
							}
							//System.out.println(cusID+" booked in "+otherCityNumb+" events in other cities");
							
							if( (oldNotLocal==false &&otherCityNumb<3)||(oldNotLocal==true &&otherCityNumb<=3)) {		//less than 3
								
								if(eventCity.equals("TOR")) {
									System.out.println("We are going to use TOR server");
									curResult = UDPRequest.UDPbookEvent(command, 1112);  //TOR
								}
								else if(eventCity.equals("MTL")) {
									System.out.println("We are going to use MTL server");
									curResult= UDPRequest.UDPbookEvent(command, 2223);    //MTL
								}
								else {
									System.out.println("We are going to use OTW server");
									curResult= UDPRequest.UDPbookEvent(command, 3334);     //OTW
								}
	                        }
							else { //the number of other cities' events in one month will be more than 3
								System.out.println(cusID+" cannot book more than 3 events in other cities in one month!");
								result=" ";
								break;
							}
						}
						if(!curResult.equals(" ")) {
							result=bookEventLocal(cusID, eventID, curType);
							
						}
						
					} catch (Exception e) {			
					
					}
			
				}
			break;
			}
			
		}	

		if(result.equals(" ")){
			System.out.println("Customer " + cusID + " failed to booked "+ eventID+ ". " );
			try {
				Log(City, getDate() + " Customer " + cusID + " failed to booked "+ eventID+ ". " );
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}else {
			System.out.println("Customer "+ cusID+" booked in this event "+eventID);
			try {
				Log(City, getDate()+" Customer "+ cusID+" successfully booked in this event "+eventID);
			}catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

	}


	public String bookEventLocal(String cusID, String eventID, String eventType) {
		
		String result=" ";

			synchronized(this) {
				for (int i = 0; i < cusClients.size(); i++) {
					if (cusClients.get(i).customerID.equals(cusID)) {
						String mapKey=eventID+eventType;
						cusClients.get(i).bookedEvent.put(mapKey, eventType);     // put event in map
																				//Update customer's info
						if (eventType.equals("conference")) {
							cusClients.get(i).takeEventTypeArray[0]++;    //event in that type add 1

						} else if (eventType.equals("tradeshow")) {
							cusClients.get(i).takeEventTypeArray[1]++;
						} else {
							cusClients.get(i).takeEventTypeArray[2]++;
						}
						
						result="success";
						//System.out.println("The result is "+ result+ " This message is from bookEventLocal");
						
						return result;

					}
				}
			}
			//System.out.println("The result of the bookEventLocal is"+ result+" This message from bookEventLocal");
			return result;
			
	}	
		

	
	public String insertBook(String cusID, String eventID, String eventType){     //need to return a string for book result
		
		//System.out.println("We are at the insertBook function and want to put the customer in it.");
		String result=" ";
		synchronized (this) {
			//System.out.println("Trying to book in insertBook "+ City);
			if (eventMap.containsKey(eventType)) {       //which city eventMap
				if (eventMap.get(eventType).containsKey(eventID)) {   					
					if(eventMap.get(eventType).get(eventID).avaialbe) {
//						System.out.println("This event is available. ");
						eventMap.get(eventType).get(eventID).bookedCus.add(cusID);	//Update event's info
						eventMap.get(eventType).get(eventID).cusNumb++;
						if(eventMap.get(eventType).get(eventID).cusNumb==eventMap.get(eventType).get(eventID).capacity) {
							eventMap.get(eventType).get(eventID).avaialbe=false;
						}
						result="success";
					}
				}
			}
		}
		return result;
	}
	

	@Override
	public boolean cancelEvent(String cusID, String eventID, String eventType)  {

		String cusCity = cusID.substring(0, 3);
		String eventCity = eventID.substring(0, 3);
		String result = " ";
		String type = " ";
		String mapKey = eventID + eventType;

		String commandc = "cancelForCustomer(" + cusID + "," + mapKey + ")";
		if (cusCity.equals(eventCity)) {
			type = cancelForCustomer(cusID, mapKey);
		} else {
			try {
				if (cusCity.equals("TOR")) {
					type = UDPRequest.UDPcancelForCustomer(commandc, 1112); // TOR
				} else if (cusCity.equals("MTL")) {
					type = UDPRequest.UDPcancelForCustomer(commandc, 2223); // MTL
				} else {
					type = UDPRequest.UDPcancelForCustomer(commandc, 3334); // OTW
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (type.equals(" ")) {
			return false;
		} else {

			String command = "cancelEvent(" + cusID + "," + eventID + "," + type + ")";
			if (cusCity.equals(eventCity)) { // Same city
				result = cancelEventLocal(cusID, eventID, type);
			}

			else if (removeflag == true) {
				result = cancelEventLocal(cusID, eventID, type);
			}

			else {

				try {
					if (eventCity.equals("TOR")) {
						result = UDPRequest.UDPcancelEvent(command, 1112); // TOR
					} else if (eventCity.equals("MTL")) {
						result = UDPRequest.UDPcancelEvent(command, 2223); // MTL
					} else {
						result = UDPRequest.UDPcancelEvent(command, 3334); // OTW
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		if (result.equals(" ")) {
			System.out.println("customerClient " + cusID + " failed to cancel " + eventID + " event.");
			try {
				Log(City, getDate() + "Customer " + cusID + " failed to cancel " + eventID + " event.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		} else {
			// System.out.println(result);
			try {
				Log(City, getDate() + " Customer " + cusID + " success to cancel " + eventID + " event.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

	}

	public String cancelEventLocal(String cusID, String eventID, String eventType) {
		String result=" ";
		synchronized (this) {
			eventMap.get(eventType).get(eventID).cusNumb--;
			int index=eventMap.get(eventType).get(eventID).bookedCus.indexOf(cusID);

			eventMap.get(eventType).get(eventID).bookedCus.remove(index); 	//remove event from local eventMap
			if(!eventMap.get(eventType).get(eventID).avaialbe) {
				eventMap.get(eventType).get(eventID).avaialbe=true;
			}
			result="success";
			
		}
		return result;
	}
	
	public String cancelForCustomer(String cusID, String eventID) {
		String type=" ";
		synchronized (this) {

		for (int i = 0; i < cusClients.size(); i++) {
			if (cusClients.get(i).customerID.equals(cusID)) {
				if (cusClients.get(i).bookedEvent.containsKey(eventID)) {

					type = cusClients.get(i).bookedEvent.get(eventID).toLowerCase();
					if (type.equals("conference")) {
						cusClients.get(i).takeEventTypeArray[0]--;
					} else if (type.equals("tradeshow")) {
						cusClients.get(i).takeEventTypeArray[1]--;
					} else {
						cusClients.get(i).takeEventTypeArray[2]--;
					}

					cusClients.get(i).bookedEvent.remove(eventID, type); 	//remove event from customer's info
				}
			}
		}
		}
		return type;
	  }

	@Override
	public String getBookingSchedule(String cusID)  {

		String result = " ";	
		for (int i = 0; i < cusClients.size(); i++) {
			if (cusClients.get(i).customerID.equals(cusID)) {
				
				for(String key:cusClients.get(i).bookedEvent.keySet()) {
					String eid=key.substring(0,10);
					result +=cusClients.get(i).bookedEvent.get(key)+": "+eid+"  ";
				}				
			}
		}
		//System.out.println(result);
		try {
			Log(City, getDate() + " Customer " + cusID + " booking schedule is "+ result);
		}catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}
	
	@Override
	public boolean swapEvent(String cusID, String newEventID, String newEventType, String oldEventID,
			String oldEventType){
		// TODO Auto-generated method stub
		// synchronized (this) {
		boolean result = false;
		oldNotLocal = false;
		String cusCity = cusID.substring(0, 3);
		String oldEventCity = oldEventID.substring(0, 3);

		if (!cusCity.equals(oldEventCity)) {
			oldNotLocal = true;
		}
		if (bookEvent(cusID, newEventID, newEventType)) {
			result = cancelEvent(cusID, oldEventID, oldEventType);
			if (result == false) {
				cancelEvent(cusID, newEventID, newEventType);
			}
		}
		return result;
		// }
	}

}
