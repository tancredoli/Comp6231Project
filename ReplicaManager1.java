package Host1;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.*;

public class ReplicaManager1 implements Runnable {
	public static int rmPort1 = 6666;
	public static int rmPort1_failure = 1111;
	Logger logger;
	int replicaID;
	int seqNum;
	Replica1 replica1;
	HashMap<Integer, Message> holdBackQueue;
	HashMap<Integer, Integer> counter;
	Queue<Message> deliveryQueue;
	Queue<Message> historyQueue;
	public int crashConfirm = 0;

	ReplicaManager1() {
		replicaID = 1;
		counter = new HashMap<>();
		holdBackQueue = new HashMap<>();
		deliveryQueue = new LinkedList<>();
		historyQueue = new LinkedList<>();
		replica1 = new Replica1();
	}

	public void initCounter() {
		counter.put(1, 0);
		counter.put(2, 0);
		counter.put(3, 0);
		counter.put(4, 0);
	}

	public int SetCounter(int replicaId) {
		int i = 0;
		int failureTime = counter.get(replicaId) + 1;
		counter.put(replicaId, failureTime);
		System.out.println(replicaId + " " + "has failed " + failureTime + "times");
		if (counter.get(replicaId) >= 3) {
			// logger.info(replicaId + " has been failed 3 times.");
			counter.put(replicaId, 0);
			i = -1;
		}
		return i;
	}

	public void recoverFromCrash(String msg) {
		int crashNum = Integer.parseInt(msg.split(":")[1]);
		if (crashNum == replicaID) {
			// recoverFromCrash
			this.logger.info("Crash: Replica" + replicaID);
		} else {
			String crashInfo = "IfCrash";
			System.out.println("reply:" + crashInfo);

			switch (crashNum) {// check if replica1 is alive
			case 1:
				sendCrashToRM(RMPort.RM_PORT.rmPort1_failure, crashInfo);
				break;
			case 2:
				sendCrashToRM(RMPort.RM_PORT.rmPort2_failure, crashInfo);
				break;
			case 3:
				sendCrashToRM(RMPort.RM_PORT.rmPort3_failure, crashInfo);
				break;
			case 4:
				sendCrashToRM(RMPort.RM_PORT.rmPort4_failure, crashInfo);
				break;
			}
		}
	}

	private void restartReplica() throws IOException {
		try {
			replica1 = new Replica1();
			replica1.historyQueue = this.historyQueue;
			replica1.recoverRplicaData();
			replica1.crash = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("restart and recover replica1.");

	}

	private void checkAndExecuteMessage(DatagramSocket aSocket) throws IOException {
		Message message = this.deliveryQueue.peek();
		if (message != null) {
			message = this.deliveryQueue.poll();
			historyQueue.offer(message);
			sendToReplicaAndGetReply(message, aSocket);
			checkAndExecuteMessage(aSocket);
		}
	}

	private void moveToDeliveryQueue(DatagramSocket aSocket) throws IOException {
		if (holdBackQueue.size() != 0) {
			if (holdBackQueue.containsKey(this.seqNum)) {
				Message message = holdBackQueue.get(this.seqNum);
				if (!this.deliveryQueue.contains(message)) {
					this.deliveryQueue.offer(message);
					this.holdBackQueue.remove(this.seqNum);
					this.seqNum++;
					checkAndExecuteMessage(aSocket);
					moveToDeliveryQueue(aSocket);
				}
			}
		}
	}

	private void moveToHoldBackQueue(String msg, DatagramSocket aSocket) throws IOException {
		int id = Integer.parseInt(msg.split(":")[0]);
		if (!holdBackQueue.containsKey(id)) {
			Message message = splitMessge(msg);
			holdBackQueue.put(id, message);
		}
		moveToDeliveryQueue(aSocket);
	}

	private void sendToFE(DatagramSocket aSocket, String msgFromReplica, String msgAddr, String msgPort) {
		DatagramPacket reply = null;
		boolean revResponse = false;
		while (!revResponse) {
			try {
				// aSocket.setSoTimeout(TIMEOUT);
				InetAddress address = InetAddress.getByName(msgAddr);
				// InetAddress address = InetAddress.getByName("132.205.95.183");// li
				byte[] data = msgFromReplica.getBytes();
				DatagramPacket aPacket = new DatagramPacket(data, data.length, address, Integer.parseInt(msgPort));
				aSocket.send(aPacket);

				// byte[] buffer = new byte[2000];
				// reply = new DatagramPacket(buffer, buffer.length);
				// aSocket.receive(reply);
				revResponse = true;
				// logger.info("RM1 sends message to FE:" + msgFromReplica);
				// aSocket.close();
				// } catch (InterruptedIOException e) {
				// send_count += 1;
				// System.out.println("Time out," + (MAXNUM - send_count) + " more tries...");
			} catch (Exception e) {
				// System.out.println("udpClient error: " + e);
			} finally {
				if (aSocket != null) {
					aSocket.close();
				}
			}
		}
	}

	private void sendToReplicaAndGetReply(Message msg, DatagramSocket aSocket) throws IOException {
		String reply = "";

		if (replica1 != null) {
			if (replica1.crash == true) {
				System.out.println("Replica1 crash");
				// logger.info("Replica1 shut down");
				return;
			}

			try {
				reply = msg.seqId + ":" + this.replicaID + ":" + replica1.executeMsg(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("reply:" + reply);
			DatagramSocket socket = null;
			socket = new DatagramSocket();
			sendToFE(socket, reply, msg.FEAddr, msg.FEPort);

			// logger.info("RM1 sends message to Replica1: " + msg.operationMsg + "; reply
			// from Replica1: " + reply);
		}

	}

	public Message splitMessge(String message) {
		Message msg = new Message();
		// seqId,FEaddr,(operation,userId......)
		String[] msgArry = message.split(":");
		msg.seqId = Integer.parseInt(msgArry[0]);
		String[] sendMsg = msgArry[1].split(",");
		msg.FEAddr = sendMsg[0];
		msg.FEPort = sendMsg[1];
		msg.operationMsg = msgArry[2];
		// msg.libCode = msg.operationMsg.split(",")[1].substring(0, 3);
		return msg;
	}

	private void sendCrashToRM(int RMFailurePort, String crashMsg) {

		DatagramPacket reply = null;
		int send_count = 0;
		boolean revResponse = false;
		DatagramSocket aSocket = null;
		String crashConfirm = "";
		try {
			aSocket = new DatagramSocket();
			InetAddress address = InetAddress.getByName("localhost");
			byte[] data = crashMsg.getBytes();
			DatagramPacket aPacket = new DatagramPacket(data, data.length, address, RMFailurePort);

			aSocket.send(aPacket);
			byte[] buffer = new byte[1000];
			reply = new DatagramPacket(buffer, buffer.length);
			logger.info("RM1 sends crush message to RM:" + crashMsg);

			aSocket.receive(reply);
			crashConfirm = new String(reply.getData()).trim();
			logger.info("crashConfirm: " + crashConfirm);

			if (crashConfirm.equals("DidCrash")) {
				byte[] msg = "RestartReplica".getBytes();
				DatagramPacket restartPacket = new DatagramPacket(msg, msg.length, address, RMFailurePort);
				aSocket.send(restartPacket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startCrashListener(int crashPort) {
		try {
			DatagramSocket asocket = new DatagramSocket(crashPort);
			DatagramPacket apocket = null;
			byte[] buf = null;
			logger.info("RM crash is listenning ");
			while (true) {
				buf = new byte[2000];
				apocket = new DatagramPacket(buf, buf.length);
				asocket.receive(apocket);
				String message = new String(apocket.getData()).trim();
				System.out.println("UDP RM1 crash receive : " + message);
				String[] messageSplited = message.split(":");
				System.out.println("messageSplited[0]--" + messageSplited[0]);

				switch (messageSplited[0]) {
				case "IfCrash": // other rms send udp msg to ask if rm1 crash
					replyCrashChecking(asocket, apocket);
					break;
				case "RestartReplica":// other rms confirm rm1 did crash
					crashConfirm++;
					if (crashConfirm >= 2) {
						restartReplica();
						crashConfirm = 0;
					}
					break;
				}
			}
		} catch (Exception e) {

		}
	}

	public void replyCrashChecking(DatagramSocket asocket, DatagramPacket apocket) throws IOException {
		String result = "Alive";
		try {
			// boolean isAlive = replica1.monServer.aSocket.isConnected();
			result = "DidCrash";
			System.out.println("DidCrash");
		} catch (Exception e) {
			result = "DidCrash";
		}
		DatagramPacket replyP = new DatagramPacket(result.getBytes(), result.getBytes().length, apocket.getAddress(),
				apocket.getPort());
		asocket.send(replyP);
	}

	public void RMListener(int RMPort) throws Exception {
		// DatagramSocket asocket = new DatagramSocket(RMPort);
		DatagramPacket packet = null;
		byte[] buffer = null;
		// logger.info("RM1 is listenning ");
		DatagramSocket socket = new DatagramSocket(RMPort);
		// MulticastSocket asocket = new MulticastSocket(RMPort);
		// asocket.joinGroup(InetAddress.getByName("224.0.0.1"));

		while (true) {
			buffer = new byte[2000];
			packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String message = new String(packet.getData()).trim();

			// socket.send(packet);// acknowledge

			System.out.println("UDP receive : " + message);

			// logger.info("RM1 receives message:" + message);

			String[] messageSplited = message.split(":");
			System.out.println("messageSplited[0]--" + messageSplited[0]);

			switch (messageSplited[0]) {
			case "Failure":
				int needFix = SetCounter(Integer.parseInt(messageSplited[2]));// Failure:seqID:replicaId
				if (needFix == -1) {
					System.out.println("Fixed the failure");
				}
				break;
			case "Crash":
				recoverFromCrash(message); // Crash:seqID:replicaId (if choose crash
				break;
			case "SetCrash":// SetCrash:replicaId
				replica1.crash = true;
				break;
			default:
				moveToHoldBackQueue(message, socket); // from Sequencer, normal operation message
				break;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		// Logger rmLogger = Logger.getLogger("RM1.log");
		// rmLogger.setLevel(Level.ALL);

		// FileHandler handler = new FileHandler("RM1.log");
		// handler.setFormatter(new logSetFormatter());
		// rmLogger.addHandler(handler);

		ReplicaManager1 rm = new ReplicaManager1();
		rm.initCounter();

		Thread t1 = new Thread(rm);
		t1.start();
	}

	@Override
	public void run() {
		try {
			RMListener(rmPort1);
			startCrashListener(rmPort1_failure);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
