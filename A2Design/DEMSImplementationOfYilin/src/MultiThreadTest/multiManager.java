package MultiThreadTest;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import CorbaApp.DEMSInterf;
import CorbaApp.DEMSInterfHelper;

public class multiManager implements Runnable{

	static DEMSInterf cr;
	
	String manID;
	String eventID;
	String type;
	int cap;
	ORB orb;
	
	public multiManager(String manID,String eventID,String type,int cap,ORB orb) {
		this.manID=manID;
		this.eventID=eventID;
		this.type=type;
		this.cap=cap;
		this.orb=orb;
	}
	

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
	
	public void operation() {
		try {       
	         String hostName,portNum = " ";
	         InputStreamReader is = new InputStreamReader(System.in);
	         BufferedReader br = new BufferedReader(is);
	         
	         
	         String managerID = manID;
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
			        	 System.out.println("Invalid ManagerID "+managerID);
			        	 System.exit(0);
			         }

	         }
	         else {
	        	 System.out.println("Invalid ManagerID" +managerID);
	        	 System.exit(0);
	         }
	         
	
	         org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
	   
	         NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	         
	         cr = DEMSInterfHelper.narrow(ncRef.resolve_str(name));
	      
	         if(cr.manLogin(managerID)){
	        	 System.out.println(managerID+" Login successfully");
	        	 Log(managerID, getDate() + " " + managerID + " login successfully");
	         }
	         else{
	        	 System.out.println(managerID+" Login Unsuccessfully");
	        	 Log(managerID, getDate() + " " + managerID + " login failed");
	         }
	         
	         if(cr.addEvent(eventID, type, managerID,  cap)) {	
    			 System.out.println(managerID+" Add this event "+eventID+" in "+ type+" successfully!");
    			 Log(managerID, getDate() + " " + managerID + " successfully add event. " 
    			 + "Event information: Type: " + type + " EventID: " + eventID + " Capacity: " + cap
    			 );
    			 
    		 }
    		 else{
    			 System.out.println(managerID+" Failed to add this Event "+eventID +" in "+ type); 
    			 Log(managerID, getDate() + " " + managerID + " failed to add this event "+ eventID+
    					 "in Type"+ type);
    			
    		 }
		}
		catch (Exception e) {
			System.out.println("Exception in multiManager: " + e);
		} 
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		operation();
	}

}
