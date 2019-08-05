package Host1;

import java.util.Queue;

import Model.*;

public class Replica1 {
	private Server_MTL MTL;
	private Server_OTW OTW;
	private Server_TOR TOR;
	
	private boolean crash = false;
	private Queue<Message> historyQueue;

	public Replica1() {
		try {
			MTL = new Server_MTL();
			OTW = new Server_OTW();
			TOR = new Server_TOR();

			Thread t1 = new Thread(MTL);
			Thread t2 = new Thread(OTW);
			Thread t3 = new Thread(TOR);
			
			t1.start();
			t2.start();
			t3.start();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isCrash() {
		return crash;
	}

	public void setCrash(boolean crash) {
		this.crash = crash;
	}

	public Queue<Message> getHistoryQueue() {
		return historyQueue;
	}

	public void setHistoryQueue(Queue<Message> historyQueue) {
		this.historyQueue = historyQueue;
	}
	
	public String sendRequest(Message msg) {
		String result = "";
		int seqId = msg.getSeqNum();
		String operation[] = msg.getOperationMsg().split(",");
		Server_interface server = null;
		String userId = operation[1].substring(0, 3);
		boolean flag = true;
		if (userId.equalsIgnoreCase("MTL"))
			server = MTL;
		else if (userId.equalsIgnoreCase("OTW"))
			server = OTW;
		else if (userId.equalsIgnoreCase("TOR"))
			server = TOR;
		switch (operation[0]) {
		case ("addEvent"):
			result = server.addEvent(operation[1], operation[2], operation[3], Integer.parseInt(operation[4]));
			break;
		case ("removeEvent"):
			result = server.removeEvent(operation[1], operation[2], operation[3]);
			break;
		case ("listEventAvailability"):
			result = server.listEventAvailability(operation[1], operation[2]);
			flag = false;
			break;
		case ("bookEvent"):
			result = server.bookEvent(operation[1], operation[2], operation[3]);
			break;
		case ("getBookingSchedule"):
			result = server.getBookingSchedule(operation[1]);
			flag = false;
			break;
		case ("cancelEvent"):
			result = server.cancelEvent(operation[1], operation[3], operation[2]);
			break;
		case ("swapEvent"):
			result = server.swapEvent(operation[1], operation[2], operation[3], operation[4], operation[5]);
			break;
		default:
			System.out.println("Invalid input please try again.");
			break;
		}
		if(seqId <= -2 && flag == true) {
			int i = result.lastIndexOf("successfully");
			if(i >= 0) {
				result = result.substring(0, i) + "failure!";
			}else{
				result = operation[0] + " successfully!";
			}
		} 
		
		if(result.contains("successfully") && flag == true) {
			result = "T:" + result;
		}else if(!result.contains("successfully") && flag == true) {
			result = "F:" + result;
		}
		if(result.equals("")) {
			result = "NULL";
		}
		return result;
	}
 
	public void recoverRplicaData() {
		MTL.clearMap();
		OTW.clearMap();
		TOR.clearMap();
		while (historyQueue.size() > 0) {
			Message msg = historyQueue.poll();
			System.out.println("recover --> " + msg.getOperationMsg());
			sendRequest(msg);
		}
	}


}
