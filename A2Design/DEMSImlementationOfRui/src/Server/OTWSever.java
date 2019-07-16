package Server;

import ImplementInterface.OTWImplementationClass;
import ServerInterface.CommonInterface;
import ServerInterface.CommonInterfaceHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @Author: Rui
 * @Description:
 * @Date: Created in 4:58 PM 2019-07-06
 * @Modified by:
 */
public class OTWSever {
    private static final int fNumberOfThreads = 100;
    private static final Executor fThreadPool =
            Executors.newFixedThreadPool(fNumberOfThreads);

    private static void handleRequest(OTWImplementationClass obj) {
        DatagramSocket aSocket = null;
        try {
            String result = "";
            aSocket = new DatagramSocket(8102);
            byte[] buffer = new byte[1000];// to stored the received data from
            // the client.
            System.out.println("OTWUdpServer Started............");
            while (true) {// non-terminating loop as the server is always
                // in listening mode.
                DatagramPacket request = new DatagramPacket(buffer,
                        buffer.length);

                // Server waits for the request to come
                aSocket.receive(request);// request received

                String[] rs = new String(request.getData()).split("-");
                int requestType = Integer.parseInt(rs[0]);
                String userID = rs[1];
                int eventType = Integer.parseInt(rs[2]);
                String eventID = rs[3].trim();
                if (requestType == 0) {
                    if (eventType == 1) {
                        result = obj.eventListHelper(obj.getConList());
                    } else if (eventType == 2) {
                        result = obj.eventListHelper(obj.getSemList());
                    } else if (eventType == 3) {
                        result = obj.eventListHelper(obj.getTsList());
                    }

                }
                if (requestType == 1) {
                    result = obj.getBookingScheduleHelper(userID);
                }
                if (requestType == 2) {
                    result = String.valueOf(obj.bookEventHelper(userID,
                            eventID, eventType));
                }
                if (requestType == 3) {
                    result = String.valueOf(obj.cancelEventHelper(userID,
                            eventID, eventType));
                }
                byte[] byteResult = result.getBytes();
                DatagramPacket reply = new DatagramPacket(byteResult
                        , result.length(), request.getAddress(),
                        request.getPort());// reply packet ready

                aSocket.send(reply);// reply sent
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

    public static void main(String[] args) {
        try {
            // create and initialize the ORB //
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa &amp; activate
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references(
                    "RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            OTWImplementationClass otwObj = new OTWImplementationClass();
            otwObj.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(otwObj);


            // and cast the reference to a CORBA reference
            CommonInterface href = CommonInterfaceHelper.narrow(ref);

            // get the root naming context
            // NameService invokes the transient name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references(
                    "NameService");

            // Use NamingContextExt, which is part of the
            // Interoperable Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            NameComponent path[] = ncRef.to_name("otw");
            ncRef.rebind(path, href);

            System.out.println("OTWServer is Started..........");

            Runnable task = () -> {
                handleRequest(otwObj);
            };

            fThreadPool.execute(task);


            // wait for invocations from clients
            for (; ; ) {
                orb.run();
            }


        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("OTWServer Exiting ...");
    }
}
