package FrontEnd;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.omg.CORBA.ORB;

import FEInterface.CommonInterfacePOA;
import Utility.Action;
import Utility.Constants;
import Utility.LogRecorder;
import Utility.Port;

/**
 * @Author: Rui
 * @Description:
 * @Date: Created in 4:03 PM 2019-07-28
 * @Modified by:
 */
public class FEImplementation extends CommonInterfacePOA {

    private static final String isTrue = "successfully";
    private ORB orb;
    private Integer FE_TIMEOUT;
    private LogRecorder feLoger;

    public void sendCrash(int rep_num) {
	DatagramSocket socket = null;
	try {
	    socket = new DatagramSocket();
	    StringBuilder sb = new StringBuilder();
	    sb.append("SetCrash:").append(rep_num);
	    InetAddress address1 = InetAddress.getByName(Port.REPLICA2_IP);
	    DatagramPacket packet1 = new DatagramPacket(sb.toString().getBytes(), sb.length(), address1,
		    Port.REPLICA_NOR_PORT);
	    System.out.println("Crash demand has been sent to : " + Port.REPLICA1_IP + Constants.separator_comma
		    + Port.REPLICA_NOR_PORT);
	    socket.send(packet1);
	} catch (SocketException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    if (socket != null) {
		socket.close();
	    }
	}

    }

    public void setOrb(ORB orb) {
	this.orb = orb;

    }

    public FEImplementation() {
	super();
	this.FE_TIMEOUT = 1000;
	try {
	    feLoger = new LogRecorder(5);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public boolean addEvent(String userID, String eventID, int eventType, int bookingCapacity) {
	StringBuilder sb = new StringBuilder();
	sb.append("addEvent").append(Constants.separator_comma).append(userID).append(Constants.separator_comma)
		.append(eventID).append(Constants.separator_comma).append(eventType).append(Constants.separator_comma)
		.append(bookingCapacity);
	String rs = new String();
	rs = receivingThread(sb.toString(), rs);
	if (rs.contains(isTrue)) {
	    feLoger.writeNorLog(feLoger.paraConstructor(userID, "", eventType, eventID, bookingCapacity),
		    Action.ADDEVENT, true, String.valueOf("true"));
	    return true;
	}

	else {
	    feLoger.writeNorLog(feLoger.paraConstructor(userID, "", eventType, eventID, bookingCapacity),
		    Action.ADDEVENT, false, String.valueOf("false"));
	    return false;
	}

    }

    @Override
    public boolean removeEvent(String userID, String eventID, int eventType) {
	StringBuilder sb = new StringBuilder();
	sb.append("removeEvent").append(Constants.separator_comma).append(userID).append(Constants.separator_comma)
		.append(eventID).append(Constants.separator_comma).append(eventType);
	String rs = new String();
	rs = receivingThread(sb.toString(), rs);
	if (rs.contains(isTrue)) {
	    feLoger.writeNorLog(feLoger.paraConstructor(userID, "", eventType, eventID, 0), Action.REMOVEEVENT, true,
		    String.valueOf("true"));
	    return true;
	}

	else {
	    feLoger.writeNorLog(feLoger.paraConstructor(userID, "", eventType, eventID, 0), Action.REMOVEEVENT, false,
		    String.valueOf("false"));
	    return false;
	}

    }

    @Override
    public String listEventAvailability(String userID, int eventType) {
	StringBuilder sb = new StringBuilder();
	sb.append("listEventAvailability").append(Constants.separator_comma).append(userID)
		.append(Constants.separator_comma).append(eventType);
	String rs = new String();
	rs = receivingThread(sb.toString(), rs);
	feLoger.writeNorLog(feLoger.paraConstructor(userID, "", eventType, "", 0), Action.LISTEVENTAVAILABILITY, true,
		String.valueOf(rs));
	return rs;
    }

    @Override
    public int bookEvent(String customerID, String eventID, int eventType) {
	StringBuilder sb = new StringBuilder();
	sb.append("bookEvent").append(Constants.separator_comma).append(customerID).append(Constants.separator_comma)
		.append(eventID).append(Constants.separator_comma).append(eventType);
	String rs = new String();
	rs = receivingThread(sb.toString(), rs);
	if (rs.contains(isTrue)) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, eventType, eventID, 0), Action.C_BOOKEVENT,
		    true, String.valueOf(0));
	    return 0;
	}

	else if (rs.contains("exit")) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, eventType, eventID, 0), Action.C_BOOKEVENT,
		    false, String.valueOf(-1));
	    return -1;
	} else if (rs.contains("booked this")) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, eventType, eventID, 0), Action.C_BOOKEVENT,
		    false, String.valueOf(-2));
	    return -2;
	} else if (rs.contains("full")) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, eventType, eventID, 0), Action.C_BOOKEVENT,
		    false, String.valueOf(-3));
	    return -3;
	} else {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, eventType, eventID, 0), Action.C_BOOKEVENT,
		    false, String.valueOf(-5));
	    return -5;
	}
    }

    @Override
    public String getBookingSchedule(String customerID) {
	StringBuilder sb = new StringBuilder();
	sb.append("getBookingSchedule").append(Constants.separator_comma).append(customerID);
	String rs = new String();
	rs = receivingThread(sb.toString(), rs);
	feLoger.writeNorLog(feLoger.paraConstructor("", customerID, 0, "", 0), Action.C_GETBOOKINGSCHEDULE, true,
		String.valueOf(rs));
	return rs;
    }

    @Override
    public int cancelEvent(String customerID, String eventID, int eventType) {
	StringBuilder sb = new StringBuilder();
	sb.append("cancelEvent").append(Constants.separator_comma).append(customerID).append(Constants.separator_comma)
		.append(eventID).append(Constants.separator_comma).append(eventType);
	String rs = new String();
	rs = receivingThread(sb.toString(), rs);
	if (rs.contains(isTrue)) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, eventType, eventID, 0), Action.C_CANCELEVENT,
		    true, String.valueOf(0));
	    return 0;
	}

	else if (rs.contains("booking_record")) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, eventType, eventID, 0), Action.C_CANCELEVENT,
		    false, String.valueOf(-1));
	    return -1;
	}

	else {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, eventType, eventID, 0), Action.C_CANCELEVENT,
		    false, String.valueOf(-2));
	    return -2;
	}

    }

    @Override
    public int swapEvent(String customerID, String newEventID, int newEventType, String oldEventID, int oldEventType) {
	StringBuilder sb = new StringBuilder();
	sb.append("swapEvent").append(Constants.separator_comma).append(customerID).append(Constants.separator_comma)
		.append(newEventID).append(Constants.separator_comma).append(newEventType)
		.append(Constants.separator_comma).append(oldEventID).append(Constants.separator_comma)
		.append(oldEventType);
	String rs = new String();
	rs = receivingThread(sb.toString(), rs);
	if (rs.contains(isTrue)) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, newEventType, newEventID, 0),
		    Action.C_SWAPEVENTS, true, String.valueOf(0));
	    return 0;
	}

	else if (rs.contains("doesn't have booked")) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, newEventType, newEventID, 0),
		    Action.C_SWAPEVENTS, false, String.valueOf(-2));
	    return -2;
	}

	else if (rs.contains("have already booked")) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, newEventType, newEventID, 0),
		    Action.C_SWAPEVENTS, false, String.valueOf(-4));
	    return -4;
	} else if (rs.contains("is not exit")) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, newEventType, newEventID, 0),
		    Action.C_SWAPEVENTS, false, String.valueOf(-3));
	    return -3;
	} else if (rs.contains("booked 3")) {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, newEventType, newEventID, 0),
		    Action.C_SWAPEVENTS, false, String.valueOf(-7));
	    return -7;
	} else {
	    feLoger.writeNorLog(feLoger.paraConstructor("", customerID, newEventType, newEventID, 0),
		    Action.C_SWAPEVENTS, false, String.valueOf(-5));
	    return -5;
	}

    }

    private String receivingThread(String requestStr, String rs) {
	Callable sendReveive = new Callable() {
	    @Override
	    public String call() throws Exception {
		// TODO Auto-generated method stub
		return sendRequest(requestStr);
	    }

	};

	ExecutorService pool = Executors.newFixedThreadPool(1);
	Future f1 = pool.submit(sendReveive);
	try {

	    rs = f1.get().toString();
	    System.out.println("----------------------------" + rs);

	} catch (InterruptedException | ExecutionException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    pool.shutdown();
	}
	return rs;
    }

    @SuppressWarnings("finally")
    private String sendRequest(String requestStr) {

	ArrayList<String> rsList = new ArrayList<>();
	ArrayList<String> tfList = new ArrayList<>();
	ArrayList<Integer> timeList = new ArrayList<>();
	for (int i = 0; i < Constants.RM_NUM; i++) {
	    tfList.add("");
	    rsList.add("");
	    timeList.add(0);
	}
	DatagramSocket aSocket = null;
	DatagramSocket listenSocket = null;
	boolean isTF = true;
	try {
	    aSocket = new DatagramSocket();

	    StringBuilder sb = new StringBuilder();

	    sb.append(Port.FRONTEND_IP).append(Constants.separator_comma).append(Port.FRONTEND_PORT)
		    .append(Constants.separator_colon);

	    requestStr += sb.toString();

	    byte[] message = requestStr.getBytes();

	    InetAddress aHost = InetAddress.getByName(Port.SEQUENCER_IP);

	    DatagramPacket request = new DatagramPacket(message, requestStr.length(), aHost, Port.SEQUENCER_PORT);

	    aSocket.send(request);

	    long startTime = System.currentTimeMillis();

	    System.out.println("Request message sent from the client is : " + new String(request.getData()));

	    listenSocket = new DatagramSocket(Port.FRONTEND_PORT);
	    listenSocket.setSoTimeout(FE_TIMEOUT);

	    int time = 0;

	    while (time < Constants.RM_NUM) {

		byte[] buffer = new byte[1000];
		DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
		listenSocket.receive(reply);

		long endTime = System.currentTimeMillis();
		String rawStr = new String(reply.getData()).trim();

		String[] strArr = rawStr.split(Constants.separator_colon);

		if (strArr.length > 2 && (strArr[2].equals("T") || strArr[2].equals("F"))) {

		    String rspStr = rawStr.substring(3 + strArr[0].length() + strArr[1].length() + strArr[2].length());
		    rsList.set(Integer.parseInt(strArr[1]) - 1, strArr[0] + Constants.separator_sup + strArr[2]);
		    if (isTF && strArr.length == 4 && (strArr[2].equals("T") || strArr[2].equals("F"))) {
			tfList.set(Integer.parseInt(strArr[1]) - 1, strArr[0] + Constants.separator_sup + strArr[3]);
			isTF = false;
		    }

		} else {
		    // listAvailability and getBookingSchedule
		    String rspStr = rawStr.substring(2 + strArr[0].length() + strArr[1].length());
		    if (rspStr.isEmpty()) {
			rspStr = " ";
		    }
		    String str = unitFormat(rspStr);
		    rsList.set(Integer.parseInt(strArr[1]) - 1, strArr[0] + Constants.separator_sup + str);

		}
		timeList.set(Integer.parseInt(strArr[1]) - 1, (int) (endTime - startTime));
		System.out.println("Receive msg form replica" + strArr[1] + " : " + new String(reply.getData()));

		time++;
	    }
	} catch (SocketTimeoutException e) {
	    // TODO Auto-generated catch block
	    multicastCrash(rsList);

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    if (aSocket != null)
		aSocket.close();
	    if (listenSocket != null) {
		listenSocket.close();
	    }
	    if (checkErrType(rsList))
		;
	    else {
		multicastFailure(rsList);
	    }
	    synchronized (FE_TIMEOUT) {
		calTimeOut(timeList);
		System.out.println("Current FETimeOut updated: " + FE_TIMEOUT);
	    }

	    if (isTF) {
		String rs = findMajority(rsList);
		String rsArr[] = rs.split(Constants.separator_sup);
		return rsArr[1];

	    } else {
		return getRS(tfList);
	    }

	}
    }

    private String getRS(ArrayList<String> tfList) {
	Iterator itr = tfList.iterator();
	while (itr.hasNext()) {
	    String temp = (String) itr.next();
	    if (!temp.isEmpty()) {
		String[] arr = temp.split(Constants.separator_sup);
		return arr[1];
	    }

	}
	return null;
    }

    private String unitFormat(String rspStr) {
	// TODO Auto-generated method stub

	ArrayList<String> aList = new ArrayList<>();
	String[] strArr = rspStr.split(",");
	for (int i = 0; i < strArr.length; i++) {
	    aList.add(strArr[i]);

	}
	aList.sort(null);
	return aList.toString();
    }

    private void calTimeOut(ArrayList<Integer> timeList) {
	// TODO Auto-generated method stub

	// Iterator<Integer> itr = timeList.iterator();
	// while (itr.hasNext()) {
	// int temp = itr.next();
	// if (temp == 0) {
	// itr.remove();
	// }
	// }
	// OptionalDouble rs = timeList.stream().mapToDouble(a -> a).average();

	timeList.sort(null);
	int compare = timeList.get(timeList.size() - 1) * 2;
	if (this.FE_TIMEOUT < compare) {
	    this.FE_TIMEOUT = compare;
	}
    }

    private void multicastFailure(ArrayList<String> tfList) {
	// TODO Auto-generated method stub
	int fail_num = -1;
	int fail_seq = -1;

	fail_num = tfList.indexOf(findWrong(tfList)) + 1;
	String temp = findWrong(tfList);

	String[] temp_arr = temp.split(Constants.separator_sup);
	fail_seq = Integer.parseInt(temp_arr[0]);
	multicastToRMs(0, fail_seq, fail_num);

    }

    private boolean checkErrType(ArrayList<String> tfList) {
	// TODO Auto-generated method stub

	if (tfList.size() > 1) {
	    ArrayList<String> list = new ArrayList<>();

	    for (int i = 0; i < tfList.size(); i++) {
		list.add(tfList.get(i));
	    }
	    Iterator<String> itr2 = list.iterator();
	    while (itr2.hasNext()) {
		if (itr2.next().equals(""))
		    itr2.remove();
	    }
	    String string = list.get(0);
	    Iterator<String> itr3 = list.iterator();
	    while (itr3.hasNext()) {
		if (itr3.next().equals(string))
		    ;
		else {
		    return false;
		}
	    }
	    return true;
	} else {
	    return true;
	}

    }

    private void multicastCrash(ArrayList<String> rsList) {
	// TODO Auto-generated method stub

	int fail_num = -1;
	int fail_seq = -1;
	fail_num = rsList.indexOf("") + 1;

	Iterator<String> itr = rsList.iterator();
	while (itr.hasNext()) {
	    String temp = itr.next();
	    if (!temp.equals("")) {
		String[] tempArr = temp.split(Constants.separator_sup);
		fail_seq = Integer.parseInt(tempArr[0]);
		break;
	    }

	}
	if (fail_num > 0 && fail_seq > -1) {
	    multicastToRMs(1, fail_seq, fail_num);
	}

    }

    private void multicastToRMs(int type, int num_seq, int wrong_rep) {
	// TODO Auto-generated method stub
	DatagramSocket socket = null;
	try {
	    socket = new DatagramSocket();
	    StringBuilder sb = new StringBuilder();
	    StringBuilder logbd = new StringBuilder();
	    if (type == 0) {
		sb.append("Failure");
		logbd.append("Wrong type: Failure");
	    } else if (type == 1) {
		sb.append("Crash");
		logbd.append("Wrong type: Crash");
	    }
	    sb.append(Constants.separator_colon).append(num_seq).append(Constants.separator_colon).append(wrong_rep);
	    InetAddress address1 = InetAddress.getByName(Port.REPLICA1_IP);
	    InetAddress address2 = InetAddress.getByName(Port.REPLICA2_IP);
	    InetAddress address3 = InetAddress.getByName(Port.REPLICA3_IP);
	    InetAddress address4 = InetAddress.getByName(Port.REPLICA4_IP);
	    DatagramPacket packet1 = new DatagramPacket(sb.toString().getBytes(), sb.length(), address1,
		    Port.REPLICA_NOR_PORT);
	    DatagramPacket packet2 = new DatagramPacket(sb.toString().getBytes(), sb.length(), address2,
		    Port.REPLICA_NOR_PORT);
	    DatagramPacket packet3 = new DatagramPacket(sb.toString().getBytes(), sb.length(), address3,
		    Port.REPLICA_NOR_PORT);
	    DatagramPacket packet4 = new DatagramPacket(sb.toString().getBytes(), sb.length(), address4,
		    Port.REPLICA_NOR_PORT);

	    logbd.append("|").append("Wrong Replica:").append(wrong_rep).append("|").append("Wrong Snum:")
		    .append(num_seq);
	    System.out.println(logbd.toString());
	    feLoger.writeSeqLog(logbd.toString());
	    socket.send(packet1);
	    socket.send(packet2);
	    socket.send(packet3);
	    socket.send(packet4);

	} catch (SocketException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    if (socket != null) {
		socket.close();
	    }
	}

    }

    String findMajority(ArrayList<String> rsList) {

	int size = rsList.size();
	double max;
	if (size % 2 == 0) {
	    max = Math.floorDiv(rsList.size(), 2);
	} else {
	    max = Math.floorDiv(rsList.size(), 2) + 1;
	}
	String str = "no_result";
	for (int i = 0; i < rsList.size(); i++) {
	    int count = 0;
	    Iterator<String> iterator = rsList.iterator();
	    while (iterator.hasNext()) {
		String cur = iterator.next();
		if (cur.equals(rsList.get(i))) {
		    count++;
		}
	    }
	    if (count >= max) {
		return rsList.get(i);
	    }
	}

	return str;
    }

    String findWrong(ArrayList<String> rsList) {

	String majorStr = findMajority(rsList);

	Iterator<String> iterator = rsList.iterator();
	while (iterator.hasNext()) {
	    String temp = iterator.next();
	    if (!temp.equals(majorStr) && !temp.isEmpty()) {
		return temp;
	    }
	}

	return majorStr;
    }

}
