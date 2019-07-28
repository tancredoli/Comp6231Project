package testNew;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import ServerApp.Server_interface;
import ServerApp.Server_interfaceHelper;

public class Customer_Client implements Runnable{

	private ORB orb;
	private String HOST = "127.0.0.1";
	private int SERVER_PORT_TOR = 6001;
	private int SERVER_PORT_MTL = 6002;
	private int SERVER_PORT_OTW= 6003;	
	private Logger LOGGER = null;
	
	private String customerId = "";
	private String userChoice = "";
	private String eventId = "";
	private String eventType ="";
	private String eventId_n = "";
	private String eventType_n = "";
	
	public Customer_Client(String customerId, String userChoice, String eventId, String eventType, String eventId_n, String eventType_n) {
		this.customerId = customerId;	
		this.userChoice = userChoice;
		this.eventId = eventId;
		this.eventType = eventType;
		this.eventId_n = eventId_n;
		this.eventType_n = eventType_n;
		
	}
	
	public void setORB(ORB orb) {
		this.orb = orb;
	}
	
	public boolean checkCustomerLogIn(String id) {
		String pattern = "^(TORC|MTLC|OTWC)(\\d{4})$";
		Pattern re = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = re.matcher(id);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * check the format of the event ID
	 */
	public  boolean checkEventId(String id) {
		String pattern = "^(TOR|MTL|OTW)(M|A|E)(\\d{6})$";
		Pattern re = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = re.matcher(id);
		if (matcher.find()) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Get the prefix of userID then send userID to specific server to check valid
	 * of account.
	 */
	public  Boolean checkServerInfo(String n_cusID) {
		DatagramSocket socket = null;
		String hostname = HOST;
		String requestcode = "002";// check if contain this customer
		int serverPort = 0;
		boolean flag = false;
		// set the server port
		if (n_cusID.substring(0, 3).equalsIgnoreCase("tor")) {
			serverPort = SERVER_PORT_TOR;
		} else if (n_cusID.substring(0, 3).equalsIgnoreCase("mtl")) {
			serverPort = SERVER_PORT_MTL;
		} else if (n_cusID.substring(0, 3).equalsIgnoreCase("otw")) {
			serverPort = SERVER_PORT_OTW;
		}

		try {
			socket = new DatagramSocket();
			// use UDP to check the valid of account.
			byte[] message = (new String(requestcode + n_cusID)).getBytes();
			InetAddress Host = InetAddress.getByName(hostname);// the address of the client is equal to the server
			DatagramPacket request = new DatagramPacket(message, message.length, Host, serverPort);
			socket.send(request);
			byte[] buffer = new byte[100];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			String result = new String(reply.getData()).trim();
			if (result.equals("valid")) {
				flag = true;
			} else {
				flag = false;
			}
		} catch (Exception e) {
			System.out.println("Socket: " + e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return flag;
	}

	/**
	 * Initial the Logger function.
	 * 
	 * @param server_name
	 */
	public void initLogger(String customerID) {
		try {
			SimpleFormatter formatter = new SimpleFormatter();
			LOGGER = Logger.getLogger(customerID + ".log");
			LOGGER.setUseParentHandlers(false);
			FileHandler FH = new FileHandler("Client_Side_Log/" + customerID + ".log", true);
			FH.setFormatter(formatter);
			LOGGER.addHandler(FH);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getEventName(String eventType) {
		if (eventType.equals("1")) {
			eventType = "Conferences";
		} else if (eventType.equals("2")) {
			eventType = "Seminars";
		} else {
			eventType = "Trade Shows";
		}
		return eventType;
	}

	public static void showMenu(String cusID) {
		System.out.println("****************Customer: " + cusID + "****************");
		System.out.println("Please select an option (1-4)");
		System.out.println("1. bookEvent");
		System.out.println("2. getBookingSchedule");
		System.out.println("3. cancelEvent");
		System.out.println("4. swapEvent");
		System.out.println("5. exit the system");
	}

	public void exeCustomer() {
		boolean valid0 = false;
		String id;
		while (!valid0) {
//			System.out.println("Please input the customer ID:");
			boolean valid1 = false;
			while (!valid1) {
				id = this.customerId.toUpperCase().trim();
				valid1 = checkCustomerLogIn(id);
				if (valid1) {
					this.customerId = id;
				} else {
					System.out.println("The customer ID is wrong, please input again!");
				}
			}
			if (!checkServerInfo(this.customerId)) {
				System.out.println("Customer ID is not exist, please try again!");
			} else {
				System.out.println(this.customerId + " log in successfully!");
				valid0 = true;
			}
		}

		Server_interface serverobj = getReference(this.orb, customerId.substring(0, 3));
		initLogger(this.customerId);
		LOGGER.info("Request: " + this.customerId + " log in DEMS System.");

		try {
			String userChoice = "";
//			showMenu(this.customerId);
//			while (true) {
				Boolean valid = false;
				while (!valid) {
					userChoice = this.userChoice.trim();
					if (userChoice.matches("^[1-5]$"))
						valid = true;
					else
						System.out.println("Invalid Input, please enter an Integer!");
				}

				switch (userChoice) {
				case "1":
//					System.out.println("Please input eventID:");
					String e_id1 = "";
					boolean eidvalid = false;
					while (!eidvalid) {
						e_id1 = this.eventId.toUpperCase().trim();
						eidvalid = checkEventId(e_id1);
						if (!eidvalid)
							System.out.println(
									"The format of the eventID is wrong, please input eventID again! (like \"MTLA100519\")");
					}

//					System.out.println("Please choose eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type1 = "";
					Boolean etypevalid = false;
					while (!etypevalid) {
						e_type1 = this.eventType.trim();
						if (e_type1.matches("^[1-3]$"))
							etypevalid = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

					String s1 = serverobj.bookEvent(this.customerId, e_id1, e_type1);
					System.out.println(s1);
					LOGGER.info("Request:" + this.customerId + " book event: "
							+ getEventName(e_type1) + " " + e_id1 + ". Response: " + s1);

//					showMenu(this.customerId);
					break;

				case "2":
					String s2 = serverobj.getBookingSchedule(this.customerId);
					System.out.println(s2);
					LOGGER.info(
							"Request: get booking schedule for " + this.customerId + ". Response:\n " + s2);
//					showMenu(this.customerId);
					break;

				case "3":
//					System.out.println("Please choose eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type3 = "";
					Boolean etypevalid3 = false;
					while (!etypevalid3) {
						e_type3 = this.eventType.trim();
						if (e_type3.matches("^[1-3]$"))
							etypevalid3 = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

//					System.out.println("Please input eventID:");
					String e_id3 = "";
					boolean eidvalid3 = false;
					while (!eidvalid3) {
						e_id3 = this.eventId.toUpperCase().trim();
						eidvalid3 = checkEventId(e_id3);
						if (!eidvalid3)
							System.out.println(
									"The format of the eventID is wrong, please input eventID again! (like \"MTLA100519\")");
					}

					String s3 = serverobj.cancelEvent(this.customerId, e_type3, e_id3);
					System.out.println(s3);
					LOGGER.info("Request: cancel event for " + this.customerId + ": "
							+ getEventName(e_type3) + " " + e_id3 + ". Response: " + s3);
//					showMenu(this.customerId);
					break;
				case "4":
//					System.out.println("Please choose old eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type4_o = "";
					Boolean etypevalid4_o = false;
					while (!etypevalid4_o) {
						e_type4_o = this.eventType.trim();
						if (e_type4_o.matches("^[1-3]$"))
							etypevalid4_o = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

//					System.out.println("Please input old eventID:");
					String e_id4_o = "";
					boolean eidvalid4_o = false;
					while (!eidvalid4_o) {
						e_id4_o = this.eventId.toUpperCase().trim();
						eidvalid4_o = checkEventId(e_id4_o);
						if (!eidvalid4_o)
							System.out.println(
									"The format of the eventID is wrong, please input eventID again! (like \"MTLA100519\")");
					}
					
					
//					System.out.println("Please choose new eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type4_n = "";
					Boolean etypevalid4_n = false;
					while (!etypevalid4_n) {
						e_type4_n = this.eventType_n.trim();
						if (e_type4_n.matches("^[1-3]$"))
							etypevalid4_n = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

//					System.out.println("Please input new eventID:");
					String e_id4_n = "";
					boolean eidvalid4_n = false;
					while (!eidvalid4_n) {
						e_id4_n = this.eventId_n.toUpperCase().trim();
						eidvalid4_n = checkEventId(e_id4_n);
						if (!eidvalid4_n)
							System.out.println(
									"The format of the eventID is wrong, please input eventID again! (like \"MTLA100519\")");
					}

					String s4 = serverobj.swapEvent(this.customerId, e_id4_n, e_type4_n, e_id4_o, e_type4_o);
					System.out.println(Thread.currentThread().getName() + " " + s4);
					LOGGER.info("Request: swap event for " + this.customerId
							+ ": (new) " + getEventName(e_type4_n) + " " + e_id4_n 
							+ " (old) "+ getEventName(e_type4_o) + " " + e_id4_o 
							+ ". Response: " + s4);
//					showMenu(this.customerId);
					break;
				case "5":
					System.out.println("You have quitted the DEMS System! Thank you!");
					System.exit(0);
				default:
					System.out.println("Invalid Input, please try again!");
				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			exeCustomer();
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}


	public Server_interface getReference(ORB orb, String server_name) {
		try 
		{
			//-ORBInitialPort 900 -ORBInitialHost localhost
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);			
			Server_interface serverobj = (Server_interface) Server_interfaceHelper.narrow(ncRef.resolve_str(server_name));
			return serverobj;
		} catch (Exception e) {
			System.out.println("Client exception: " + e);
			e.printStackTrace();
		}
		return null;
	}
}
