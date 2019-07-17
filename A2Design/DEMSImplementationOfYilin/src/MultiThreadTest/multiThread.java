package MultiThreadTest;
import org.omg.CORBA.ORB;

public class multiThread {
	public static void main(String[] args) {
		ORB orb = ORB.init(args, null);
		
		System.out.println("----MutilThread Test----");
		System.out.println("--Managers Login and add events--");
		Thread t1 = new Thread(new multiManager("TORM1111","TORA080819","conference",2,orb));
		Thread t2 = new Thread(new multiManager("TORM2222","TORA080819","seminar",2,orb));
		
		Thread t3 = new Thread(new multiManager("TORM3333","TORE010819","tradeshow",20,orb));
		Thread t4 = new Thread(new multiManager("TORM4444","TORE010819","conference",1,orb));
		Thread t5 = new Thread(new multiManager("TORM5555","TORE010819","seminar",1,orb));
		try {
			t1.sleep(50);
			t2.sleep(50);
			t3.sleep(50);
			t4.sleep(50);
			t5.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		try {
			t1.join();
			t2.join();
			t3.join();
			t4.join();
			t5.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("--Customers Login, book events and swap events--");
		System.out.println("--Case1--");
		//"TORA080819","conference",capacity=2
		//"TORA080819","seminar",capacity=2
		//The swap results are different every time, depend on the order of threads operation
		Thread t6 = new Thread(new multiCustomer("TORC0006","TORA080819","conference","TORA080819","seminar",orb));
		Thread t7 = new Thread(new multiCustomer("TORC0007","TORA080819","conference","TORA080819","seminar",orb));
		Thread t8 = new Thread(new multiCustomer("TORC0008","TORA080819","seminar","TORA080819","conference",orb));
		try {
			t6.sleep(50);
			t7.sleep(50);
			t8.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t6.start();
		t7.start();
		t8.start();
		try {
			t6.join();
			t7.join();
			t8.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("--Case2--");
		//"TORE010819","conference",capacity=1
		//only one customer can swap successfully
		Thread t9 = new Thread(new multiCustomer("TORC0009","TORE010819","tradeshow","TORE010819","conference",orb));
		Thread t10 = new Thread(new multiCustomer("TORC0010","TORE010819","tradeshow","TORE010819","conference",orb));
		try {
			t9.sleep(50);
			t10.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t9.start();
		t10.start();
		try {
			t9.join();
			t10.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("--Case3--");
		//"TORE010819","seminar",capacity=1
		//The results depend on the order of threads operation
		//Maybe one of t12/13 swaps successfully when t11 finishes swap firstly
		//Maybe both of t12/13 swap failed when t11 did not finish swap
		Thread t11 = new Thread(new multiCustomer("TORC0011","TORE010819","seminar","TORE010819","tradeshow",orb));
		Thread t12 = new Thread(new multiCustomer("TORC0012","TORE010819","tradeshow","TORE010819","seminar",orb));
		Thread t13 = new Thread(new multiCustomer("TORC0013","TORE010819","tradeshow","TORE010819","seminar",orb));
		try {
			t11.sleep(50);
			t12.sleep(50);
			t13.sleep(50);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t11.start();
		t12.start();
		t13.start();
		
		
	}
}
