import java.rmi.Naming;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import CorbaApp.DEMSInterf;
import CorbaApp.DEMSInterfHelper;

import java.io.InputStreamReader;
import java.util.LinkedList;


public class ManagerClient {
	 static DEMSInterf cr;

    public static void Log(String ID,String Message) throws Exception{
		  
		String path = "D://eclipse-workspace/DEMS1.3/ClientLog/" + ID + ".txt"; 
		FileWriter fileWriter = new FileWriter(path,true);
		BufferedWriter bf = new BufferedWriter(fileWriter);
		bf.write(Message + "\n");
		bf.close();
	}
	
	public static String getDate(){
	    Date date = new Date();
	    long times = date.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String dateString = formatter.format(date);
	    return dateString;
	}
	
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static boolean isCustomer(String customerID) {
		String curCity=customerID.substring(0, 3);
		String lastFour=customerID.substring(4);
        
        if( customerID.charAt(3)=='C' && lastFour.length()==4 && isInteger(lastFour)) {
	         if(curCity.equals("TOR") ||curCity.equals("MTL")||curCity.equals("OTW") ) {
	        	      return true;
	        	 }
	         return false;
        }
       return false;
		
	}
	
	
	public static void main(String args[]){
		try {
	         int RMIPort;         
	         String hostName,portNum = " ";
	         InputStreamReader is = new InputStreamReader(System.in);
	         BufferedReader br = new BufferedReader(is);
	         
	         System.out.println("Enter ManagerID:");
	         Scanner Id = new Scanner(System.in);
	         String managerID = Id.nextLine();
	         String city = managerID.substring(0,3);
	         String lastFour=managerID.substring(4);
	         String name = " ";

	         
	         if( managerID.charAt(3)=='M' && lastFour.length()==4 && isInteger(lastFour)) {
		         if(city.equals("TOR")) {
		        	      portNum = "1111";
		        	      name = "TOR";
		        	 }
		         else if(city.equals("MTL")) {
		        	      portNum = "2222";
		        	      name = "MTL";
		        	 }
		         else if(city.equals("OTW")) {
		        	      portNum = "3333";
		        	      name = "OTW";
		        	 }else {
			        	 System.out.println("Invalid ManagerID");
			        	 System.exit(0);
			         }

	         }
	         else {
	        	 System.out.println("Invalid ManagerID");
	        	 System.exit(0);
	         }
	         
//	         RMIPort = Integer.parseInt(portNum);
//	         String registryURL = "rmi://localhost:" + portNum + "/DEMS-" + city;  

//	         DEMSInterf cr = (DEMSInterf)Naming.lookup(registryURL);
	         //System.out.println("Done " );
	         
	         ORB orb = ORB.init(args, null);
	
	         org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
	   
	         NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	         
	         cr = DEMSInterfHelper.narrow(ncRef.resolve_str(name));
	      
	         if(cr.manLogin(managerID)){
	        	 System.out.println("Login successfully");
	        	 Log(managerID, getDate() + " " + managerID + " login successfully");
	         }
	         else{
	        	 System.out.println("Login Unsuccessfully");
	        	 Log(managerID, getDate() + " " + managerID + " login failed");
	         }

	         while(true){
	        	 System.out.println(" ");
	        	 System.out.println("Please select an operation: ");
	        	 System.out.println("1: add Event");
	        	 System.out.println("2: remove Event");
	        	 System.out.println("3: list Event Availability");
	        	 System.out.println("4: book Event");
	        	 System.out.println("5: get booking Schedule");
	        	 System.out.println("6: cancel Event");
				 System.out.println("7: swap Events");
				 System.out.println("8: Exit" + "\n");
	        	 
	        	 Scanner s = new Scanner(System.in);
		         int input = s.nextInt();
	        	 switch (input) {
	        	 case 1: 	//1: add Event
	        		
	        		 System.out.println("Please Enter Event ID:(e.g. TORA070619)");
	        		 Scanner one = new Scanner(System.in);
	        		 String eventID = one.nextLine();
	        		 
	        		 System.out.println("Please Enter Event Type:(conference/tradeshow/seminar)");
	        		 Scanner two = new Scanner(System.in);
	        		 String eventType = two.nextLine();
	        		 
	        		 
	        		 System.out.println("Please Enter Capacity:");
	        		 Scanner three = new Scanner(System.in);
	        		 int cap=Integer.parseInt(three.nextLine());
/*
	        		 System.out.println("Please Enter Event Topic:");
	        		 Scanner four = new Scanner(System.in);
	        		 String topic = four.nextLine();
*/   		 
	        		 if((eventID.length()!=10)||(!eventID.substring(0, 3).equals("TOR")&&!eventID.substring(0, 3).equals("MTL")&&!eventID.substring(0, 3).equals("OTW"))
	        				 ||(!eventID.substring(3, 4).equals("M")&&!eventID.substring(3, 4).equals("E")&&!eventID.substring(3, 4).equals("A"))) {
	        			 System.out.println("Wrong EventID! Failed to add this Event!"); 
	        			 Log(managerID, getDate() + " " + managerID + " failed to add this event "+ eventID+
	        					 "in Type"+ eventType);
	        			 break;
	        		 }
/*	        		 
	        		 LinkedList<String> eventDetail = new LinkedList<>();
	        		 String first="capacity "+cap;
	        		 String second="topic"+topic;
        		 
	        		 eventDetail.add(first);
	        		 eventDetail.add(second);
*/	        		 		
	        		 if(cr.addEvent(eventID, eventType, managerID,  cap)) {	
	        			 System.out.println("Add this event successfully!");
	        			 Log(managerID, getDate() + " " + managerID + " successfully add event. " 
	        			 + "Event information: Type: " + eventType + " EventID: " + eventID + " Capacity: " + cap
	        			 );
	        			 break;
	        		 }
	        		 else{
	        			 System.out.println("Failed to add this Event!"); 
	        			 Log(managerID, getDate() + " " + managerID + " failed to add this event "+ eventID+
	        					 "in Type"+ eventType);
	        			 break;
	        		 }
	        		 
	        	 case 2:	//2: remove Event
	        		 
	        		 System.out.println("Please Enter Event ID:");
	        		 Scanner six = new Scanner(System.in);
	        		 String eID = six.nextLine();
	        		 
	        		 System.out.println("Please Enter Type:(conference/tradeshow/seminar)");
	        		 Scanner seven = new Scanner(System.in);
	        		 String eT = seven.nextLine();
	        		 

	        		 if(cr.removeEvent(eID,eT, managerID)){
	        			 System.out.println("Remove "+ eID +" successfully in "+ eT+ " !");
	        			 Log(managerID, getDate() + " " + managerID + " remove "+ eID +" successfully. " 
	        			 + "Remove information: Event: " + eID + " in: " + eT);
	        		 }
	        		 else{
	        			 System.out.println("Remove this event Unsuccessfully!");
	        			 Log(managerID, getDate() + " " + managerID + " remove this event in failed");
	        		 }
	        		 break;
	        		 
	        	 case 3:	//3: list Event Availability
	        		 
	        		 System.out.println("Please Enter Type:(conference/tradeshow/seminar)");
	        		 Scanner eight = new Scanner(System.in);
	        		 String curType = eight.nextLine();
	        		 System.out.println("Event Availibility of "+ curType+" :");
	        		 String result=cr.listEventA(managerID, curType);
	        		 System.out.println(result);
	        		 Log(managerID, getDate()+" list event aviablity. Detail: "+ result);
	        		 break; 
	        		 
	        	 case 4:	//4: book Event
	        		 System.out.println("Please Enter CustomerID: ");
	        		 Scanner nine1 = new Scanner(System.in);     		 
	        		 String cID = nine1.nextLine();
	        		 String cusCity=cID.substring(0,3);
	        		 
	        		 System.out.println("Please Enter EventID: ");
	        		 Scanner nine = new Scanner(System.in);     		 
	        		 String evenID = nine.nextLine();
	        		 
	        		 System.out.println("Please Enter Type:(conference/tradeshow/seminar)");
	        		 Scanner ten = new Scanner(System.in);     		 
	        		 String type = ten.nextLine();
	        		 
	        		 if((evenID.length()!=10)||(!evenID.substring(0, 3).equals("TOR")&&!evenID.substring(0, 3).equals("MTL")&&!evenID.substring(0, 3).equals("OTW"))
	        				 ||(!evenID.substring(3, 4).equals("M")&&!evenID.substring(3, 4).equals("E")&&!evenID.substring(3, 4).equals("A"))) {
	        			 System.out.println("Wrong EventID! Failed to add this Event!"); 
	        			 Log(managerID, getDate() + " " + managerID + " failed to book this event "+ evenID+
	        					 "in Type"+ type);
	        			 break;
	        		 }
	        		
	        		 if(isCustomer(cID)&& (cusCity.equals(city))) {		//Same city
	        		 System.out.println("The customer : "+cID+" The event is : "+ evenID+" the type is : "+ type);
		        		 if(cr.bookEvent(cID,evenID, type)){
		        			 System.out.println("Customer "+cID+" successfully booked in event "+ evenID);
		        			 Log(managerID,  getDate() + " Manager: "+managerID +" successfully let Customer: " + cID + " to booked in event " + evenID
		    	        			 +" in "+ type);
		        			 Log(cID,  getDate() + " Customer: " + cID + " successfully booked in event " + evenID+ " by manager :"+ managerID
		    	        			 +" in "+ type);
		        		 }
		        		 else {
		        			 System.out.println("Customer "+ cID+ " failed to book in event "+ evenID);
		        			 Log(managerID, getDate() +" Manager: "+managerID +" fail to let Customer: " + cID + "  book in event " + evenID
		    	        			 +" in "+ type);
		        			 Log(cID,  getDate() + " Customer: " + cID + " fail to booked in event " + evenID+ " by manager :"+ managerID
		    	        			 +" in "+ type);
		        		 }
	        		 }else {
	        			 System.out.println("Invalid CustomerID or Manager and Customer belong to different cities");
	        			 
	        		 }
	        		 break;
	        		 
	        	 case 5:	//5: get booking Schedule
	        		 System.out.println("Please Enter CustomerID: ");
	        		 Scanner one5 = new Scanner(System.in);     		 
	        		 String cusID5 = one5.nextLine();
	        		 String cus5City=cusID5.substring(0, 3);

	        		 if(isCustomer(cusID5)&&(cus5City.equals(city))) {		//Same city
		        		 String result5=cr.getBookingSchedule(cusID5);
		        		 System.out.println( cusID5+ "booking schedule: "+ result5);
	        			 Log(managerID, getDate() + " Manager: "+managerID+" got customer " + cusID5 + " booking schedule: " + result5);
	        			 Log(cusID5, getDate() + " Customer " + cusID5 + " booking schedule: " + result5+ " got by manager: "+managerID);
	        		 }else {
	        			 System.out.println("Invalid CustomerID or Manager and Customer belong to different cities");
	        		 }
        			 break;
        			 
	        	 case 6:	//6: cancel Event
	        		 System.out.println("Please Enter CustomerID: ");
	        		 Scanner one6 = new Scanner(System.in);     		 
	        		 String cusID6 = one6.nextLine();
	        		 String cus6City=cusID6.substring(0, 3);
	        		 
	        		 System.out.println("Please Enter Event ID: ");
	        		 Scanner two6 = new Scanner(System.in);     		 
	        		 String eID6 = two6.nextLine();
	        		 
	        		 System.out.println("Please Enter Type:(conference/tradeshow/seminar)");
	        		 Scanner three6 = new Scanner(System.in);     		 
	        		 String type6 = three6.nextLine();
	        		 
	        		 if(isCustomer(cusID6)&& cus6City.equals(city)) { 		//Same city
		        		 if(cr.cancelEvent(cusID6, eID6, type6)){
		        			 System.out.println("Customer "+cusID6+" successfully cancel event "+ eID6);
		        			 Log(managerID,  getDate() + " Manager: "+managerID+" successfuly let customer " + cusID6 + " to cancel event " + eID6);
		        			 Log(cusID6,  getDate() + " Customer " + cusID6 + " successfully cancel event " + eID6 +" by manager: "+managerID);
		    	        			 
		        		 }
		        		 else {
		        			 System.out.println("Customer "+ cusID6+ " failed to cancel event "+ eID6);
		        			 Log(managerID, getDate() + " Manager: "+managerID + " fail to let "+ cusID6 + " to cancel in event " + eID6);
		        			 Log(cusID6, getDate() + " Customer " + cusID6 + " failed to cancel in event " + eID6+ " by manager:"+managerID);
		    	        			
		        		 }
	        		 }else {
	        			 System.out.println("Invalid CustomerID or Manager and Customer belong to different cities");
	        		 }
	        		 break;
	 			case 7:
					System.out.println("Please Enter CustomerID: ");
					Scanner one7 = new Scanner(System.in);
					String cusID7 = one7.nextLine();
					String cus7City = cusID7.substring(0, 3);

					System.out.println("Please Enter New Event ID: ");
					Scanner two7 = new Scanner(System.in);
					String newID7 = two7.nextLine();

					System.out.println("Please Enter New Type:(conference/tradeshow/seminar)");
					Scanner three7 = new Scanner(System.in);
					String newType7 = three7.nextLine();

					System.out.println("Please Enter Old Event ID: ");
					Scanner four7 = new Scanner(System.in);
					String oldID7 = four7.nextLine();

					System.out.println("Please Enter Old Type:(conference/tradeshow/seminar)");
					Scanner five7 = new Scanner(System.in);
					String oldType7 = five7.nextLine();

					if (isCustomer(cusID7) && cus7City.equals(city)) { // Same city
						if (cr.swapEvent(cusID7, newID7, newType7, oldID7, oldType7)) {
							System.out.println("Customer " + cusID7 + " successfully swap old event " + oldID7
									+ " and new event " + newID7);
							Log(managerID, getDate() + " Manager: " + managerID + " successfuly let customer " + cusID7
									+ " to swap old event " + oldID7 + " and new event " + newID7);
							Log(cusID7, getDate() + " Customer " + cusID7 + " successfully swap old event " + oldID7
									+ " and new event " + newID7 + " by manager: " + managerID);

						} else {
							System.out.println("Customer " + cusID7 + " failed to swap old event " + oldID7
									+ " and new event " + newID7);
							Log(managerID, getDate() + " Manager: " + managerID + " fail to let " + cusID7
									+ " to swap old event " + oldID7 + " and new event " + newID7);
							Log(cusID7, getDate() + " Customer " + cusID7 + " failed to swap old event " + oldID7
									+ " and new event " + newID7);

						}
					} else {
						System.out.println("Invalid CustomerID or Manager and Customer belong to different cities");
					}

					break;
				case 8:
					System.exit(0);
	  		
				default:
					break;
				}
	        	 
	         }
	         
	      } 
	      catch (Exception e) {
	         System.out.println("Exception in ManagerClient: " + e);
	      } 
	}



}
