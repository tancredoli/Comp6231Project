package Host4;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.omg.CORBA.ORB;

import DataBase.Customer;
import DataBase.EventDetails;
import Utility.Action;
import Utility.LogRecorder;

/**
 * @Author: Rui
 * @Description:
 * @Date: Created in 4:48 PM 2019-07-06
 * @Modified by:
 */
public class TORImplementationClass {

    public static LogRecorder torLogRecorder;
    // initial port number
    private int mtlUdpPort = 7102;
    private int otwUdpPort = 8102;
    private int torUdpPort = 9102;
    private HashMap<String, HashMap<String, EventDetails>> eventList;
    private HashMap<String, EventDetails> conList;
    private HashMap<String, EventDetails> semList;
    private HashMap<String, EventDetails> tsList;
    private File serverDir = new File(System.getProperty("user.dir"));
    private ORB orb;

    public void refresh() {
	this.eventList.get("Conferences").clear();
	this.eventList.get("Seminars").clear();
	this.eventList.get("TradeShows").clear();

    }

    public TORImplementationClass() throws Exception {
	super();
	torLogRecorder = new LogRecorder(3);
	HashMap<String, HashMap<String, EventDetails>> eventList = new HashMap();
	HashMap<String, EventDetails> conList = new HashMap();
	HashMap<String, EventDetails> semList = new HashMap();
	HashMap<String, EventDetails> tsList = new HashMap();
	eventList.put("Conferences", conList);
	eventList.put("Seminars", semList);
	eventList.put("TradeShows", tsList);
	this.eventList = eventList;
	this.conList = conList;
	this.semList = semList;
	this.tsList = tsList;
	// ArrayList<String> booklist1 = new ArrayList<>();
	// booklist1.add("MTLC0001");
	// EventDetails ed1 = new EventDetails(5, booklist1);
	// this.tsList.put("TORE150619", ed1);

    }

    public void setORB(ORB orb_val) {
	this.orb = orb_val;
    }

    public HashMap<String, EventDetails> getConList() {
	return conList;
    }

    public void setConList(HashMap<String, EventDetails> conList) {
	this.conList = conList;
    }

    public HashMap<String, EventDetails> getSemList() {
	return semList;
    }

    public void setSemList(HashMap<String, EventDetails> semList) {
	this.semList = semList;
    }

    public HashMap<String, EventDetails> getTsList() {
	return tsList;
    }

    public void setTsList(HashMap<String, EventDetails> tsList) {
	this.tsList = tsList;
    }

    public void addEventHelper(HashMap<String, EventDetails> curList, String userID, String eventID, int eventType,
	    int bookingCapacity) {
	EventDetails cur;
	torLogRecorder.writeNorLog(torLogRecorder.paraConstructor(userID, "", eventType, eventID, bookingCapacity),
		Action.ADDEVENT, true, "true");
	// !!!need to be modied!
	if (curList.containsKey(eventID)) {
	    cur = curList.get(eventID);
	    if (bookingCapacity > cur.getBookingCapacity()) {
		cur.setBookingCapacity(bookingCapacity);
		cur.setAvailableSeats(bookingCapacity - cur.getCurrentBooking());
	    } else {
		curList.remove(eventID);
		cur = new EventDetails(bookingCapacity);
		curList.put(eventID, cur);
	    }

	} else {
	    cur = new EventDetails(bookingCapacity);
	    curList.put(eventID, cur);
	}

    }

    public String getBookedList(String eventID, int eventType) {
	if (eventType == 1) {

	    return this.conList.get(eventID).toString();
	} else if (eventType == 2) {

	    return this.conList.get(eventID).toString();
	} else {

	    return this.conList.get(eventID).toString();
	}
    }

    public boolean addEvent(String userID, String eventID, int eventType, int bookingCapacity) {

	if (eventType == 1) {
	    addEventHelper(this.conList, userID, eventID, eventType, bookingCapacity);
	    return true;
	} else if (eventType == 2) {
	    addEventHelper(this.semList, userID, eventID, eventType, bookingCapacity);
	    return true;
	} else if (eventType == 3) {
	    addEventHelper(this.tsList, userID, eventID, eventType, bookingCapacity);
	    return true;
	} else

	    return false;
    }

    public boolean removeEventHelper(HashMap<String, EventDetails> curList, String userID, String eventID,
	    int eventType) {
	if (curList.containsKey(eventID)) {
	    curList.remove(eventID);
	    torLogRecorder.writeNorLog(torLogRecorder.paraConstructor(userID, "", eventType, eventID, 0),
		    Action.REMOVEEVENT, true, "true");
	    return true;
	} else {
	    torLogRecorder.writeNorLog(torLogRecorder.paraConstructor(userID, "", eventType, eventID, 0),
		    Action.REMOVEEVENT, false, "false");
	    return false;
	}

    }

    public boolean removeEvent(String userID, String eventID, int eventType) {
	if (eventType == 1) {
	    if (removeEventHelper(this.conList, userID, eventID, eventType))
		return true;
	    else
		return false;
	} else if (eventType == 2) {
	    if (removeEventHelper(this.semList, userID, eventID, eventType))
		return true;
	    else
		return false;
	} else if (eventType == 3) {
	    if (removeEventHelper(this.tsList, userID, eventID, eventType))
		return true;
	    else
		return false;
	} else

	    return false;

    }

    public String eventListHelper(HashMap<String, EventDetails> curList) {
	String rs = "";
	if (!curList.isEmpty()) {
	    Iterator itr = curList.entrySet().iterator();
	    while (itr.hasNext()) {
		Map.Entry entry = (Map.Entry) itr.next();
		String curEventID = (String) entry.getKey();
		EventDetails ed = (EventDetails) entry.getValue();
		int leftSeats = ed.getAvailableSeats();
		rs = rs + curEventID + "-" + ed.getAvailableSeats() + ",";
	    }
	}

	return rs;
    }

    public String listEventAvailability(String userID, int eventType) {
	String result = "";
	if (eventType == 1) {
	    result = eventListHelper(this.conList);
	    result += sendRequest(otwUdpPort, 0, userID, eventType, "");
	    result += sendRequest(mtlUdpPort, 0, userID, eventType, "");
	}
	if (eventType == 2) {
	    result = eventListHelper(this.semList);
	    result += sendRequest(otwUdpPort, 0, userID, eventType, "");
	    result += sendRequest(mtlUdpPort, 0, userID, eventType, "");
	}
	if (eventType == 3) {
	    result = eventListHelper(this.tsList);
	    result += sendRequest(otwUdpPort, 0, userID, eventType, "");
	    result += sendRequest(mtlUdpPort, 0, userID, eventType, "");
	}
	torLogRecorder.writeNorLog(torLogRecorder.paraConstructor(userID, "", eventType, "", 0),
		Action.LISTEVENTAVAILABILITY, true, result);
	return result;
    }

    public int bookEventHelper(String customerID, String eventID, int eventType) {
	EventDetails cur;
	if (eventType == 1) {
	    // conference
	    if (this.conList.keySet().contains(eventID)) {
		cur = this.conList.get(eventID);
		return (cur.modify(customerID, 0));
	    }
	    return -1;// the event doesn't exists;
	}
	if (eventType == 2) {
	    // sem
	    if (this.semList.keySet().contains(eventID)) {
		cur = this.semList.get(eventID);
		return (cur.modify(customerID, 0));
	    }

	    return -1;// the event doesn't exists;
	}
	if (eventType == 3) {
	    // ts
	    if (this.tsList.keySet().contains(eventID)) {
		cur = this.tsList.get(eventID);
		return (cur.modify(customerID, 0));
	    }
	    return -1;// the event doesn't exists;
	}
	return -4;

    }

    public int bookEvent(String customerID, String eventID, int eventType) {
	int rs = 0;
	int tripleCheck = tripleCheck(customerID, eventID);
	if (tripleCheck == -1) {
	    rs = -4;// same slot
	    return rs;
	} else if (tripleCheck == -2) {
	    rs = -5;// triple problem
	    return rs;
	} else {
	    if (eventID.startsWith("TOR")) {
		rs = bookEventHelper(customerID, eventID, eventType);
	    } else if (eventID.startsWith("MTL")) {
		rs = Integer.parseInt(sendRequest(mtlUdpPort, 2, customerID, eventType, eventID).trim());
	    } else if (eventID.startsWith("OTW")) {
		rs = Integer.parseInt(sendRequest(otwUdpPort, 2, customerID, eventType, eventID).trim());
	    }
	    if (rs == 0)
		torLogRecorder.writeNorLog(torLogRecorder.paraConstructor("", customerID, eventType, eventID, 0),
			Action.C_BOOKEVENT, true, String.valueOf(rs));
	    else
		torLogRecorder.writeNorLog(torLogRecorder.paraConstructor("", customerID, eventType, eventID, 0),
			Action.C_BOOKEVENT, false, String.valueOf(rs));
	    return rs;
	}

    }

    public String getBookingScheduleHelper(String customerID) {
	String result = "";
	Iterator itr = this.eventList.entrySet().iterator();
	while (itr.hasNext()) {
	    Map.Entry entry = (Map.Entry) itr.next();
	    String curListName = (String) entry.getKey();
	    if (curListName.equals("Conferences")) {
		curListName = "1";
	    } else if (curListName.equals("Seminars")) {
		curListName = "2";
	    } else if (curListName.equals("TradeShows")) {
		curListName = "3";
	    }
	    HashMap curList = (HashMap) entry.getValue();
	    Iterator innerItr = curList.entrySet().iterator();
	    while (innerItr.hasNext()) {
		Map.Entry innerEntry = (Map.Entry) innerItr.next();
		String eventID = (String) innerEntry.getKey();
		EventDetails ed = (EventDetails) innerEntry.getValue();
		ArrayList customerList = ed.getBookedList();
		Iterator listItr = customerList.iterator();
		while (listItr.hasNext()) {
		    if (listItr.next().equals(customerID)) {
			result += curListName + "-" + eventID + ",";
			break;
		    }
		}
	    }
	}
	return result;
    }

    public String getBookingSchedule(String customerID) {

	String result = "";
	result = getBookingScheduleHelper(customerID);
	result += sendRequest(mtlUdpPort, 1, customerID, 4, "");
	result += sendRequest(otwUdpPort, 1, customerID, 4, "");
	torLogRecorder.writeNorLog(torLogRecorder.paraConstructor("", customerID, 4, "", 0),
		Action.M_GETBOOKINGSCHEDULE, true, result);
	return result;
    }

    public int cancelEventHelper(String customerID, String eventID, int eventType) {
	EventDetails cur;

	if (eventType == 1) {
	    // conference
	    if (this.conList.keySet().contains(eventID)) {
		cur = this.conList.get(eventID);
		return (cur.modify(customerID, 1));
	    }
	    return -1;// the event doesn't exists;
	}
	if (eventType == 2) {
	    // sem
	    if (this.semList.keySet().contains(eventID)) {
		cur = this.semList.get(eventID);
		return (cur.modify(customerID, 1));
	    }
	    return -1;// the event doesn't exists;
	}
	if (eventType == 3) {
	    // ts
	    if (this.tsList.keySet().contains(eventID)) {
		cur = this.tsList.get(eventID);
		return (cur.modify(customerID, 1));
	    }
	    return -1;// the event doesn't exists;
	}
	return -4;
    }

    public int cancelEvent(String customerID, String eventID, int eventType) {
	int rs = 0;
	if (eventID.startsWith("TOR")) {
	    rs = cancelEventHelper(customerID, eventID, eventType);
	} else if (eventID.startsWith("OTW")) {
	    rs = Integer.parseInt(sendRequest(otwUdpPort, 3, customerID, eventType, eventID).trim());
	} else if (eventID.startsWith("MTL")) {
	    rs = Integer.parseInt(sendRequest(mtlUdpPort, 3, customerID, eventType, eventID).trim());
	}
	return rs;
    }

    public int swapEvent(String customerID, String newEventID, int newEventType, String oldEventID, int oldEventType) {

	int checkRs = swapCheck(customerID, newEventID, newEventType, oldEventID, oldEventType);
	if (checkRs == -3) {
	    torLogRecorder.writeNorLog(torLogRecorder.paraConstructor("", customerID, newEventType, newEventID, 0),
		    Action.C_SWAPEVENTS, false, String.valueOf(-2));
	    return -2;// this customer didn't book the oldEvent.

	}
	if (checkRs == -1) {
	    torLogRecorder.writeNorLog(torLogRecorder.paraConstructor("", customerID, newEventType, newEventID, 0),
		    Action.C_SWAPEVENTS, false, String.valueOf(-6));
	    return -6;// one cant order the event at the same time slot

	}
	if (checkRs == -2) {
	    torLogRecorder.writeNorLog(torLogRecorder.paraConstructor("", customerID, newEventType, newEventID, 0),
		    Action.C_SWAPEVENTS, false, String.valueOf(-7));
	    return -7;// three event problem

	}
	if (checkRs == 0) {
	    int rs = -100;
	    if (newEventID.startsWith("TOR")) {
		rs = bookEventHelper(customerID, newEventID, newEventType);
	    } else if (newEventID.startsWith("OTW")) {
		rs = Integer.parseInt(sendRequest(otwUdpPort, 2, customerID, newEventType, newEventID).trim());
	    } else if (newEventID.startsWith("MTL")) {
		rs = Integer.parseInt(sendRequest(mtlUdpPort, 2, customerID, newEventType, newEventID).trim());
	    }
	    if (rs == 0) {
		cancelEvent(customerID, oldEventID, oldEventType);
		torLogRecorder.writeNorLog(torLogRecorder.paraConstructor("", customerID, newEventType, newEventID, 0),
			Action.C_SWAPEVENTS, true, String.valueOf(rs));
		return rs;

	    } else {
		int finalRs = rs - 2;
		torLogRecorder.writeNorLog(torLogRecorder.paraConstructor("", customerID, newEventType, newEventID, 0),
			Action.C_SWAPEVENTS, false, String.valueOf(finalRs));
		return finalRs;
	    }

	}
	return -1;
    }

    private int tripleCheck(String customerID, String eventID) {
	String innerrs = "";
	Customer customer = new Customer(customerID);
	innerrs = getBookingSchedule(customerID);
	String[] rsArray = innerrs.split(",");
	if (rsArray.length == 0) {
	    return 0;
	} else {
	    String[] rsAry = new String[rsArray.length];
	    String inputCon = "";
	    String inputSem = "";
	    String inputTs = "";
	    for (int i = 0; i < rsArray.length; i++) {
		String[] temp = rsArray[i].split("-");
		if (temp[0].equals("1")) {
		    inputCon += temp[1] + ";";
		} else if (temp[0].equals("2")) {
		    inputSem += temp[1] + ";";
		} else if (temp[0].equals("3")) {
		    inputTs += temp[1] + ";";
		}
	    }
	    customer.updateFromServer(inputCon, 1);
	    customer.updateFromServer(inputSem, 2);
	    customer.updateFromServer(inputTs, 3);

	    return customer.checkBookable(eventID);
	}

    }

    private int swapCheck(String customerID, String newEventID, int newEventType, String oldEventID, int oldEventType) {
	String innerrs = "";
	Customer customer = new Customer(customerID);
	innerrs = getBookingSchedule(customerID);
	String[] rsArray = innerrs.split(",");
	if (rsArray.length == 0) {
	    return -3;
	} else {
	    String[] rsAry = new String[rsArray.length];
	    String inputCon = "";
	    String inputSem = "";
	    String inputTs = "";
	    for (int i = 0; i < rsArray.length; i++) {
		String[] temp = rsArray[i].split("-");
		if (temp[0].equals("1")) {
		    inputCon += temp[1] + ";";
		} else if (temp[0].equals("2")) {
		    inputSem += temp[1] + ";";
		} else if (temp[0].equals("3")) {
		    inputTs += temp[1] + ";";
		}
	    }
	    customer.updateFromServer(inputCon, 1);
	    customer.updateFromServer(inputSem, 2);
	    customer.updateFromServer(inputTs, 3);

	    if (customer.removeEvent(oldEventType, oldEventID)) {
		return customer.checkBookable(newEventID);
	    } else
		return -3;
	}

    }

    private String sendRequest(int serverPort, int requestType, String userID, int eventType, String eventID) {
	String rs = "";
	DatagramSocket aSocket = null;
	String requestStr = requestType + "-" + userID + "-" + eventType + "-" + eventID;
	try {
	    aSocket = new DatagramSocket(); // reference of the original socket
	    byte[] message = requestStr.getBytes(); // message to be passed
	    // is stored in byte array

	    InetAddress aHost = InetAddress.getByName("localhost"); // Host
	    // name is specified and the IP address of server host is
	    // calculated using DNS.

	    DatagramPacket request = new DatagramPacket(message, requestStr.length(), aHost, serverPort);// request
	    // packet ready
	    aSocket.send(request);// request sent out
	    System.out.println("Request message sent from the client is : " + new String(request.getData()));

	    byte[] buffer = new byte[1000];// to store the received data, it
	    // will be populated by what receive method returns
	    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);// reply packet ready but not populated.

	    // Client waits until the reply is
	    // received
	    // -----------------------------------------------------------------------
	    aSocket.receive(reply);// reply received and will populate reply
	    // packet now.
	    rs = new String(reply.getData());// print reply message after
	    // converting it to a string from bytes
	} catch (SocketException e) {
	    System.out.println("Socket: " + e.getMessage());
	} catch (IOException e) {
	    e.printStackTrace();
	    System.out.println("IO: " + e.getMessage());
	} finally {
	    if (aSocket != null)
		aSocket.close();// now all resources used by the socket are
	    // returned to the OS, so that there is no
	    // resource leakage, therefore, close the socket after it's use
	    // is completed to release resources.
	}
	return rs.trim();
    }

}
