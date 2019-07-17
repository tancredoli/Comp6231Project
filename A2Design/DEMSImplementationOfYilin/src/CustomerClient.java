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

public class CustomerClient {
	
public static void Log(String ID,String Message) throws Exception{
		
		String path = "D://eclipse-workspace/DEMS1.3/ClientLog/" +ID + ".txt";   
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
	
	
	public static void main(String[] args) {
		
		try {
	         int RMIPort;         
	         String hostName;
	         String portNum = " ";
	         String name = " ";
	                 
	         System.out.println("Enter customerID:");
	         Scanner Id = new Scanner(System.in);
	         String customerID = Id.nextLine();
	         String city = customerID.substring(0,3);

	         String lastFour=customerID.substring(4);
	         
	         if( customerID.charAt(3)=='C' && lastFour.length()==4 && isInteger(lastFour)) {
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
		        	 name ="OTW";
		         }else {
		        	 System.out.println("Invalid customerID");
		        	 System.exit(0);
		         }
	        
	         }
	         else {
	        	 System.out.println("Invalid customerID");
	        	 System.exit(0);
	         }
	         
//	         InputStreamReader is = new InputStreamReader(System.in);
//	         BufferedReader br = new BufferedReader(is);

	         RMIPort = Integer.parseInt(portNum);
//	         String registryURL = "rmi://localhost:" + portNum + "/DEMS-" + city;  

//	         DEMSInterf cr = (DEMSInterf)Naming.lookup(registryURL);
	         
	         ORB orb = ORB.init(args, null);
	         org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
	         NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	         DEMSInterf cr = (DEMSInterf) DEMSInterfHelper.narrow(ncRef.resolve_str(name));
	         
	         if(cr.cusLogin(customerID)){
	        	 System.out.println("Login successfully");
	        	 Log(customerID, getDate() + " " + customerID + " login successfully");
	         }
	         else{
	        	 System.out.println("Login failed");
	        	 Log(customerID, getDate() + " " + customerID + " login failed");
	         }
	         while(true){
	        	 System.out.println(" ");
	        	 System.out.println("Please select an operation: ");
	        	 System.out.println("1: book event");
	        	 System.out.println("2: get booking Schedule");
	        	 System.out.println("3: cancel event");
				 System.out.println("4: swap events");
				 System.out.println("5: Exit" + "\n");
	        	 
	        	 Scanner s = new Scanner(System.in);
		         int input = s.nextInt();
	        	 switch (input) {
	       
	        	 case 1:	//1: book event
	        		 System.out.println("Please Enter eventID: ");
	        		 Scanner one = new Scanner(System.in);     		 
	        		 String eventID = one.nextLine();
	        		 
	        		 System.out.println("Please Enter Type:(conference/tradeshow/seminar)");
	        		 Scanner two = new Scanner(System.in);     		 
	        		 String eventType = two.nextLine();
	        		 
	        		 if((eventID.length()!=10)||(!eventID.substring(0, 3).equals("TOR")&&!eventID.substring(0, 3).equals("MTL")&&!eventID.substring(0, 3).equals("OTW"))
	        				 ||(!eventID.substring(3, 4).equals("M")&&!eventID.substring(3, 4).equals("E")&&!eventID.substring(3, 4).equals("A"))) {
	        			 System.out.println("Wrong EventID! Failed to book this Event!"); 
	        			 Log(customerID, getDate() + " " + customerID + " failed to book in event " + eventID
	    	        			 +" in "+ eventType);
	        			 break;
	        		 }
	        		 
	        		 System.out.println("The customer :"+customerID+"The event is :"+ eventID+"the type is :"+ eventType);
	        		 if(cr.bookEvent(customerID,eventID, eventType)){
	        			 System.out.println("Customer "+customerID+" successfully booked in event "+ eventID);
	        			 Log(customerID,  getDate() + " " + customerID + " successfully booked in event " + eventID
	    	        			 +" in "+ eventType);
	        		 }
	        		 else {
	        			 System.out.println("Customer "+ customerID+ "failed to book in event "+ eventID);
	        			 Log(customerID, getDate() + " " + customerID + " failed to book in event " + eventID
	    	        			 +" in "+ eventType);
	        		 }
	        		 break;
	        		 
	        	 case 2:	//2: get booking Schedule
	        		 String result=cr.getBookingSchedule(customerID);
	        		 System.out.println( customerID+ "booking schedule: "+ result);
        			 Log(customerID, getDate() + " " + customerID + " booking schedule: " + result);
        			 break;
        			 
	        	 case 3:	//3: cancel event
	        		 System.out.println("Please Enter eventID: ");
	        		 Scanner three = new Scanner(System.in);     		 
	        		 String eID = three.nextLine();
	        		 
	        		 System.out.println("Please Enter Type:(conference/tradeshow/seminar)");
	        		 Scanner four = new Scanner(System.in);     		 
	        		 String eventT = four.nextLine();
	        		 
	        		 if(cr.cancelEvent(customerID, eID, eventT)){
	        			 System.out.println("Customer "+customerID+" successfully cancel event "+ eID);
	        			 Log(customerID,  getDate() + " " + customerID + " successfully cancel event " + eID);
	    	        			 
	        		 }
	        		 else {
	        			 System.out.println("Customer "+ customerID+ "failed to cancel event "+ eID);
	        			 Log(customerID, getDate() + " " + customerID + " failed to cancel event " + eID);
	    	        			
	        		 }
	        		 break;
	        	 case 4:
						System.out.println("Please Enter New eventID: ");
						Scanner five = new Scanner(System.in);
						String newID = five.nextLine();

						System.out.println("Please Enter New Type:(conference/tradeshow/seminar)");
						Scanner six = new Scanner(System.in);
						String newType = six.nextLine();

						System.out.println("Please Enter Old eventID: ");
						Scanner seven = new Scanner(System.in);
						String oldID = seven.nextLine();

						System.out.println("Please Enter Old Type:(conference/tradeshow/seminar)");
						Scanner eight = new Scanner(System.in);
						String oldType = eight.nextLine();

						if (cr.swapEvent(customerID, newID, newType, oldID, oldType)) {
							System.out.println("Customer " + customerID + " successfully swap old event " + oldID +" in "+ oldType
									+ " and new event " + newID+" in "+ newType);
							Log(customerID, getDate() + " " + customerID + " successfully swap old event " + oldID +" in "+ oldType
									+ " and new event " + newID+" in "+ newType);
						} else {
							System.out.println("Customer " + customerID + " failed to swap old event " + oldID +" in "+ oldType
									+ " and new event " + newID+" in "+ newType);
							Log(customerID, getDate() + " " + customerID + " failed to swap old event " + oldID +" in "+ oldType
									+ " and new event " + newID+" in "+ newType);
						}

						break;
					case 5:
						System.exit(0);
	  		
				default:
					break;
				}
	        	 
	         }
	         
	      } 
	      catch (Exception e) {
	         System.out.println("Exception in CustomerClient: " + e);
	      } 

	}

}
