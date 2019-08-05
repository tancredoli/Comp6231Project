package Host1;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import Model.*;

public class ReplicaManager1 {	
	private int replicaID;
	private int seqNum = 0;
	private Replica1 replica1;
	private HashMap<Integer, Integer> counter;
	private HashMap<Integer, Message> holdBackQueue;
	private Queue<Message> deliveryQueue;
	private Queue<Message> historyQueue;
	private Queue<String> failureQueue;
	private int crasNumber = 0;
	private int CRASH_CONFIRM_NUM = 3;

	ReplicaManager1() {
		replicaID = 2;
		counter = new HashMap<>();
		holdBackQueue = new HashMap<>();
		deliveryQueue = new LinkedList<>();
		historyQueue = new LinkedList<>();
		failureQueue = new LinkedList<>();
		replica1 = new Replica1();
		initLogger();
	}

	public void initLogger() {
		try {
			SimpleFormatter formatter = new SimpleFormatter();
			RMPort.LOGGER = Logger.getLogger("RM" + replicaID + ".log");
			RMPort.LOGGER.setUseParentHandlers(false);
			File file = new File(RMPort.dir);
			if (!file.exists()) {
				file.mkdir();
			}
			RMPort.FH = new FileHandler(RMPort.dir + "/RM" + replicaID + ".log", true);
			RMPort.FH.setFormatter(formatter);
			RMPort.LOGGER.addHandler(RMPort.FH);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		System.out.println("Replica " + replicaId + " has failed " + failureTime + " times");
		RMPort.LOGGER.info("Replica " + replicaId + " has failed " + failureTime + " times");
		if (counter.get(replicaId) >= 3) {
			counter.put(replicaId, 0);
			i = 1;
			RMPort.LOGGER.info("Replica " + replicaId + " has fixed the filure.");
		}
		return i;
	}

	public void solveCrash(int crashRM) {
		if (crashRM == replicaID) {
			System.out.println("This Replica maybe crash!");
		} else {
			String crashInfo = "IfCrash";
			System.out.println("send crash message to other RM:" + crashInfo);
			switch (crashRM) {
			case 1:
				sendCrashToRM(RMPort.RM_PORT.rmHost1, RMPort.RM_PORT.rmPort1_crash, crashInfo);
				break;
			case 2:
				sendCrashToRM(RMPort.RM_PORT.rmHost2, RMPort.RM_PORT.rmPort2_crash, crashInfo);
				break;
			case 3:
				sendCrashToRM(RMPort.RM_PORT.rmHost3, RMPort.RM_PORT.rmPort3_crash, crashInfo);
				break;
			case 4:
				sendCrashToRM(RMPort.RM_PORT.rmHost4, RMPort.RM_PORT.rmPort4_crash, crashInfo);
				break;
			}
		}
	}
	
	public static int getRMName(String RMAdd) {
		int RMname = 0;
		if(RMAdd == RMPort.RM_PORT.rmHost1)
			RMname = 1;
		if(RMAdd == RMPort.RM_PORT.rmHost2)			
			RMname = 2;
		if(RMAdd == RMPort.RM_PORT.rmHost3)			
			RMname = 3;
		if(RMAdd ==RMPort.RM_PORT.rmHost4)
			RMname = 4;
		return RMname;
	}

	private void sendCrashToRM(String RMAddress, int CrashRMPort, String msg) {
		DatagramPacket reply = null;
		DatagramSocket socket = null;
		String ifCrash = "";
		try {
			socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(RMAddress);
			byte[] data = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, CrashRMPort);
			socket.send(packet);
			RMPort.LOGGER.info("RM " + replicaID + " send message to RM" + getRMName(RMAddress) + ". Message:" + msg);
			
			byte[] buffer = new byte[1000];
			reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			
			ifCrash = new String(reply.getData()).trim();
			RMPort.LOGGER.info("RM " + replicaID + " get reply from RM" + getRMName(RMAddress) + ". Message:" + ifCrash);
			
			if (ifCrash.equals("DidCrash")) {
				byte[] data2 = "RestartReplica".getBytes();
				DatagramPacket packet2 = new DatagramPacket(data2, data2.length, address, CrashRMPort);
				socket.send(packet2);
				RMPort.LOGGER.info("RM " + replicaID + " send message to RM" + getRMName(RMAddress) +  ". Message: RestartReplica");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void checkCrashReply(DatagramSocket socket, DatagramPacket pocket) throws IOException {
		String result = "NotCrash";
		try {
			if (replica1.isCrash() == true)
				result = "DidCrash";
			System.out.println(result);
		} catch (Exception e) {
			result = "DidCrash";
		}
		DatagramPacket reply = new DatagramPacket(result.getBytes(), result.getBytes().length, pocket.getAddress(),
				pocket.getPort());
		socket.send(reply);
		RMPort.LOGGER.info("RM " + replicaID + " check if its replica crash and send reply to RM " + getRMName(pocket.getAddress().toString()) + ". Message:" + result);
	}

	private void restartReplica() throws IOException {
		try {
			System.out.println("RM " + replicaID + " restart replica " + replicaID + "...");
			replica1.setHistoryQueue(this.historyQueue);
			replica1.recoverRplicaData();
			replica1.setCrash(false);
			RMPort.LOGGER.info("RM " + replicaID + " restart replica " + replicaID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void solveFailure(String failureMessage) {
		int failureRM = Integer.parseInt(failureMessage.split(":")[2]);
		System.out.println("RM " + replicaID + " send failure message to other RMs: " + failureMessage);
		if (failureRM != replicaID)
			sendFailureToRM(RMPort.RM_PORT.rmHost1, RMPort.RM_PORT.rmPort1, failureMessage);
		if (failureRM != replicaID)
			sendFailureToRM(RMPort.RM_PORT.rmHost2, RMPort.RM_PORT.rmPort2, failureMessage);
		if (failureRM != replicaID)
			sendFailureToRM(RMPort.RM_PORT.rmHost3, RMPort.RM_PORT.rmPort3, failureMessage);
		if (failureRM != replicaID)
			sendFailureToRM(RMPort.RM_PORT.rmHost4, RMPort.RM_PORT.rmPort4, failureMessage);
	}

	private void sendFailureToRM(String RMAddress, int FailureRMPort, String msg) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(RMAddress);
			byte[] data = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, FailureRMPort);
			socket.send(packet);
			RMPort.LOGGER.info("RM " + replicaID + " send message to RM" + getRMName(RMAddress) + ". Message:" + msg);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void moveToHoldBackQueue(String msg, DatagramSocket socket) throws IOException {
		int seqNum = Integer.parseInt(msg.split(":")[0]);
		if (!holdBackQueue.containsKey(seqNum)) {
			Message message = FormatMessage(msg);
			holdBackQueue.put(seqNum, message);
		}
		moveToDeliveryQueue(socket);
	}

	private void moveToDeliveryQueue(DatagramSocket socket) throws IOException {
		if (holdBackQueue.size() != 0) {
			if (holdBackQueue.containsKey(this.seqNum)) {
				Message message = holdBackQueue.get(this.seqNum);
				if (!this.deliveryQueue.contains(message)) {
					this.deliveryQueue.offer(message);
					this.holdBackQueue.remove(this.seqNum);
					this.seqNum++;
					checkAndSendRequest(socket);
					moveToDeliveryQueue(socket);
				}
			}
		}
	}

	private void checkAndSendRequest(DatagramSocket socket) throws IOException {
		Message message = this.deliveryQueue.peek();
		if (message != null) {
			message = this.deliveryQueue.poll();
			historyQueue.offer(message);
			sendToReplica(message);
			checkAndSendRequest(socket);
		}
	}

	private void sendToReplica(Message msg) throws IOException {
		String reply = "";
		if (replica1 != null) {
			if (replica1.isCrash() == true) {
				System.out.println("Replica " + replicaID + " crash.");
				return;
			}
			try {
				reply = msg.getSeqNum() + ":" + this.replicaID + ":" + replica1.sendRequest(msg);
				RMPort.LOGGER.info("RM " + replicaID + " send message to replica" + replicaID + " and get reply. Message:" + msg + " \nReply: " + reply);			
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("Replica reply: " + reply);
			DatagramSocket socket = null;
			socket = new DatagramSocket();
			sendToFE(socket, reply, msg.getFEAddr(), msg.getFEPort());
		}
	}
	
	private void sendToFE(DatagramSocket socket, String msgFromReplica, String msgAddr, String msgPort) {
		try {
			InetAddress address = InetAddress.getByName(msgAddr);
			byte[] data = msgFromReplica.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, Integer.parseInt(msgPort));
			socket.send(packet);
			RMPort.LOGGER.info("RM " + replicaID + " send reply to FE. Message:" + msgFromReplica);	
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

	public Message FormatMessage(String msg) {
		Message message = new Message();
		String[] msgs = msg.split(":");
		message.setSeqNum(Integer.parseInt(msgs[0]));
		String[] msg1 = msgs[1].split(",");
		message.setFEAddr(msg1[0]);
		message.setFEPort(msg1[1]);
		message.setOperationMsg(msgs[2]);
		return message;
	}

	public void CrashListener(int crashPort) {
		RMPort.LOGGER.info("RM " + replicaID + " crash listener open.");
		try {
			DatagramSocket socket = new DatagramSocket(crashPort);
			DatagramPacket pocket = null;
			byte[] buffer = null;
			while (true) {
				buffer = new byte[2000];
				pocket = new DatagramPacket(buffer, buffer.length);
				socket.receive(pocket);
				String message = new String(pocket.getData()).trim();
				RMPort.LOGGER.info("RM " + replicaID + " crash Listener receive: " + message);		
				System.out.println("RM" + replicaID + " crash Listener receive: " + message);
				String[] messages = message.split(":");
				switch (messages[0]) {
				case "IfCrash":
					checkCrashReply(socket, pocket);
					break;
				case "RestartReplica":
					crasNumber++;
					if (crasNumber >= CRASH_CONFIRM_NUM) {
						restartReplica();
						crasNumber = 0;
					}
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void RMListener(int rmPort) throws Exception {
		RMPort.LOGGER.info("RM " + replicaID + " listener open.");
		DatagramSocket socket = new DatagramSocket(rmPort);
		DatagramPacket packet = null;
		byte[] buffer = null;
		while (true) {
			buffer = new byte[2000];
			packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String message = new String(packet.getData()).trim();
			String[] message1 = message.split(":");
			System.out.println(message1[0] + "-------\nUDP receive: " + message);
			RMPort.LOGGER.info("RM " + replicaID + " receive message: " + message);
			switch (message1[0]) {
			case "Failure":// Failure:sequenceId:replicaId
				if (!failureQueue.contains(message1[1])) {
					failureQueue.offer(message1[1]);
					int rid = Integer.parseInt(message1[2]);
					int ifFixed = SetCounter(rid);
					if (ifFixed == 1) {
						System.out.println("Replica " + rid + " has been fixed.");
					}
					solveFailure(message);
				}
				break;
			case "Crash":
				int crashRM = Integer.parseInt(message.split(":")[2]);
				solveCrash(crashRM); // Crash:sequenceId:replicaId
				break;
			case "SetCrash": // SetCrash:replicaId
				if (message1[1].equals(replicaID + "")) {
					replica1.setCrash(true);
				}
				break;
			default://seqNum:Addr,port:replicaId:addEvent,...,...,
				moveToHoldBackQueue(message, socket);
				break;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		ReplicaManager1 rm = new ReplicaManager1();
		rm.initCounter();

		Runnable taskListener = () -> {
			try {
				rm.RMListener(RMPort.RM_PORT.rmPort1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		Thread t1 = new Thread(taskListener);
		t1.start();

		Runnable crashListener = () -> {
			try {
				rm.CrashListener(RMPort.RM_PORT.rmPort1_crash);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		Thread t2 = new Thread(crashListener);
		t2.start();
	}

}
