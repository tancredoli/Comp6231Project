package testNew;

import org.omg.CORBA.ORB;

public class MuiltiCustomer_add {
	public static void main(String[] args) {
		ORB orb = ORB.init(args, null);
		//Customer_Client(String customerId, String userChoice, String eventId, String eventType, String eventId_n, String eventType_n)		
		String idc1 = "MTLC1000";
		String idc2 = "OTWC1002";
		String idc3 = "TORC1001";
		// addEvent
		Customer_Client c1 = new Customer_Client(idc1, "1", "MTLA100519", "1", "MTLA100519", "1");
		Customer_Client c2 = new Customer_Client(idc1, "1", "MTLE100519", "1", "MTLA100519", "1");
		Customer_Client c3 = new Customer_Client(idc1, "1", "MTLM100519", "1", "MTLA100519", "1");
		
		Customer_Client c4 = new Customer_Client(idc2, "1", "OTWA100519", "1", "MTLA100519", "1");
		Customer_Client c5 = new Customer_Client(idc2, "1", "OTWE100519", "1", "MTLA100519", "1");
		Customer_Client c6 = new Customer_Client(idc2, "1", "OTWM100519", "1", "MTLA100519", "1");
		
		Customer_Client c7 = new Customer_Client(idc3, "1", "TORA100519", "1", "MTLA100519", "1");
		Customer_Client c8 = new Customer_Client(idc3, "1", "TORE100519", "1", "MTLA100519", "1");
		Customer_Client c9 = new Customer_Client(idc3, "1", "TORM100519", "1", "MTLA100519", "1");
		
		Customer_Client c11 = new Customer_Client(idc1, "1", "OTWA100519", "1", "MTLA100519", "1");
		Customer_Client c12 = new Customer_Client(idc1, "1", "TORA100519", "1", "MTLA100519", "1");
		Customer_Client c13 = new Customer_Client(idc1, "1", "TORE100519", "1", "MTLA100519", "1");
		
		Customer_Client c14 = new Customer_Client(idc2, "1", "MTLA100519", "1", "MTLA100519", "1");
		Customer_Client c15 = new Customer_Client(idc2, "1", "TORA100519", "1", "MTLA100519", "1");
//		Customer_Client c16 = new Customer_Client(idc2, "1", "OTWM100619", "1", "MTLA100519", "1");
		
		Customer_Client c17 = new Customer_Client(idc3, "1", "MTLA100519", "1", "MTLA100519", "1");
		Customer_Client c18 = new Customer_Client(idc3, "1", "OTWM100619", "1", "MTLA100519", "1");
//		Customer_Client c19 = new Customer_Client(idc3, "1", "MTLM100519", "1", "MTLA100519", "1");

		c1.setORB(orb);		
		c2.setORB(orb);		
		c3.setORB(orb);		
		c4.setORB(orb);		
		c5.setORB(orb);		
		c6.setORB(orb);		
		c7.setORB(orb);		
		c8.setORB(orb);		
		c9.setORB(orb);		
		c11.setORB(orb);		
		c12.setORB(orb);		
//		c13.setORB(orb);		
		c14.setORB(orb);
		c15.setORB(orb);		
//		c16.setORB(orb);		
		c17.setORB(orb);		
		c18.setORB(orb);		
//		c19.setORB(orb);	
		
		Thread t1 = new Thread(c1);
		Thread t2 = new Thread(c2);
		Thread t3 = new Thread(c3);
		Thread t4 = new Thread(c4);
		Thread t5 = new Thread(c5);
		Thread t6 = new Thread(c6);
		Thread t7 = new Thread(c7);
		Thread t8 = new Thread(c8);
		Thread t9 = new Thread(c9);
		
		Thread t11 = new Thread(c11);
		Thread t12 = new Thread(c12);
//		Thread t13 = new Thread(c13);
		Thread t14 = new Thread(c14);
		Thread t15 = new Thread(c15);
//		Thread t16 = new Thread(c16);
		Thread t17 = new Thread(c17);
		Thread t18 = new Thread(c18);
//		Thread t19 = new Thread(c19);


		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		t7.start();
		t8.start();
		t9.start();
		t11.start();
		t12.start();
//		t13.start();
		t14.start();
		t15.start();
//		t16.start();
		t17.start();
		t18.start();
//		t19.start();	
	}
}
