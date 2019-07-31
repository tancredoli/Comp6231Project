package Host1;

import java.io.IOException;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.*;

public class Replica1 {

	public Logger log;
	public Server_MTL MTL;
	public Server_OTW OTW;
	public Server_TOR TOR;
	
	public boolean crash = false;
	public Queue<Message> historyQueue;

	public Replica1() {
		try {

			// Logger conserver1_log = Logger.getLogger("conserver1.log");
			// createLogger("conserver1.log", conserver1_log);
			//
			// Logger mcgserver1_log = Logger.getLogger("mcgserver1_log");
			// createLogger("mcgserver1.log", mcgserver1_log);
			//
			// Logger monserver1_log = Logger.getLogger("monserver1.log");
			// createLogger("monserver1.log", monserver1_log);

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

	public String executeMsg(Message msg) {
		String result = "";
		int sequenceid=msg.seqId;
		String operation[] = msg.operationMsg.split(",");
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
		if(sequenceid<=2 && flag == true) {
			int i = result.lastIndexOf("successfully");
			if(i >= 0) {
				result = result.substring(0, i) + "failure!";
			}else{
				result = result.substring(0, i) + "successfully!";
			}
		} 
		return result;
	}
 

//	private static void createLogger(String log_name, Logger logger) throws IOException {
//		logger.setLevel(Level.ALL);
//		FileHandler handler = new FileHandler(log_name);
//		handler.setFormatter(new logSetFormatter());
//		logger.addHandler(handler);
//	}

	public void recoverRplicaData() {
		while (historyQueue.size() > 0) {
			Message msg = historyQueue.poll();
			System.out.println("recover --- " + msg.operationMsg);
			executeMsg(msg);
		}
	}


}
