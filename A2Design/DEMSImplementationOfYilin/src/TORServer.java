import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import CorbaApp.DEMSInterf;
import CorbaApp.DEMSInterfHelper;

public class TORServer {
	
     public static void main(String[] args) throws Exception{
	    String portNum = " ", registryURL = " ";
	    int udpportnum = 0;
	    String p;
	    
	    try{     
	    	// create and initialize the ORB //
	    	ORB orb = ORB.init(args, null);
	    	
	    	// get reference to rootpoa &amp; activate
	    	POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
	    	rootpoa.the_POAManager().activate();
	    	
	    	// create servant and register it with the ORB
	    	DEMSImpl demsimpl = new DEMSImpl();
	        demsimpl.setORB(orb);
	        
	        // get object reference from the servant
	        org.omg.CORBA.Object ref = rootpoa.servant_to_reference(demsimpl);
	        
	        // and cast the reference to a CORBA reference
	        DEMSInterf href = DEMSInterfHelper.narrow(ref);
	        
	        // get the root naming context
	        // NameService invokes the transient name service
	     	org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
	     	
	        // Use NamingContextExt, which is part of the
	        // Interoperable Naming Service (INS) specification.
	     	NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
	     	
	        // bind the Object Reference in Naming
	     	NameComponent path[] = ncRef.to_name( "TOR" );
	     	ncRef.rebind(path, href);
	    	
	    	p= "TOR";
	    	portNum = "1111";
	    	udpportnum = 1112;
	    	
//	        int RMIPortNum = Integer.parseInt(portNum);
//	        startRegistry(RMIPortNum);
//	        DEMSImpl obj = new DEMSImpl();
//	        registryURL = "rmi://localhost:" + portNum + "/DEMS-" + p;
//	        Naming.rebind(registryURL, obj);
//	        System.out.println ("TOR Server registered.  Registry currently contains:");
//	        listRegistry(registryURL); 
//	        System.out.println("TOR Server ready. ");
	        
	    	DatagramSocket serversocket = new DatagramSocket(udpportnum);
	        startlistening(p, demsimpl, udpportnum, serversocket);

	        demsimpl.StartServer(p);
	         
	     	System.out.println("TOR Server ready and waiting ...");
	     	
	     	orb.run();
	         
	    }	      
	    catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
}
	     
	
	
	private static void startlistening(String City, DEMSImpl cityServer, int UDPlistenPort, DatagramSocket SeverSocket) throws Exception {
		
		String threadName = City + "listen";
		Listening listen = new Listening(threadName, SeverSocket, cityServer);
		listen.start();
	}
	
//	private static void startRegistry(int RMIPortNum) throws RemoteException {
//		
//		try {
//			Registry registry = LocateRegistry.getRegistry(RMIPortNum);
//		    registry.list( ); 
//		} catch (RemoteException e) { 		
//		    Registry registry = LocateRegistry.createRegistry(RMIPortNum);
//		    System.out.println("RMI registry created at port " + RMIPortNum);
//		}
//	}
//		 
//	private static void listRegistry(String registryURL) throws RemoteException, MalformedURLException {
//		
//		String [ ] names = Naming.list(registryURL);
//		for (int i=0; i < names.length; i++)
//			System.out.println(names[i]);
//	}
}


	