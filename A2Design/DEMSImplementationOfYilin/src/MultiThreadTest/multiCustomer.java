package MultiThreadTest;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import CorbaApp.DEMSInterf;
import CorbaApp.DEMSInterfHelper;

public class multiCustomer implements Runnable{

	static DEMSInterf cr;
	
	String cusID;
	String eventID;
	String type;
	String eventID_new;
	String type_new;
	ORB orb;
	
	public multiCustomer(String cusID,String eventID,String type,String eventID_new,String type_new,ORB orb) {
		this.cusID=cusID;
		this.eventID=eventID;
		this.type=type;
		this.eventID_new=eventID_new;
		this.type_new=type_new;
		this.orb=orb;
	}
	
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
	
	public void operation() {        
        String hostName;
        String portNum = " ";
        String name = " ";
                
        String customerID = cusID;
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
	        	 System.out.println("Invalid customerID "+customerID);
	        	 System.exit(0);
	         }
       
        }
        else {
       	 System.out.println("Invalid customerID "+customerID);
       	 System.exit(0);
        }
        
        
        try {
	        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
	        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	        cr = (DEMSInterf) DEMSInterfHelper.narrow(ncRef.resolve_str(name));
	        
	        if(cr.cusLogin(customerID)){
	       	 System.out.println(customerID+" Login successfully");
	       	 Log(customerID, getDate() + " " + customerID + " login successfully");
	        }
	        else{
	       	 System.out.println(customerID+" Login failed");
	       	 Log(customerID, getDate() + " " + customerID + " login failed");
	        }
        }catch (Exception e) {
			System.out.println("Exception in multiCustomer: " + e);
		}      
        
	}
	
	public void book() {
		try {
			if(cr.bookEvent(cusID,eventID, type)){
				 System.out.println("Customer "+cusID+" successfully booked in event "+ eventID+" in "+ type);
				 Log(cusID,  getDate() + " " + cusID + " successfully booked in event " + eventID
	       			 +" in "+ type);
			 }
			 else {
				 System.out.println("Customer "+ cusID+ "failed to book in event "+ eventID+" in "+ type);
				 Log(cusID, getDate() + " " + cusID + " failed to book in event " + eventID
	       			 +" in "+ type);
			 }
		}catch(Exception e) {
			System.out.println("Exception in multiCustomer: " + e);
		} 
	}
	
	public void swap() {
		try {
			if (cr.swapEvent(cusID, eventID_new, type_new, eventID, type)) {
				System.out.println("Customer " + cusID + " successfully swap old event " + eventID +" in "+ type
						+ " and new event " + eventID_new +" in "+ type_new);
				Log(cusID, getDate() + " " + cusID + " successfully swap old event " + eventID +" in "+ type
						+ " and new event " + eventID_new +" in "+ type_new);
			} else {
				System.out.println("Customer " + cusID + " failed to swap old event " + eventID +" in "+ type
						+ " and new event " + eventID_new +" in "+ type_new);
				Log(cusID, getDate() + " " + cusID + " failed to swap old event " + eventID +" in "+ type
						+ " and new event " + eventID_new +" in "+ type_new);
			}
		}catch(Exception e) {
			System.out.println("Exception in multiCustomer: " + e);
		} 
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		operation();
		try {
			Thread.sleep(80);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		book();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		swap();
		
	}

}
