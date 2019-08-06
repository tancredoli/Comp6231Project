package Sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import Utility.Constants;
import Utility.LogRecorder;
import Utility.Port;

public class Sequencer {
    private int sNum;
    private LogRecorder seLoger;

    public Sequencer() {
	this.sNum = 0;
	try {
	    seLoger = new LogRecorder(6);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	Sequencer sq = new Sequencer();
	sq.listenFromFE();
    }

    private String formatPacket(String requestFromFE) {
	StringBuilder sb = new StringBuilder();
	sb.append(sNum).append(Constants.separator_colon).append(Port.PORT_NUM.FRONTEND_IP)
		.append(Constants.separator_comma).append(Port.PORT_NUM.FRONTEND_PORT).append(Constants.separator_colon)
		.append(requestFromFE);
	String requestToSend = sb.toString();
	String log5 = "Formated msg: " + requestToSend;
	System.out.println(requestToSend);
	try {
	    seLoger.writeSeqLog(log5);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	sNum++;
	return requestToSend;
    }

    private void listenFromFE() {
	DatagramSocket socket = null;
	try {
	    socket = new DatagramSocket(Port.PORT_NUM.SEQUENCER_PORT);
	    byte[] buffer = null;
	    while (true) {
		buffer = new byte[1000];
		DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
		socket.receive(receivePacket);
		String requestFromFE = new String(receivePacket.getData()).trim();
		String msg = formatPacket(requestFromFE);
		multiCast(msg);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (socket != null) {
		socket.close();
	    }
	}
    }

    private void multiCast(String msg) {
	DatagramSocket aSocket = null;
	try {
	    aSocket = new DatagramSocket();
	    InetAddress address1 = InetAddress.getByName(Port.PORT_NUM.rmHost1);
	    InetAddress address2 = InetAddress.getByName(Port.PORT_NUM.rmHost2);
	    InetAddress address3 = InetAddress.getByName(Port.PORT_NUM.rmHost3);
	    InetAddress address4 = InetAddress.getByName(Port.PORT_NUM.rmHost4);
	    DatagramPacket packet1 = new DatagramPacket(msg.getBytes(), msg.length(), address1, Port.PORT_NUM.rmPort1);
	    DatagramPacket packet2 = new DatagramPacket(msg.getBytes(), msg.length(), address2, Port.PORT_NUM.rmPort2);
	    DatagramPacket packet3 = new DatagramPacket(msg.getBytes(), msg.length(), address3, Port.PORT_NUM.rmPort3);
	    DatagramPacket packet4 = new DatagramPacket(msg.getBytes(), msg.length(), address4, Port.PORT_NUM.rmPort4);
	    aSocket.send(packet1);
	    aSocket.send(packet2);
	    aSocket.send(packet3);
	    aSocket.send(packet4);
	    String log1 = "Request: " + msg + " has been sent to rp1!";
	    String log2 = "Request: " + msg + " has been sent to rp2!";
	    String log3 = "Request: " + msg + " has been sent to rp3!";
	    String log4 = "Request: " + msg + " has been sent to rp4!";
	    System.out.println(log1);
	    seLoger.writeSeqLog(log1);
	    System.out.println(log2);
	    seLoger.writeSeqLog(log2);
	    System.out.println(log3);
	    seLoger.writeSeqLog(log3);
	    System.out.println(log4);
	    seLoger.writeSeqLog(log4);
	} catch (UnknownHostException e) {
	    e.printStackTrace();
	    e.printStackTrace();
	} catch (SocketException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    if (aSocket != null) {
		aSocket.close();
	    }
	}
    }
}
