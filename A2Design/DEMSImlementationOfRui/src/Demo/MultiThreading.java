package Demo;

import ServerInterface.CommonInterface;
import ServerInterface.CommonInterfaceHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

/**
 * @Author: Rui
 * @Description:
 * @Date: Created in 3:52 PM 2019-07-08
 * @Modified by:
 */
public class MultiThreading {

    public static void main(String[] args) {

        ORB orb = ORB.init(args, null);
        //-ORBInitialPort 1050 -ORBInitialHost localhost
        org.omg.CORBA.Object objRef = null;
        try {
            objRef = orb.resolve_initial_references(
                    "NameService");
            NamingContextExt ncRef =
                    NamingContextExtHelper.narrow(objRef);
            CommonInterface mtlObj =
                    (CommonInterface) CommonInterfaceHelper.narrow(ncRef.resolve_str(
                            "mtl"));
            CommonInterface otwObj =
                    (CommonInterface) CommonInterfaceHelper.narrow(ncRef.resolve_str(
                            "otw"));
            CommonInterface torObj =
                    (CommonInterface) CommonInterfaceHelper.narrow(ncRef.resolve_str(
                            "tor"));
            mtlObj.addEvent("MTLM1001", "MTLM220620", 1, 1);
            mtlObj.addEvent("MTLM1001", "MTLM230620", 1, 3);
            otwObj.bookEvent("OTWC1001", "MTLM230620", 1);
            otwObj.bookEvent("OTWC1002", "MTLM230620", 1);
            otwObj.bookEvent("OTWC1003", "MTLM230620", 1);

            System.out.println("OTWC1001 : " + otwObj.getBookingSchedule(
                    "OTWC1001"));
            System.out.println("OTWC1002 : " + otwObj.getBookingSchedule(
                    "OTWC1002"));
            System.out.println("OTWC1003 : " + otwObj.getBookingSchedule(
                    "OTWC1003"));
            new Thread(new SwapRun(otwObj, "OTWC1001", "MTLM220620", 1,
                    "MTLM230620", 1)).start();
            new Thread(new SwapRun(otwObj, "OTWC1002", "MTLM220620", 1,
                    "MTLM230620", 1)).start();
            new Thread(new SwapRun(otwObj, "OTWC1003", "MTLM220620", 1,
                    "MTLM230620", 1)).start();
            Thread.sleep(1000);
            System.out.println("OTWC1001 : " + otwObj.getBookingSchedule(
                    "OTWC1001"));
            System.out.println("OTWC1002 : " + otwObj.getBookingSchedule(
                    "OTWC1002"));
            System.out.println("OTWC1003 : " + otwObj.getBookingSchedule(
                    "OTWC1003"));
        } catch (InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (CannotProceed cannotProceed) {
            cannotProceed.printStackTrace();
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static class SwapRun implements Runnable {

        String customerID;
        String newEventID;
        String oldEventID;
        int newEventType;
        int oldEventType;
        CommonInterface obj;

        public SwapRun(
                CommonInterface obj,
                String customerID, String newEventID,
                int newEventType,
                String oldEventID, int oldEventType) {

            this.customerID = customerID;
            this.newEventID = newEventID;
            this.newEventType = newEventType;
            this.oldEventID = oldEventID;
            this.oldEventType = oldEventType;
            this.obj = obj;
        }


        @Override
        public void run() {
            int rs = obj.swapEvent(customerID, newEventID, newEventType,
                    oldEventID, oldEventType);
            System.out.println(customerID + ":");
            if (rs == 0) {
                System.out.println("Congratulation, you have " +
                        "successfully swap the events!");
            }
            if (rs == -1) {
                System.out.println("Sorry, the event you canceled " +
                        "doesn't exist.");
            }
            if (rs == -2) {
                System.out.println("Sorry, the event you swap from you " +
                        "did't book before!");
            }
            if (rs == -3) {
                System.out.println("Sorry, the event you swap to " +
                        "doesn't exist!");
            }
            if (rs == -4) {
                System.out.println("Sorry, the event you swap to " +
                        "has already been booked by yourself");
            }
            if (rs == -5) {
                System.out.println("Sorry, the event you swap to " +
                        "has no availability!");
            }
            if (rs == -6) {
                System.out.println("Sorry, the event you swap to " +
                        "has a conflict with your booked events!");
            }
            if (rs == -7) {
                System.out.println("You can't book more than three " +
                        "events outside your city!");
            }

        }
    }
}
