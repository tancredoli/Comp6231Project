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

public class ReplicaManager1 {
	Logger logger;
	int replicaID;
	int seqNum;
	Replica1 replica1;
	HashMap<Integer, Message> holdBackQueue;
	HashMap<Integer, Integer> counter;
	Queue<Message> deliveryQueue;
	Queue<String> failureQueue;
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
		System.out.println("Replica " + replicaId + " has failed " + failureTime + " times");
		if (counter.get(replicaId) >= 3) {
			// logger.info(replicaId + " has been failed 3 times.");
			counter.put(replicaId, 0);
			i = 1;
		}
		return i;
	}

	public void solveCrash(int crashRM) {
		if (crashRM == replicaID) {
			System.out.println("This Replica crash!");
			// this.logger.info("Crash: Replica" + replicaID);
		} else {
			String crashInfo = "IfCrash";
			System.out.println("reply:" + crashInfo);

			switch (crashRM) {// check if replica1 is alive
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

	public void solveFailure(String failureMessage) {
		int failureRM = Integer.parseInt(failureMessage.split(":")[2]);
		if (failureRM != replicaID) 
			sendFailureToRM(RMPort.RM_PORT.rmHost1, RMPort.RM_PORT.rmPort1, failureMessage);
		if (failureRM != replicaID) 
			sendFailureToRM(RMPort.RM_PORT.rmHost2, RMPort.RM_PORT.rmPort2, failureMessage);
		if (failureRM != replicaID) 
			sendFailureToRM(RMPort.RM_PORT.rmHost3, RMPort.RM_PORT.rmPort3, failureMessage);
		if (failureRM != replicaID) 
			sendFailureToRM(RMPort.RM_PORT.rmHost4, RMPort.RM_PORT.rmPort4, failureMessage);
		
	}

	private void restartReplica() throws IOException {
		try {
			replica1.historyQueue = this.historyQueue;
			replica1.recoverRplicaData();
			replica1.crash = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// logger.info("restart and recover replica1.");
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
			Message message = FormatMessage(msg);
			holdBackQueue.put(id, message);
		}
		moveToDeliveryQueue(aSocket);
	}

	private void sendToFE(DatagramSocket socket, String msgFromReplica, String msgAddr, String msgPort) {
		DatagramPacket reply = null;
		boolean revResponse = false;
		while (!revResponse) {
			try {
				// aSocket.setSoTimeout(TIMEOUT);
				InetAddress address = InetAddress.getByName(msgAddr);
				// InetAddress address = InetAddress.getByName("132.205.95.183");// li
				byte[] data = msgFromReplica.getBytes();
				DatagramPacket packet = new DatagramPacket(data, data.length, address, Integer.parseInt(msgPort));
				socket.send(packet);

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
				if (socket != null) {
					socket.close();
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
				reply = msg.seqId + ":" + this.replicaID + ":" + replica1.sendRequest(msg);
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

	public Message FormatMessage(String msg) {
		Message msg1 = new Message();
		String[] msg2 = msg.split(":");
		String[] msg3 = msg2[1].split(",");
		msg1.seqId = Integer.parseInt(msg2[0]);
		msg1.FEAddr = msg3[0];
		msg1.FEPort = msg3[1];
		msg1.operationMsg = msg2[2];
		return msg1;
	}

	private void sendCrashToRM(String RMAddress, int CrashRMPort, String msg) {
		DatagramPacket reply = null;
		DatagramSocket socket = null;
		String crashConfirm = "";
		try {
			socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(RMAddress);
			byte[] data = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, CrashRMPort);

			socket.send(packet);

			byte[] buffer = new byte[1000];
			reply = new DatagramPacket(buffer, buffer.length);
			// logger.info("RM" + replicaID + " sends crush message to RM:" + crashMsg);

			socket.receive(reply);
			crashConfirm = new String(reply.getData()).trim();
			// logger.info("crashConfirm: " + crashConfirm);

			if (crashConfirm.equals("DidCrash")) {
				byte[] data2 = "RestartReplica".getBytes();
				DatagramPacket restartPacket = new DatagramPacket(data2, data2.length, address, CrashRMPort);
				socket.send(restartPacket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendFailureToRM(String RMAddress, int FailureRMPort, String msg) {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket();
			InetAddress address = InetAddress.getByName(RMAddress);
			byte[] data = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(data, data.length, address, FailureRMPort);
			socket.send(packet);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startCrashListener(int crashPort) {
		try {
			DatagramSocket socket = new DatagramSocket(crashPort);
			DatagramPacket pocket = null;
			byte[] buffer = null;
			// logger.info("RM crash is listenning ");
			System.out.println("RM crash is listenning ");
			while (true) {
				buffer = new byte[2000];
				pocket = new DatagramPacket(buffer, buffer.length);
				socket.receive(pocket);

				String message = new String(pocket.getData()).trim();
				System.out.println("===UDP RM1 crash receive :==== " + message);
				String[] messageSplited = message.split(":");
				System.out.println("messageSplited[0]--" + messageSplited[0]);

				switch (messageSplited[0]) {
				case "IfCrash": // other rms send udp msg to ask if rm1 crash
					replyCrashChecking(socket, pocket);
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

	public void replyCrashChecking(DatagramSocket socket, DatagramPacket pocket) throws IOException {
		String result = "Alive";
		try {
			// boolean isAlive = replica1.monServer.aSocket.isConnected();
			if(replica1.crash == true)
				result = "DidCrash";
			System.out.println(result);
		} catch (Exception e) {
			result = "DidCrash";
		}
		DatagramPacket replyP = new DatagramPacket(result.getBytes(), result.getBytes().length, pocket.getAddress(),
				pocket.getPort());
		socket.send(replyP);
	}

	public void RMListener(int RMPort) throws Exception {
		DatagramSocket socket = new DatagramSocket(RMPort);
		DatagramPacket packet = null;
		byte[] buffer = null;
		// logger.info("RM is listening ");
		while (true) {
			buffer = new byte[2000];
			packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);

			String message = new String(packet.getData()).trim();
			// socket.send(packet);// acknowledge
			System.out.println("UDP receive : " + message);
			// logger.info("RM1 receives message:" + message);

			String[] message1 = message.split(":");
			System.out.println("message[0] ==> " + message1[0]);

			switch (message1[0]) {
			case "Failure":
				if(!failureQueue.contains(message1[1])) {
					failureQueue.offer(message1[1]);
					int rid = Integer.parseInt(message1[2]);
					int ifFixed = SetCounter(rid);
					if(ifFixed == 1) {
						System.out.println("Replica " + rid + " has been fixed.");					
					}
					solveFailure(message);
				}	
				break;
			case "Crash":
				int crashRM = Integer.parseInt(message.split(":")[1]);
				solveCrash(crashRM); // Crash:replicaId
				break;
			case "SetCrash": // SetCrash:replicaId
				if (message1[1].equals(replicaID + "")) {
					replica1.crash = true;
				}
				break;
			default:
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
				rm.startCrashListener(RMPort.RM_PORT.rmPort1_crash);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};

		Thread t2 = new Thread(crashListener);
		t2.start();


	}

}
