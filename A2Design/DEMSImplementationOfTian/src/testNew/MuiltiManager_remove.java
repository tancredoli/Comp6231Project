package testNew;

import org.omg.CORBA.ORB;

public class MuiltiManager_remove {
	public static void main(String[] args) {

		ORB orb = ORB.init(args, null);
		String idm1 = "MTLM1001";
		String idm2 = "OTWM1001";
		String idm3 = "TORM1001";
//Manager_Client(String managerId, String userChoice, String eventType, String eventId, String capacity, String eventType_n, String eventId_n)
			
		// addEvent
		Manager_Client man1 = new Manager_Client(idm1, "2", "1", "MTLA100519", "15", "1", "MTLA100519");
		Manager_Client man2 = new Manager_Client(idm1, "2", "1", "MTLE100519", "2", "1", "MTLA100519");
		Manager_Client man3 = new Manager_Client(idm1, "2", "1", "MTLM100519", "1", "1", "MTLA100519");
		
		Manager_Client man4 = new Manager_Client(idm2, "2", "1", "OTWA100519", "15", "1", "MTLA100519");
		Manager_Client man5 = new Manager_Client(idm2, "2", "1", "OTWE100519", "2", "1", "MTLA100519");
		Manager_Client man6 = new Manager_Client(idm2, "2", "1", "OTWM100519", "2", "1", "MTLA100519");
		
		Manager_Client man7 = new Manager_Client(idm3, "2", "1", "TORA100519", "2", "1", "MTLA100519");
		Manager_Client man8 = new Manager_Client(idm3, "2", "1", "TORE100519", "2", "1", "MTLA100519");
		Manager_Client man9 = new Manager_Client(idm3, "2", "1", "TORM100519", "2", "1", "MTLA100519");
		
		Manager_Client man11 = new Manager_Client(idm1, "2", "1", "MTLA100619", "15", "1", "MTLA100519");
		Manager_Client man12 = new Manager_Client(idm1, "2", "1", "MTLE100619", "2", "1", "MTLA100519");
		Manager_Client man13 = new Manager_Client(idm1, "2", "1", "MTLM100619", "3", "1", "MTLA100519");
		
		Manager_Client man14 = new Manager_Client(idm2, "2", "1", "OTWA100619", "15", "1", "MTLA100519");
		Manager_Client man15 = new Manager_Client(idm2, "2", "1", "OTWE100619", "2", "1", "MTLA100519");
		Manager_Client man16 = new Manager_Client(idm2, "2", "1", "OTWM100619", "2", "1", "MTLA100519");
		
		Manager_Client man17 = new Manager_Client(idm3, "2", "1", "TORA100619", "2", "1", "MTLA100519");
		Manager_Client man18 = new Manager_Client(idm3, "2", "1", "TORE100619", "2", "1", "MTLA100519");
		Manager_Client man19 = new Manager_Client(idm3, "2", "1", "TORM100619", "2", "1", "MTLA100519");

		man1.setORB(orb);		
		man2.setORB(orb);		
		man3.setORB(orb);		
		man4.setORB(orb);		
		man5.setORB(orb);		
		man6.setORB(orb);		
		man7.setORB(orb);		
		man8.setORB(orb);		
		man9.setORB(orb);		
		man11.setORB(orb);		
		man12.setORB(orb);		
		man13.setORB(orb);		
		man14.setORB(orb);
		man15.setORB(orb);		
		man16.setORB(orb);		
		man17.setORB(orb);		
		man18.setORB(orb);		
		man19.setORB(orb);	
		
		Thread t1 = new Thread(man1);
		Thread t2 = new Thread(man2);
		Thread t3 = new Thread(man3);
		Thread t4 = new Thread(man4);
		Thread t5 = new Thread(man5);
		Thread t6 = new Thread(man6);
		Thread t7 = new Thread(man7);
		Thread t8 = new Thread(man8);
		Thread t9 = new Thread(man9);
		
		Thread t11 = new Thread(man11);
		Thread t12 = new Thread(man12);
		Thread t13 = new Thread(man13);
		Thread t14 = new Thread(man14);
		Thread t15 = new Thread(man15);
		Thread t16 = new Thread(man16);
		Thread t17 = new Thread(man17);
		Thread t18 = new Thread(man18);
		Thread t19 = new Thread(man19);


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
		t13.start();
		t14.start();
		t15.start();
		t16.start();
		t17.start();
		t18.start();
		t19.start();	
	}
}
