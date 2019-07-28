package Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
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
import Server_mtl.config_MTL;

public class Manager_Client implements Runnable{
	
	private String MANAGER_ID = null;
	private String HOST = "127.0.0.1";
	private int SERVER_PORT_TOR = 6001;
	private int SERVER_PORT_MTL = 6002;
	private int SERVER_PORT_OTW= 6003;	
	private Logger LOGGER = null;
	private ORB orb;
	
	public static boolean checkManagerLogIn(String id) {
		String pattern = "^(TORM|MTLM|OTWM)(\\d{4})$";
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
	public static boolean checkEventId(String id) {
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
	public Boolean checkServerInfo(String n_manaID) {
		DatagramSocket socket = null;
		String hostname = HOST;
		String requestcode = "001";
		int serverPort = 0;
		// set the server port
		if (MANAGER_ID.substring(0, 3).equalsIgnoreCase("tor")) {
			serverPort = SERVER_PORT_TOR;
		} else if (MANAGER_ID.substring(0, 3).equalsIgnoreCase("mtl")) {
			serverPort = SERVER_PORT_MTL;
		} else if (MANAGER_ID.substring(0, 3).equalsIgnoreCase("otw")) {
			serverPort = SERVER_PORT_OTW;
		}

		try {
			socket = new DatagramSocket();
			// use UDP to check the valid of account.
			byte[] message = (new String(requestcode + n_manaID)).getBytes();
			InetAddress Host = InetAddress.getByName(hostname);// the address of the client is equal to the server
			DatagramPacket request = new DatagramPacket(message, message.length, Host, serverPort);
			socket.send(request);
			byte[] buffer = new byte[100];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			String result = new String(reply.getData()).trim();
			if (result.equals("valid")) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println("Socket: " + e.getMessage());
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return null;
	}

	/**
	 * Initial the Logger function.
	 * 
	 * @param server_name
	 */
	public void initLogger(String managerID) {
		try {
			SimpleFormatter formatter = new SimpleFormatter();
			LOGGER = Logger.getLogger(managerID + ".log");
			LOGGER.setUseParentHandlers(false);
			FileHandler FH = new FileHandler("Client_Side_Log/" + managerID + ".log", true);
			FH.setFormatter(formatter);
			LOGGER.addHandler(FH);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getEventName(String eventType) {
		if (eventType.equals("1")) {
			eventType = "Conferences";
		} else if (eventType.equals("2")) {
			eventType = "Seminars";
		} else {
			eventType = "Trade Shows";
		}
		return eventType;
	}

	public void showMenu(String manaID) {
		System.out.println("**************Manager: " + manaID + "*************");
		System.out.println("Please select an option (1-7)");
		System.out.println("1. AddEvent");
		System.out.println("2. RemoveEvent");
		System.out.println("3. ListEventAvailability");
		System.out.println("4. bookEvent");
		System.out.println("5. getBookingSchedule");
		System.out.println("6. cancelEvent");
		System.out.println("7. swapEvent");
		System.out.println("8. exit the system");
	}

	public void exeManager() {
//		getServerReferrence();
		Scanner keyboard = new Scanner(System.in);
		boolean valid0 = false;
		String id;
		while (!valid0) {
			System.out.println("Please input the manager ID:");
			boolean valid1 = false;
			while (!valid1) {
				id = keyboard.next().toUpperCase().trim();
				valid1 = checkManagerLogIn(id);
				if (valid1) {
					MANAGER_ID = id;
				} else {
					System.out.println("The manager ID is wrong, please input again!");
				}
			}
			if (!checkServerInfo(MANAGER_ID)) {
				System.out.println("Manager ID is not exist, please try again!");
			} else {
				System.out.println(MANAGER_ID + " log in successfully!");
				valid0 = true;
			}
		}
		

		Server_interface serverobj = getReference(this.orb, MANAGER_ID.substring(0, 3));
		initLogger(MANAGER_ID);
		LOGGER.info("Request: " + MANAGER_ID + " log in DEMS System.");

		try {
			String userChoice = "";
			showMenu(MANAGER_ID);
			while (true) {
				Boolean valid = false;
				while (!valid) {
					userChoice = keyboard.next().trim();
					if (userChoice.matches("^[1-8]$"))
						valid = true;
					else
						System.out.println("Invalid Input, please enter an Integer!");
				}
				switch (userChoice) {
				case "1":
					System.out.println("Please input eventID:");
					String e_id1 = "";
					boolean eidvalid1 = false;
					while (!eidvalid1) {
						e_id1 = keyboard.next().toUpperCase().trim();
						eidvalid1 = checkEventId(e_id1);
						if (!eidvalid1)
							System.out.println(
									"The format of the eventID is wrong, please input eventID again! (like \"MTLA100519\")");
					}

					System.out.println("Please choose eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type1 = "";
					Boolean etypevalid1 = false;
					while (!etypevalid1) {
						e_type1 = keyboard.next().trim();
						if (e_type1.matches("^[1-3]$"))
							etypevalid1 = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

					System.out.println("Please add bookingCapacity:");
					String e_capacity1 = "";
					Boolean capvalid1 = false;
					while (!capvalid1) {
						e_capacity1 = keyboard.next().trim();
						if (e_capacity1.matches("^\\d+$") && Integer.parseInt(e_capacity1) > 0)
							capvalid1 = true;
						else
							System.out.println("Invalid Input, please enter an valid Integer!");
					}

					String s1 = serverobj.addEvent(e_id1, e_type1, Integer.parseInt(e_capacity1));
					System.out.println(s1);
					LOGGER
							.info("Request: add event " + getEventName(e_type1) + " " + e_id1 + ". Response: " + s1);
					showMenu(MANAGER_ID);
					break;

				case "2":
					System.out.println("Please input eventID:");
					String e_id2 = "";
					boolean eidvalid2 = false;
					while (!eidvalid2) {
						e_id2 = keyboard.next().toUpperCase().trim();
						eidvalid2 = checkEventId(e_id2);
						if (!eidvalid2)
							System.out.println(
									"The format of the eventID is wrong, please input eventID again! (like \"MTLA100519\")");
					}

					System.out.println("Please choose eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type2 = "";
					Boolean etypevalid2 = false;
					while (!etypevalid2) {
						e_type2 = keyboard.next().trim();
						if (e_type2.matches("^[1-3]$"))
							etypevalid2 = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

					String s2 = serverobj.removeEvent(e_id2, e_type2);
					System.out.println(s2);
					LOGGER
							.info("Request: remove event " + getEventName(e_type2) + " " + e_id2 + ". Response: " + s2);
					showMenu(MANAGER_ID);
					break;

				case "3":
					System.out.println("Please choose eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type3 = "";
					Boolean etypevalid3 = false;
					while (!etypevalid3) {
						e_type3 = keyboard.next().trim();
						if (e_type3.matches("^[1-3]$"))
							etypevalid3 = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

					String s3 = serverobj.listEventAvailability(e_type3);
					System.out.println(s3);
					LOGGER
							.info("Request: list event availability " + getEventName(e_type3) + ". Response: " + s3);
					showMenu(MANAGER_ID);
					break;

				case "4":
					System.out.println("Please input customer ID:");
					String c_id4 = "";
					boolean cidvalid4 = false;
					while (!cidvalid4) {
						c_id4 = keyboard.next().toUpperCase().trim();
						cidvalid4 = c_id4.matches("^(TORC|MTLC|OTWC)(\\d{4})$");
						if (!cidvalid4)
							System.out.println(
									"The format of the customer ID is wrong, please input eventID again! (like \"MTLC1000\")");
					}

					System.out.println("Please input eventID:");
					String e_id4 = "";
					boolean eidvalid4 = false;
					while (!eidvalid4) {
						e_id4 = keyboard.next().toUpperCase().trim();
						eidvalid4 = checkEventId(e_id4);
						if (!eidvalid4)
							System.out.println(
									"The format of the eventID is wrong, please input eventID again! (like \"MTLA100519\")");
					}

					System.out.println("Please choose eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type4 = "";
					Boolean etypevalid4 = false;
					while (!etypevalid4) {
						e_type4 = keyboard.next().trim();
						if (e_type4.matches("^[1-3]$"))
							etypevalid4 = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

					String s4 = serverobj.bookEvent(c_id4, e_id4, e_type4);
					System.out.println(s4);
					LOGGER.info("Request: book event for " + c_id4 + ": " + getEventName(e_type4) + e_id4
							+ ". Response: " + s4);
					showMenu(MANAGER_ID);
					break;

				case "5":
					System.out.println("Please input customer ID:");
					String c_id5 = "";
					boolean cidvalid5 = false;
					while (!cidvalid5) {
						c_id5 = keyboard.next().toUpperCase().trim();
						cidvalid5 = c_id5.matches("^(TORC|MTLC|OTWC)(\\d{4})$");
						if (!cidvalid5)
							System.out.println(
									"The format of the customer ID is wrong, please input eventID again! (like \"MTLC1000\")");
					}

					String s5 = serverobj.getBookingSchedule(c_id5);
					System.out.println(s5);
					LOGGER.info("Request: get booking schedule for " + c_id5 + ". Response:\n " + s5);
					showMenu(MANAGER_ID);
					break;

				case "6":
					System.out.println("Please input customer ID:");
					String c_id6 = "";
					boolean cidvalid6 = false;
					while (!cidvalid6) {
						c_id6 = keyboard.next().toUpperCase().trim();
						cidvalid6 = c_id6.matches("^(TORC|MTLC|OTWC)(\\d{4})$");
						if (!cidvalid6)
							System.out.println(
									"The format of the customer ID is wrong, please input eventID again! (like \"MTLC1000\")");
					}

					System.out.println("Please choose eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type6 = "";
					Boolean etypevalid6 = false;
					while (!etypevalid6) {
						e_type6 = keyboard.next().trim();
						if (e_type6.matches("^[1-3]$"))
							etypevalid6 = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

					System.out.println("Please input eventID:");
					String e_id6 = "";
					boolean eidvalid6 = false;
					if (!eidvalid6) {
						e_id6 = keyboard.next().toUpperCase().trim();
						eidvalid6 = checkEventId(e_id6);
						if (!eidvalid6)
							System.out.println(
									"The format of the eventID is wrong, please input eventID again! (like \"MTLA100519\")");
					}

					String s6 = serverobj.cancelEvent(c_id6, e_type6, e_id6);
					System.out.println(s6);
					LOGGER.info("Request: cancel event for " + c_id6 + ": " + getEventName(e_type6)
							+ e_id6 + ". Response: " + s6);
					showMenu(MANAGER_ID);
					break;
					
				case "7":
					System.out.println("Please input customer ID:");
					String c_id7 = "";
					boolean cidvalid7 = false;
					while (!cidvalid7) {
						c_id7 = keyboard.next().toUpperCase().trim();
						cidvalid7 = c_id7.matches("^(TORC|MTLC|OTWC)(\\d{4})$");
						if (!cidvalid7)
							System.out.println(
									"The format of the customer ID is wrong, please input eventID again! (like \"MTLC1000\")");
					}

					System.out.println("Please choose old eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type7_o = "";
					Boolean etypevalid7_o  = false;
					while (!etypevalid7_o ) {
						e_type7_o  = keyboard.next().trim();
						if (e_type7_o .matches("^[1-3]$"))
							etypevalid7_o  = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

					System.out.println("Please input old eventID:");
					String e_id7_o  = "";
					boolean eidvalid7_o  = false;
					if (!eidvalid7_o ) {
						e_id7_o  = keyboard.next().toUpperCase().trim();
						eidvalid7_o  = checkEventId(e_id7_o );
						if (!eidvalid7_o )
							System.out.println(
									"The format of the eventID is wrong, please input eventID again! (like \"MTLA100519\")");
					}

					System.out.println("Please choose new eventType:\n1. Conferences\n2. Seminars\n3. Trade Shows");
					String e_type7_n = "";
					Boolean etypevalid7_n  = false;
					while (!etypevalid7_n ) {
						e_type7_n  = keyboard.next().trim();
						if (e_type7_n .matches("^[1-3]$"))
							etypevalid7_n  = true;
						else
							System.out.println("Invalid Input, please enter an Integer of the event type!");
					}

					System.out.println("Please input new eventID:");
					String e_id7_n  = "";
					boolean eidvalid7_n  = false;
					if (!eidvalid7_n ) {
						e_id7_n  = keyboard.next().toUpperCase().trim();
						eidvalid7_n  = checkEventId(e_id7_n );
						if (!eidvalid7_n )
							System.out.println(
									"The format of the eventID is wrong, please input eventID again! (like \"MTLA100519\")");
					}
					String s7 = serverobj.swapEvent(c_id7, e_id7_n, e_type7_n, e_id7_o, e_type7_o);
					System.out.println(s7);
					LOGGER.info("Request: swap event for " + c_id7
							+ ": (new) " + getEventName(e_type7_n) + " " + e_id7_n 
							+ " (old) "+ getEventName(e_type7_o) + " " + e_id7_o 
							+ ". Response: " + s7);
					showMenu(MANAGER_ID);
					break;	
					
				case "8":
					System.out.println("You have quitted the DEMS System! Thank you!");
					System.exit(0);

				default:
					System.out.println("Invalid Input, please try again.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		ORB orb = ORB.init(args, null);
		Manager_Client manager = new Manager_Client();
		manager.orb = orb;
			
		Thread t1 = new Thread(manager);
		t1.start();
	}
	
	@Override
	public void run() {
		try {
		exeManager();
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
