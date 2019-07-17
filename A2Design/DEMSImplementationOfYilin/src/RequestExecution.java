import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RequestExecution extends Thread{
	
	public Thread t;
	public DEMSImpl server;
	public DatagramSocket serversocket;
	public InetAddress address;
	public int clientport;
	String message;
	
	public RequestExecution(DEMSImpl server,DatagramSocket serversocket,InetAddress address,int clientport,String message) {
		super();
		this.address = address;
		this.message = message;
		this.server = server;
		this.clientport = clientport;
		this.serversocket = serversocket;
	}
	
	public void start(){
		if(t == null){
			t = new Thread(this);
			t.start();
		}
	}
	
	public void run(){
		String result = " ";
		if(message.startsWith("listEventA")){
			
			result = UDPlistEventA(message);
		}
		else if(message.startsWith("bookEvent")){
			result = UDPbookEvent(message);
		}
		else if(message.startsWith("cancelEvent")){
			result = UDPcancelEvent(message);
		}
		else if(message.startsWith("cancelForCustomer")){
			result = UDPcancelForCustomer(message);
		}
		byte []buffer = result.getBytes();
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length,address,clientport);
		try {
			serversocket.send(reply);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String UDPlistEventA(String message){
		String data = message.substring(message.indexOf("(") + 1,message.length() - 1);
		String result = server.listEventALocal(data);
		//System.out.println("Result in RequestExecution "+ result );
		return result; 
	}
	
	public String UDPbookEvent(String message){
		String arguments = message.substring(message.indexOf("(") + 1,message.length() - 1);
		String arg[] = arguments.split(",");
		String customerID = arg[0];
		String eventID = arg[1];
		String type = arg[2];
		String result = server.insertBook(customerID,eventID,type);
		return result;
	}
	
	public String UDPcancelEvent(String message){
		String arguments = message.substring(message.indexOf("(") + 1,message.length() - 1);
		String arg[] = arguments.split(",");
		String cusID = arg[0];
		String eventID = arg[1];
		String type=arg[2];
		String result=server.cancelEventLocal(cusID, eventID,type);
		//System.out.println("RequestExcution local: "+result);
		return result;
	}
	
	public String UDPcancelForCustomer(String message){
		String arguments = message.substring(message.indexOf("(") + 1,message.length() - 1);
		String arg[] = arguments.split(",");
		String cusID = arg[0];
		String eventID = arg[1];
		String result=server.cancelForCustomer(cusID, eventID);
		//System.out.println("RequestExcution customer: "+result);
		return result;
	}

}
