package testNew;
import org.omg.CORBA.ORB;

public class MuiltiCustomer_swap {
	public static void main(String[] args) {
		ORB orb = ORB.init(args, null);
		String idc1 = "MTLC1000";
		String idc2 = "OTWC1002";
		String idc3 = "TORC1001";
		// book
		Customer_Client c1 = new Customer_Client(idc1, "1", "MTLA100519", "1", "MTLE100519", "1");
		Customer_Client c2 = new Customer_Client(idc1, "1", "MTLM100519", "1", "MTLE100519", "1");
		Customer_Client c3 = new Customer_Client(idc2, "1", "OTWA100519", "1", "MTLE100519", "1");
		Customer_Client c4 = new Customer_Client(idc2, "1", "OTWM100519", "1", "MTLE100519", "1");
		Customer_Client c5 = new Customer_Client(idc3, "1", "TORA100519", "1", "MTLE100519", "1");
		Customer_Client c6 = new Customer_Client(idc3, "1", "OTWM100519", "1", "MTLE100519", "1");
		
		// swap event which new event capacity is 2
		Customer_Client c7 = new Customer_Client(idc1, "4", "MTLA100519", "1", "MTLE100519", "1");
		Customer_Client c8 = new Customer_Client(idc2, "4", "OTWA100519", "1", "MTLE100519", "1");
		Customer_Client c9 = new Customer_Client(idc3, "4", "TORA100519", "1", "MTLE100519", "1");
		
		// swap event which the new event is cancelled from another customer by using swapEvent method 
		Customer_Client c10 = new Customer_Client(idc1, "4", "MTLM100519", "1", "OTWE100519", "1");
		Customer_Client c11 = new Customer_Client(idc2, "4", "OTWM100519", "1", "MTLM100519", "1");
		Customer_Client c12 = new Customer_Client(idc3, "4", "OTWM100519", "1", "MTLM100519", "1");

		c1.setORB(orb);	c2.setORB(orb); c3.setORB(orb); c4.setORB(orb);  c5.setORB(orb);  c6.setORB(orb);
		c7.setORB(orb); c8.setORB(orb); c9.setORB(orb); c10.setORB(orb); c11.setORB(orb); c12.setORB(orb);

		Thread t1 = new Thread(c1);  Thread t2 = new Thread(c2);   Thread t3 = new Thread(c3);
		Thread t4 = new Thread(c4);   Thread t5 = new Thread(c5);   Thread t6 = new Thread(c6);
		Thread t7 = new Thread(c7);   Thread t8 = new Thread(c8);   Thread t9 = new Thread(c9);
		Thread t10 = new Thread(c10);  Thread t11 = new Thread(c11); Thread t12 = new Thread(c12);

		// book
//		t1.start(); t2.start(); t3.start(); t4.start(); t5.start(); t6.start();
		// swap 
//		t7.start();  t8.start();  t9.start();
		t10.start(); t11.start(); t12.start();

	}
}
