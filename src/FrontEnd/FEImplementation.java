package FrontEnd;

import FEInterface.CommonInterfacePOA;
import Utility.Port;
import org.omg.CORBA.ORB;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * @Author: Rui
 * @Description:
 * @Date: Created in 4:03 PM 2019-07-28
 * @Modified by:
 */
public class FEImplementation extends CommonInterfacePOA {

    private static final String isTrue = "successfully";
    private static final String separator = ":";
    private ORB orb;

    public void setOrb(ORB orb) {
        this.orb = orb;
    }

    @Override
    public boolean addEvent(
            String userID, String eventID, int eventType,
            int bookingCapacity) {
        StringBuilder sb = new StringBuilder();
        sb.append("addEvent")
                .append(separator)
                .append(userID)
                .append(separator)
                .append(eventID)
                .append(separator)
                .append(eventType)
                .append(separator)
                .append(bookingCapacity);
        String rs = sendRequest(Port.SEQUENCER_PORT, sb.toString());
        if (rs.contains(isTrue))
            return true;
        else
            return false;

    }

    @Override
    public boolean removeEvent(String userID, String eventID, int eventType) {
        StringBuilder sb = new StringBuilder();
        sb.append("removeEvent")
                .append(separator)
                .append(userID)
                .append(separator)
                .append(eventID)
                .append(separator)
                .append(eventType);
        String rs = sendRequest(Port.SEQUENCER_PORT, sb.toString());
        if (rs.contains(isTrue))
            return true;
        else
            return false;
    }

    @Override
    public String listEventAvailability(String userID, int eventType) {
        StringBuilder sb = new StringBuilder();
        sb.append("listEventAvailability")
                .append(separator)
                .append(userID)
                .append(separator)
                .append(eventType);
        String rs = sendRequest(Port.SEQUENCER_PORT, sb.toString());
        return rs;
    }

    @Override
    public int bookEvent(String customerID, String eventID, int eventType) {
        return 0;
    }

    @Override
    public String getBookingSchedule(String customerID) {
        return null;
    }

    @Override
    public int cancelEvent(String customerID, String eventID, int eventType) {
        return 0;
    }

    @Override
    public int swapEvent(
            String customerID, String newEventID, int newEventType,
            String oldEventID, int oldEventType) {
        return 0;
    }


    private String sendRequest(
            int sequencerPort, String requestStr) {
        String rs = "";
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(); // reference of the original
            // socket
            byte[] message = requestStr.getBytes(); // message to be passed
            // is stored in byte array

            InetAddress aHost = InetAddress.getByName("localhost"); // Host
            // name is specified and the IP address of server host is
            // calculated using DNS.

            DatagramPacket request = new DatagramPacket(message,
                    requestStr.length(),
                    aHost,
                    sequencerPort); //
            // request
            // packet ready
            aSocket.send(request); // request sent out
            System.out.println(
                    "Request message sent from the client is : " + new String(
                            request.getData()));

            byte[] buffer = new byte[1000]; // to store the received data, it
            // will be populated by what receive method returns
            DatagramPacket reply = new DatagramPacket(buffer,
                    buffer.length); // reply
            // packet ready but not populated.

            // Client waits until the reply is
            // received
            // -----------------------------------------------------------------------
            aSocket.receive(reply); // reply received and will populate reply
            // packet now.
            rs = new String(reply.getData()); // print reply message
            // after
            // converting it to a string from bytes
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close(); // now all resources used by the socket are
            // returned to the OS, so that there is no
            // resource leakage, therefore, close the socket after it's use
            // is completed to release resources.
        }
        return rs.trim();
    }

}
