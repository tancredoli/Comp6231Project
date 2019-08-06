package FrontEnd;

import java.util.Scanner;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import FEInterface.CommonInterface;
import FEInterface.CommonInterfaceHelper;

public class FEServer {

    public static void main(String[] args) {

	try {
	    ORB orb = ORB.init(args, null);

	    POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
	    rootpoa.the_POAManager().activate();

	    FEImplementation feObj = new FEImplementation();
	    feObj.setOrb(orb);

	    org.omg.CORBA.Object ref = rootpoa.servant_to_reference(feObj);

	    CommonInterface href = CommonInterfaceHelper.narrow(ref);

	    org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

	    NameComponent path[] = ncRef.to_name("frontend");
	    ncRef.rebind(path, href);

	    System.out.println("FrontEndServer is Started..........");

	    Runnable runnable = new Runnable() {

		@Override
		public void run() {
		    // TODO Auto-generated method stub
		    Scanner scanner = new Scanner(System.in);

		    String rString = scanner.nextLine();
		    if (rString.isEmpty()) {
			feObj.sendCrash(1);
		    }

		}
	    };
	    for (;;) {
		new Thread(runnable).start();
		orb.run();

	    }

	} catch (Exception e) {
	    System.err.println("ERROR: " + e);
	    e.printStackTrace(System.out);
	}

	System.out.println("FrontEndServer Exiting ...");
    }

}
