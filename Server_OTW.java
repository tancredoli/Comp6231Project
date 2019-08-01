package Host1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import Model.Event;

public class Server_OTW implements Server_interface, Runnable {

	// database of otw
	private static HashMap<String, HashMap<String, Integer>> hashMapHashMap = new HashMap<String, HashMap<String, Integer>>();
	// the booking record of customer(form different city).
	private static HashMap<String, ArrayList<Event>> bookRecord = new HashMap<String, ArrayList<Event>>();
	
	public Server_OTW(){
		super();
	}

	public void clearMap() {
		hashMapHashMap = new HashMap<String, HashMap<String, Integer>>();
		bookRecord = new HashMap<String, ArrayList<Event>>();
	}
	
	@Override
	public synchronized String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) {
		boolean flag_cancel = false;
		ArrayList<Event> list_bookrecord = new ArrayList<Event>();
		int remove_num = -1;
		String s = "";
		if (!customerID.substring(0, 3).equalsIgnoreCase(config_OTW.SERVER_NAME)) {
			s += "The customer " + customerID + " is not belong to " + config_OTW.SERVER_NAME
					+ " city, you can not swap event, please try again!";
		} else {
			if (checkCustomerID(customerID).equalsIgnoreCase("invalid")) {
				s += "The customer " + customerID + " is not exist, please try again!";
			} else {								
				if (bookRecord.containsKey(customerID)) {
					list_bookrecord = bookRecord.get(customerID);
					for (int i = 0; i < list_bookrecord.size(); i++) {
						Event e = list_bookrecord.get(i);
						if (e.getEventID().equalsIgnoreCase(oldEventID) && e.getEventType().equalsIgnoreCase(oldEventType)) {
							flag_cancel = true;//have booked
							remove_num = i;
							break;
						}
					}		
				if(flag_cancel) {
					// check if the number of events of other cities less than 3
					int flag = bookCondition(customerID, newEventID, newEventType);
					String old_month = oldEventID.substring(6, 8);
					String new_month = newEventID.substring(6, 8);
					String old_city = oldEventID.substring(0, 3);
					String new_city = newEventID.substring(0, 3);

					if (flag == 1 || flag == -2 && !old_city.equalsIgnoreCase(config_OTW.SERVER_NAME) && !new_city.equalsIgnoreCase(config_OTW.SERVER_NAME)
							&& old_month.equalsIgnoreCase(new_month)) {
						// check if belongs to local city
						if (isLocalEvent(newEventID)) {
							s += bookLocalEvent(customerID, newEventID, newEventType);
						} else if (newEventID.substring(0, 3).equalsIgnoreCase("TOR")) {
							s += sendMessageToOtherServer("006", config_OTW.SERVER_PORT_TOR, customerID, newEventID, newEventType);
						} else if (newEventID.substring(0, 3).equalsIgnoreCase("MTL")) {
							s += sendMessageToOtherServer("006", config_OTW.SERVER_PORT_MTL, customerID, newEventID, newEventType);
						} else {
							s += "This city doesn't provide any service. Booked events failure!";
						}
						// Add event record to local city bookRecord.
						if (s.contains("successfully!")) {
							ArrayList<Event> list1 = null;
							Event e = new Event(newEventID, newEventType);
							list1 = bookRecord.get(customerID);
							list1.add(e);
							bookRecord.put(customerID, list1);
							list_bookrecord.remove(remove_num);
							if (isLocalEvent(oldEventID)) {
								// customer have booked this eventID and remove from the local bookRecord.
								s += cancelLocalEvent(oldEventID, oldEventType);
							} else if (oldEventID.substring(0, 3).equalsIgnoreCase("TOR")) {
								// customer have booked this eventID and remove from the other server's
								// bookRecord.
								s += sendMessageToOtherServer("004", config_OTW.SERVER_PORT_TOR, oldEventID, oldEventType);
							} else if (oldEventID.substring(0, 3).equalsIgnoreCase("MTL")) {
								// customer have booked this eventID and remove from the other server's
								// bookRecord.
								s += sendMessageToOtherServer("004", config_OTW.SERVER_PORT_MTL, oldEventID, oldEventType);
							} 		
							s = customerID + " swap event: (new) " + getEventName(newEventType) + " " + newEventID
									 + " (old) " + getEventName(oldEventType) + " " + oldEventID + " successfully!";
						}else {
							s += " " + customerID + " Swap failed! ";
						}
					} else if (flag == -1) {
						s += "Customer " + customerID + " have already booked the event" + getEventName(newEventType) + " " + newEventID + ". Swap failed! ";
					} else {// flag == -2
						s += "Customer " + customerID + " have already booked 3 events from other cities in " + newEventID.substring(6, 8) + " month! Swap failed! ";
					}
				}else {
					s += "Customer " + customerID + " doesn't have booked the event " + getEventName(oldEventType) + " " + oldEventID + ". Swap failed! ";
				}
			}
		}		
	}

		if (s.contains("successfully")) {
			config_OTW.LOGGER.info(customerID + " swap event: (new) " + getEventName(newEventType) + " " + newEventID
					 + " (old) " + getEventName(oldEventType) + " " + oldEventID
					+ ". Request successfully completed. " + " Server response: " + s);
		} else {
			config_OTW.LOGGER.info(customerID + " swap event: (new) " + getEventName(newEventType) + " " + newEventID
					 + " (old) " + getEventName(oldEventType) + " " + oldEventID
					+ ". Request failed. " + " Server response: " + s);
		}
		
		return s;		
	}
	
	@Override
	public synchronized String addEvent(String managerId, String eventID, String eventType, int bookingCapacity) {
		String s = "";
		if (isLocalEvent(eventID)) {
			HashMap<String, Integer> sub_map = new HashMap<>();
			if (hashMapHashMap.containsKey(eventType)) {
				sub_map = hashMapHashMap.get(eventType);
				if (sub_map.containsKey(eventID)) {
					sub_map.put(eventID, bookingCapacity);
					s += "Add event " + getEventName(eventType) + " " + eventID + " successfully!";
				} else {
					// add the new record
					sub_map.put(eventID, bookingCapacity);
					s += "Add event " + getEventName(eventType) + " " + eventID + " successfully!";
				}
			} else {
				sub_map.put(eventID, bookingCapacity);
				s += "Add event " + getEventName(eventType) + " " + eventID + " successfully!";
			}
			// add new event type
			hashMapHashMap.put(eventType, sub_map);
		} else {
			s += "You can't add event from other cities, please try again!";
		}

		if (s.contains("successfully")) {
			config_OTW.LOGGER
					.info(managerId + " add Event Record: " + getEventName(eventType) + " " + eventID + " "
							+ bookingCapacity + ". Request successfully completed. " + " Server response: " + s);
		} else {
			config_OTW.LOGGER.info(managerId + " add Event Record: " + getEventName(eventType) + " "
					+ eventID + " " + bookingCapacity + ". Request failed. " + " Server response: " + s);
		}
		return s;
	}

	@Override
	public synchronized String removeEvent(String managerId, String eventID, String eventType) {
		String s = "";
		if (hashMapHashMap.containsKey(eventType)) {
			HashMap<String, Integer> sub_map = hashMapHashMap.get(eventType);
			if (sub_map.containsKey(eventID)) {
				// remove from the database.
				sub_map.remove(eventID);
				// remove from bookRecord.
				hashMapHashMap.put(eventType, sub_map);
				s += removeLocalEvent(eventID, eventType);
				s += sendMessageToOtherServer("005", config_OTW.SERVER_PORT_TOR, eventID, eventType);
				s += sendMessageToOtherServer("005", config_OTW.SERVER_PORT_MTL, eventID, eventType);
			} else {
				s += "The event " + getEventName(eventType) + " " + eventID
						+ " is not exist or is belong to another city, please check out!";
			}
		} else {
			s += "The event " + getEventName(eventType) + " " + eventID
					+ " is not exist or is belong to another city, please check out!";
		}

		if (s.contains("successfully")) {
			s = "Remove Event Record: " + getEventName(eventType) + " " + eventID + " successfully!";
			config_OTW.LOGGER.info(managerId + " remove Event Record: " + getEventName(eventType) + " "
					+ eventID + ". Request successfully completed. " + " Server response: " + s);
		} else {
			config_OTW.LOGGER.info(managerId + " remove Event Record: " + getEventName(eventType) + " "
					+ eventID + ". Request failed. " + " Server response: " + s);
		}

		return s;
	}

	@Override
	public String listEventAvailability(String managerId, String eventType) {
		String tor_availability = sendMessageToOtherServer("003", config_OTW.SERVER_PORT_TOR, eventType);
		String mtl_availability = sendMessageToOtherServer("003", config_OTW.SERVER_PORT_MTL, eventType);
		String otw_availability = getLocalListEventAvailability(eventType);
		String s = getEventName(eventType) + " -\n" + (mtl_availability.length() == 0 ? "" : (mtl_availability + "\n"))
				+ (otw_availability.length() == 0 ? "" : (otw_availability + "\n"))
				+ (tor_availability.length() == 0 ? "" : tor_availability);
		s = s.trim();
		config_OTW.LOGGER.info(managerId + " list event availability: " + getEventName(eventType)
				+ ". Request successfully completed. " + " Server response: " + s);
		return s;
	}

	@Override
	public synchronized String bookEvent(String customerID, String eventID, String eventType) {
		String s = "";
		if (!customerID.substring(0, 3).equalsIgnoreCase(config_OTW.SERVER_NAME)) {
			s += "The customer " + customerID + " is not belong to " + config_OTW.SERVER_NAME
					+ " city, you can not boot event, please try again!";
		} else {
			if (checkCustomerID(customerID).equalsIgnoreCase("invalid")) {
				config_OTW.CUSTOMER_ACCOUNT.add(customerID);
				ArrayList<Event> list = new ArrayList<Event>();
				bookRecord.put(customerID, list);
				s += "The customer " + customerID + " has been created!\n";
			}
			// check if the number of events of other cities less than 3
			int flag = bookCondition(customerID, eventID, eventType);
			if (flag == 1) {
				// check if belongs to local city
				if (isLocalEvent(eventID)) {
					s += bookLocalEvent(customerID, eventID, eventType);
				} else if (eventID.substring(0, 3).equalsIgnoreCase("TOR")) {
					s += sendMessageToOtherServer("006", config_OTW.SERVER_PORT_TOR, customerID, eventID, eventType);
				} else if (eventID.substring(0, 3).equalsIgnoreCase("MTL")) {
					s += sendMessageToOtherServer("006", config_OTW.SERVER_PORT_MTL, customerID, eventID, eventType);
				} else {
					s += "This city doesn't provide any service. Booked events failure!";
				}
				// Add event record to local city bookRecord.
				if (s.contains("successfully!")) {
					ArrayList<Event> list = null;
					Event e = new Event(eventID, eventType);
					if (bookRecord.containsKey(customerID)) {
						list = bookRecord.get(customerID);
						list.add(e);
						bookRecord.put(customerID, list);
					} else {
						list = new ArrayList<Event>();
						list.add(e);
						bookRecord.put(customerID, list);
					}
				}
			} else if (flag == -1) {
				s += "You have already booked the event" + getEventName(eventType) + " " + eventID
						+ ", please change another one!";
			} else {// flag == -2
				s += "You have already booked 3 events from other cities in " + eventID.substring(6, 8)
						+ " month, plaease try again!";
			}
		}

		if (s.contains("successfully")) {
			config_OTW.LOGGER.info(customerID + " book event: " + customerID + " " + getEventName(eventType) + " "
					+ eventID + ". Request successfully completed. " + " Server response: " + s);
		} else {
			config_OTW.LOGGER.info(customerID + " book event: " + customerID + " " + getEventName(eventType) + " "
					+ eventID + ". Request failed. " + " Server response: " + s);
		}
		return s;
	}

	@Override
	public String getBookingSchedule(String customerID) {
		String s = "";
		if (!customerID.substring(0, 3).equalsIgnoreCase(config_OTW.SERVER_NAME)) {
			s += "The customer " + customerID + " is not belong to " + config_OTW.SERVER_NAME
					+ " city, you can not get booking schedule, please try again!";
		} else {
			if (checkCustomerID(customerID).equalsIgnoreCase("invalid")) {
				s += "The customer " + customerID + " is not exist, please try again! ";
			} else {
				ArrayList<Event> list = new ArrayList<Event>();
				if (bookRecord.containsKey(customerID)) {
					s += customerID + " get booking schedule successfully!\n";
					list = bookRecord.get(customerID);
					if (list.size() > 0) {
						s += "No. \t Event Type \t Event ID \n";
						for (int i = 0; i < list.size(); i++) {
							Event e = list.get(i);
							String temp = e.getEventType();
							s += (i + 1) + " \t " + getEventName(temp) + " \t " + e.getEventID() + "\n";
						}
					} else {
						s += customerID + " don't have any booking record! ";
					}
				} else {
					s += customerID + " don't have any booking record.";
				}
			}
		}
		if (s.contains("successfully")) {
			config_OTW.LOGGER.info(customerID + " get booking schedule: " + customerID
					+ ". Request successfully completed. " + " Server response: " + s);
		} else {
			config_OTW.LOGGER.info(customerID + " get booking schedule: " + customerID + ". Request failed. "
					+ " Server response: " + s);
		}
		return s;
	}

	@Override
	public synchronized String cancelEvent(String customerID, String eventType, String eventID) {
		String s = "";
		if (!customerID.substring(0, 3).equalsIgnoreCase(config_OTW.SERVER_NAME)) {
			s += "The customer " + customerID + " is not belong to " + config_OTW.SERVER_NAME
					+ " city, you can not cancel event, please try again!";
		} else {
			if (checkCustomerID(customerID).equalsIgnoreCase("invalid")) {
				s += "The customer " + customerID + " is not exist, please try again!";
			} else {
				boolean flag = false;
				ArrayList<Event> list = new ArrayList<Event>();
				if (bookRecord.containsKey(customerID)) {
					list = bookRecord.get(customerID);
					for (int i = 0; i < list.size(); i++) {
						Event e = list.get(i);
						if (e.getEventID().equalsIgnoreCase(eventID) && e.getEventType().equalsIgnoreCase(eventType)) {
							flag = true;
							// remove from the bookRecord.
							list.remove(i);
						}
					}
					if (flag == true && isLocalEvent(eventID)) {
						// customer have booked this eventID and remove from the local bookRecord.
						s += cancelLocalEvent(eventID, eventType);
					} else if (flag == true && eventID.substring(0, 3).equalsIgnoreCase("TOR")) {
						// customer have booked this eventID and remove from the other server's
						// bookRecord.
						s += sendMessageToOtherServer("004", config_OTW.SERVER_PORT_TOR, eventID, eventType);
					} else if (flag == true && eventID.substring(0, 3).equalsIgnoreCase("MTL")) {
						// customer have booked this eventID and remove from the other server's
						// bookRecord.
						s += sendMessageToOtherServer("004", config_OTW.SERVER_PORT_MTL, eventID, eventType);
					} else {
						s += "Customer " + customerID + " doesn't have booked the event " + getEventName(eventType)
								+ " " + eventID + ", please try again! ";
					}
				} else {
					s += customerID + " don't have any booking record.";
				}
			}
		}
		if (s.contains("successfully")) {
			config_OTW.LOGGER.info(customerID + " cancel event: " + customerID + " " + getEventName(eventType) + " "
					+ eventID + ". Request successfully completed. " + " Server response: " + s);
		} else {
			config_OTW.LOGGER.info(customerID + " cancel event: " + customerID + " " + getEventName(eventType) + " "
					+ eventID + ". Request failed. " + " Server response: " + s);
		}
		return s;
	}

	/**
	 * check if is local event.
	 */
	public boolean isLocalEvent(String eventID) {
		if (eventID.substring(0, 3).equalsIgnoreCase(config_OTW.SERVER_NAME))
			return true;
		else
			return false;
	}

	public static String getEventName(String eventType) {
		if (eventType.equalsIgnoreCase("1")) {
			eventType = "Conferences";
		} else if (eventType.equalsIgnoreCase("2")) {
			eventType = "Seminars";
		} else {
			eventType = "Trade Shows";
		}
		return eventType;
	}

	/**
	 * check if the number of events in other cities more than 3..
	 */
	public int bookCondition(String customerID, String eventID, String eventType) {
		int flag = 0;
		int sum = 0;
		String month = eventID.substring(6, 8);
		if (bookRecord.containsKey(customerID)) {
			ArrayList<Event> list = new ArrayList<Event>();
			list = bookRecord.get(customerID);
			for (int i = 0; i < list.size(); i++) {
				String eID = list.get(i).getEventID();
				String eType = list.get(i).getEventType();
				if (!eID.substring(0, 3).equalsIgnoreCase(config_OTW.SERVER_NAME)
						&& eID.substring(6, 8).equalsIgnoreCase(month)) {
					sum += 1;
				}
				if (eID.equalsIgnoreCase(eventID) && eType.equalsIgnoreCase(eventType)) {
					flag = -1; // "You have already booked this event, please change another one!";
				}
			}
			if (!eventID.substring(0, 3).equalsIgnoreCase(config_OTW.SERVER_NAME) && sum >= 3) {
				flag = -2;// "You have already booked 3 events from other cities, please try again!";
			}
		}
		if (flag == 0)
			flag = 1;
		return flag;
	}

	/**
	 * book event from local city. eventType and eventID
	 * 
	 * @param eventType
	 * @return String
	 */
	public synchronized static String bookLocalEvent(String customerID, String eventID, String eventType) {
		String s = "";
		if (hashMapHashMap.containsKey(eventType)) {
			HashMap<String, Integer> sub_map = hashMapHashMap.get(eventType);
			if (sub_map.containsKey(eventID)) {
				if (sub_map.get(eventID) > 0) {
					// overwrite and update the bookingCapacity(-1)
					sub_map.put(eventID, sub_map.get(eventID) - 1);
					hashMapHashMap.put(eventType, sub_map);
					s += "The event " + getEventName(eventType) + " " + eventID + " is booked successfully!";
				} else {
					s += "The event " + getEventName(eventType) + " " + eventID + " was full, book event failure!";
				}
			} else {
				s += "The event " + getEventName(eventType) + " " + eventID
						+ " is not exit. You can't book it. Please try again!";
			}
		} else {
			s += "The event " + getEventName(eventType) + " " + eventID
					+ " is not exit. You can't book it. Please try again!";
		}
		return s;
	}

	/**
	 * Increased the bookingCapacity in local city according to the given eventType
	 * and eventID
	 * 
	 * @param eventType
	 * @return String
	 */
	public synchronized static String cancelLocalEvent(String eventID, String eventType) {
		String s = "";
		if (hashMapHashMap.containsKey(eventType)) {
			HashMap<String, Integer> sub_map = hashMapHashMap.get(eventType);
			if (sub_map.containsKey(eventID)) {
				// overwrite and update the bookingCapacity(+1)
				sub_map.put(eventID, sub_map.get(eventID) + 1);
				hashMapHashMap.put(eventType, sub_map);
			}
			s += "Cancle event " + getEventName(eventType) + " " + eventID + " successfully! ";
		} else {
			s += "Cancle event " + getEventName(eventType) + " " + eventID + " failed!";
		}
		return s;
	}

	/**
	 * Increased the bookingCapacity in local city according to the given eventType
	 * and eventID
	 * 
	 * @param eventType
	 * @return String
	 */
	public synchronized static String removeLocalEvent(String eventID, String eventType) {
		Set<String> set = new HashSet<String>();
		set = bookRecord.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String str = it.next();
			for (int i = 0; i < bookRecord.get(str).size(); i++) {
				if (bookRecord.get(str).get(i).getEventID().equalsIgnoreCase(eventID)) {
					bookRecord.get(str).remove(i);
				}
			}
		}
		return " Remove event " + getEventName(eventType) + " " + eventID + " for customers in " + config_OTW.SERVER_NAME
				+ " successfully! ";
	}

	/**
	 * Check local ListEventAvailability according to the given eventType and return
	 * the value.
	 * 
	 * @param eventType
	 * @return String
	 */
	public static String getLocalListEventAvailability(String eventType) {
		HashMap<String, Integer> sub_map = new HashMap<>();
		String s = "";
		if (hashMapHashMap.containsKey(eventType)) {
			sub_map = hashMapHashMap.get(eventType);
			Set<String> set = new HashSet<String>();
			set = sub_map.keySet();
			Iterator<String> it = set.iterator();
			while (it.hasNext()) {
				String e_id = it.next();
				String temp = " " + e_id + " " + sub_map.get(e_id) + ",";
				s += temp;
			}
			if (s.length() > 0)
				s = s.substring(0, s.length() - 1).trim();
		}
		return s;
	}

	/**
	 * check if manager ID is in MANAGER_ACCOUNT
	 */
	public static String checkManagerID(String manaID) {
		String s = "invalid";
		for (String account : config_OTW.MANAGER_ACCOUNT) {
			if (account.equalsIgnoreCase(manaID)) {
				config_OTW.MANAGER_ID = manaID;
				s = "valid";
			}
		}
		return s;
	}

	/**
	 * check if manager ID is in CUSTOMER_ACCOUNT
	 */
	public static String checkCustomerID(String cusID) {
		String s = "invalid";
		for (String account : config_OTW.CUSTOMER_ACCOUNT) {
			if (account.equalsIgnoreCase(cusID)) {
				config_OTW.CUSTOMER_ID = cusID;
				s = "valid";
			}
		}
		return s;
	}

	/**
	 * Initial the Logger function.
	 * 
	 * @param config_OTW.SERVER_NAME
	 */
	public static void initLogger() {
		try {
			SimpleFormatter formatter = new SimpleFormatter();
			config_OTW.LOGGER = Logger.getLogger(config_OTW.SERVER_NAME + ".log");
			config_OTW.LOGGER.setUseParentHandlers(false);
			config_OTW.FH = new FileHandler("Server_Side_Log/" + config_OTW.SERVER_NAME + ".log", true);
			config_OTW.FH.setFormatter(formatter);
			config_OTW.LOGGER.addHandler(config_OTW.FH);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Open UDP listening port to check ManagerID and CustomerID and receive the
	 * other server request.
	 */
	public static void openUDPListener() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(config_OTW.LOCAL_LISTENING_PORT);
			while (true) {
				byte[] buffer = new byte[100];
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);
				config_OTW.LOGGER.info("Get request: " + (new String(request.getData()).trim()) + "\n"
						+ " Start a new thread to handle this.");
				new Connection(socket, request);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	/**
	 * New thread to handle the newly request.
	 */
	static class Connection extends Thread {
		DatagramSocket socket = null;
		DatagramPacket request = null;
		String result = null;

		public Connection(DatagramSocket n_socket, DatagramPacket n_request) {
			this.socket = n_socket;
			this.request = n_request;
			String requestcode = new String(request.getData()).trim().substring(0, 3);
			switch (requestcode) {
			case "001":
				String m_id1 = new String(request.getData()).trim().substring(3);
				config_OTW.LOGGER
						.info("Request code: " + requestcode + ", " + "Check ManagerID: " + m_id1 + " valid or not.");
				result = checkManagerID(m_id1);
				break;
			case "002":
				String c_id2 = new String(request.getData()).trim().substring(3);
				config_OTW.LOGGER
						.info("Request code: " + requestcode + ", " + "Check CustomerID: " + c_id2 + " valid or not.");
				result = checkCustomerID(c_id2);
				break;
			case "003":
				String e_type3 = new String(request.getData()).trim().substring(3);
				config_OTW.LOGGER.info("Request code: " + requestcode + ", " + "Get Local ListEventAvailability: "
						+ getEventName(e_type3));
				result = getLocalListEventAvailability(e_type3);
				break;
			case "004":
				String e_id4 = new String(request.getData()).trim().substring(3, 13);
				String e_type4 = new String(request.getData()).trim().substring(13);
				config_OTW.LOGGER.info(
						"Request code: " + requestcode + ", " + "Cancel Event: " + getEventName(e_type4) + " " + e_id4);
				result = cancelLocalEvent(e_id4, e_type4);
				break;
			case "005":
				String e_id5 = new String(request.getData()).trim().substring(3, 13);
				String e_type5 = new String(request.getData()).trim().substring(13);
				config_OTW.LOGGER.info(
						"Request code: " + requestcode + ", " + "Cancel Event: " + getEventName(e_type5) + " " + e_id5);
				result = removeLocalEvent(e_id5, e_type5);
				break;
			case "006":
				String c_id6 = new String(request.getData()).trim().substring(3, 11);
				String e_id6 = new String(request.getData()).trim().substring(11, 21);
				String e_type6 = new String(request.getData()).trim().substring(21);
				config_OTW.LOGGER.info("Request code: " + requestcode + ", " + "Book Event: " + c_id6 + " "
						+ getEventName(e_type6) + " " + e_id6);
				result = bookLocalEvent(c_id6, e_id6, e_type6);
				break;
			}
			this.start();
		}

		@Override
		public void run() {
			try {
				DatagramPacket reply = new DatagramPacket(result.getBytes(), result.getBytes().length,
						request.getAddress(), request.getPort());
				socket.send(reply);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String sendMessageToOtherServer(int serverPort, String code, String s) {
		DatagramSocket socket = null;
		String hostname = "127.0.0.1";

		try {
			socket = new DatagramSocket();
			byte[] message = (new String(code + s)).getBytes();
			InetAddress Host = InetAddress.getByName(hostname);
			DatagramPacket request = new DatagramPacket(message, message.length, Host, serverPort);
			socket.send(request);
			byte[] buffer = new byte[100];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			String result = new String(reply.getData()).trim();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
		return null;
	}

	public static String sendMessageToOtherServer(String code, int serverPort, String eventType) {
		return sendMessageToOtherServer(serverPort, code, eventType);
	}

	public static String sendMessageToOtherServer(String code, int serverPort, String eventID, String eventType) {
		return sendMessageToOtherServer(serverPort, code, eventID + eventType);
	}

	public static String sendMessageToOtherServer(String code, int serverPort, String customerID, String eventID,
			String eventType) {
		return sendMessageToOtherServer(serverPort, code, customerID + eventID + eventType);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		initLogger();
		System.out.println("Server_OTW bound");
		openUDPListener();	
	}
	
}
