package Client;

import DataBase.Customer;
import FEInterface.CommonInterface;
import FEInterface.CommonInterfaceHelper;
import Utility.Action;
import Utility.LogRecorder;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;
import java.util.Scanner;

/**
 * @Author: Rui
 * @Description:
 * @Date: Created in 4:04 PM 2019-07-28
 * @Modified by:
 */
public class Client {

    public static int checkEventType(String inputType) {

        if (inputType.toUpperCase().equals("CONFERENCE") || inputType.toUpperCase().equals("CON"))
            return 1;
        else if (inputType.toUpperCase().equals("TRADE SHOWS") || inputType.toUpperCase().equals("TS"))
            return 3;
        else if (inputType.toUpperCase().equals("SEMINARS") || inputType.toUpperCase().equals("SEM"))
            return 2;
        else
            return -1;
    }

    public static int checkID(String userID) {
        char[] userNameCharArray = userID.toCharArray();
        if (userID.startsWith("TOR") || userID.startsWith("OTW") || userID.startsWith("MTL")) {
            if (userNameCharArray[3] == 'M')
                return 0;
            else if (userNameCharArray[3] == 'C')
                return 1;
            else
                return -1;
        } else
            return -1;
    }

    public static boolean checkManager(String userID, String customerID) {
        return userID.substring(0, 3).equals(customerID.substring(0, 3));
    }

    public static void main(String args[]) throws IOException {

        LogRecorder clientRecorder = new LogRecorder(4);
        System.out.println("Please input your customerID or managerID:");
        Scanner sc = new Scanner(System.in);
        String userID = sc.nextLine();
        if (userID.length() != 8) {
            System.out.println("Sorry, the format of your input is not " +
                    "correct!");
            System.exit(0);
        }
        if (checkID(userID) == 0) {
            try {
                clientRecorder.writeLog(clientRecorder.paraConstructor("",
                        userID, 4, "", 0), Action.LOGIN, true, "");
                managerStart(args, userID, sc, clientRecorder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (checkID(userID) == 1) {
            try {
                customerStart(args, userID, sc, clientRecorder);
                clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                        "", 4, "", 0), Action.LOGIN, true, "");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Sorry, your input is not correct!");
            System.exit(0);
        }


    }

    private static void customerStart(
            String[] args,
            String userID, Scanner sc,
            LogRecorder clientRecorder) throws IOException {
        CommonInterface obj = getRemoteObj(args, userID);
        System.out.println("Welcome back, " + userID + "!");
        Customer customer = new Customer(userID);

        while (true) {
            System.out.println(
                    "-----------------------------------------------------------------------------------");
            System.out.println("Please choose the option as follow(input " +
                    "the character in brackets to continue):");
            System.out.println("1. (B)ook an event.");
            System.out.println("2. (L)ist all your current events.");
            System.out.println("3. (C)ancel your current event.");
            System.out.println("4. (S)wap your current event.");
            System.out.println("5. (E)xit.");

            String opt = sc.nextLine();
            if (opt.equals("S") || opt.equals("s")) {
                System.out.println("Please input the old eventType:");
                int oldEventType = checkEventType(sc.nextLine());
                if (oldEventType < 0) {
                    System.out.println("Sorry, the old eventType you input " +
                            "is " +
                            "not correct.");
                    continue;
                }
                System.out.println("Please input the old eventID:");
                String oldEventID = sc.nextLine().toUpperCase();
                System.out.println("Please input the new eventType:");
                int newEventType = checkEventType(sc.nextLine());
                if (newEventType < 0) {
                    System.out.println("Sorry, the new eventType you input " +
                            "is " +
                            "not correct.");
                    continue;
                }
                System.out.println("Please input the new eventID:");
                String newEventID = sc.nextLine().toUpperCase();
                if (!customer.checkValid(newEventID)) {
                    System.out.println("Sorry, the new eventID you input is" +
                            " incorrect.");
                    continue;
                }
                int rs = obj.swapEvent(userID, newEventID, newEventType,
                        oldEventID, oldEventType);
                if (rs == 0) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, newEventType, newEventID, 0),
                            Action.C_SWAPEVENTS, true, String.valueOf(rs));
                    System.out.println("Congratulation, you have " +
                            "successfully swap the events!");
                }
                if (rs == -1) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, newEventType, newEventID, 0),
                            Action.C_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you canceled " +
                            "doesn't exist.");
                }
                if (rs == -2) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, newEventType, newEventID, 0),
                            Action.C_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you swap from you " +
                            "did't book before!");
                }
                if (rs == -3) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, newEventType, newEventID, 0),
                            Action.C_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you swap to " +
                            "doesn't exist!");
                }
                if (rs == -4) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, newEventType, newEventID, 0),
                            Action.C_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you swap to " +
                            "has already been booked by yourself");
                }
                if (rs == -5) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, newEventType, newEventID, 0),
                            Action.C_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you swap to " +
                            "has no availability!");
                }
                if (rs == -6) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, newEventType, newEventID, 0),
                            Action.C_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you swap to " +
                            "has a conflict with your booked events!");
                }
                if (rs == -7) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, newEventType, newEventID, 0),
                            Action.C_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("You can't book more than three " +
                            "events outside your city!");
                }


            }
            if (opt.equals("E") || opt.equals("e")) {
                clientRecorder.writeLog(clientRecorder.paraConstructor("",
                        userID, 4, "", 0), Action.LOGOUT, true, "");
                System.exit(0);
            }
            if (opt.equals("B") || opt.equals("b")) {
                System.out.println("Please input the eventType:");
                int eventType = checkEventType(sc.nextLine());
                if (eventType < 0) {
                    System.out.println("Sorry, the eventType you input is " +
                            "not correct.");
                    continue;
                }
                System.out.println("Please input the eventID:");
                String eventID = sc.nextLine().toUpperCase();
                if (!customer.checkValid(eventID)) {
                    System.out.println("Sorry, the eventID you input is " +
                            "incorrect.");
                    continue;
                }
                int rs = obj.bookEvent(userID, eventID, eventType);
                if (rs == 0) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.C_BOOKEVENT, true, "0");
                    System.out.println("Congratulation, you have " +
                            "successfully booked " +
                            "this event!");
                } else if (rs == -1) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.C_BOOKEVENT, false, "-1");
                    System.out.println("Sorry, the event you booked doesn't" +
                            " exist.");
                } else if (rs == -2) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.C_BOOKEVENT, false, "-2");
                    System.out.println("Sorry, you can't book same events.");
                } else if (rs == -3) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.C_BOOKEVENT, false, "-3");
                    System.out.println("Sorry, there is no available seats " +
                            "for this event.");
                } else if (rs == -4) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.C_BOOKEVENT, false, "-4");
                    System.out.println("Sorry, you can not book events " +
                            "which are at same time slot.");
                } else if (rs == -5) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.C_BOOKEVENT, false, "-5");
                    System.out.println("Sorry, you can book at most three " +
                            "events in one month outside your city!");
                }
                continue;
            }
            if (opt.equals("C") || opt.equals("c")) {
                System.out.println("Please input the eventType:");
                int eventType = checkEventType(sc.nextLine());
                if (eventType < 0) {
                    System.out.println("Sorry, the eventType you input is " +
                            "not correct.");
                    continue;
                }
                System.out.println("Please input the eventID:");
                String eventID = sc.nextLine().toUpperCase();
                int rs = obj.cancelEvent(userID, eventID, eventType);
                if (rs == 0) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.C_CANCELEVENT, true, String.valueOf(rs));
                    System.out.println("Congratulation, you have " +
                            "successfully canceled " +
                            "this event!");
                } else if (rs == -1) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.C_CANCELEVENT, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you canceled " +
                            "doesn't exist.");
                } else if (rs == -2) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.C_CANCELEVENT, false, String.valueOf(rs));
                    System.out.println("Sorry, you didn't book this event.");
                }
                continue;
            }
            if (opt.equals("L") || opt.equals("l")) {
                String rs = "";
                rs = obj.getBookingSchedule(userID);
                String[] rsArray = rs.split(",");
                String[] rsAry = new String[rsArray.length];
                for (int i = 0; i < rsArray.length; i++) {
                    String[] temp = rsArray[i].split(" ");
                    if (temp.length > 1)
                        rsAry[i] = temp[1];
                    else
                        rsAry[i] = "";
                }
                customer.updateFromServer(rsAry[0] + rsAry[3] + rsAry[6], 2);
                customer.updateFromServer(rsAry[1] + rsAry[4] + rsAry[7], 1);
                customer.updateFromServer(rsAry[2] + rsAry[5] + rsAry[8], 3);
                clientRecorder.writeLog(clientRecorder.paraConstructor("",
                        userID, 4, "", 0),
                        Action.C_GETBOOKINGSCHEDULE, true, rs);
                System.out.println(customer.printAll());
                continue;
            }
        }
    }


    public static boolean checkEventID(String eventID, String city) {
        if (eventID.length() == 10) {
            String inputCity = eventID.substring(0, 3);
            String inputSlot = eventID.substring(3, 4);
            int inputDay = Integer.parseInt(eventID.substring(4, 6));
            int inputMon = Integer.parseInt(eventID.substring(6, 8));
            int inputYear = Integer.parseInt(eventID.substring(8, 10));
            if (inputCity.equals(city))
                if (inputSlot.equals("M") || inputSlot.equals("A") || inputSlot.equals("E"))
                    if (inputYear > 18)
                        if (inputMon > 0 && inputMon < 13)
                            if (inputDay > 0 && inputDay < 32)
                                return true;


        }
        return false;

    }

    private static void managerStart(
            String[] args,
            String userID, Scanner sc,
            LogRecorder clientRecorder) throws IOException {
        CommonInterface obj = getRemoteObj(args, userID);
        System.out.println("Welcome back, " + userID + "!");
        while (true) {
            System.out.println(
                    "-----------------------------------------------------------------------------------");
            System.out.println("Please choose the option as follow(input " +
                    "the character in brackets to continue):");
            System.out.println("1. (A)dd event or modify current event.");
            System.out.println("2. (R)emove current event.");
            System.out.println("3. (L)ist all the events of a certain event" +
                    " type.");
            System.out.println("4. (B)ook an event for a customer.");
            System.out.println("5. L(i)st all booked events for a customer.");
            System.out.println("6. (C)ancel a booked event for a customer.");
            System.out.println("7. (S)wap your current event.");
            System.out.println("0. Tada! Use (test) to output a booked list" +
                    " " +
                    "of a certain event.");
            System.out.println("8. (E)xit.");


            String opt = sc.nextLine();
            if (opt.equals("S") || opt.equals("s")) {
                System.out.println("Please input the customerID");
                String customerID = sc.nextLine().toUpperCase();
                int isValid = checkID(customerID);
                if (isValid != 1) {
                    System.out.println("Sorry, the customerID you input is " +
                            "not correct!");
                    continue;
                }
                if (!checkManager(userID, customerID)) {
                    System.out.println("Sorry, you don't have the " +
                            "authorization!");
                    continue;
                }
                System.out.println("Please input the old eventType:");
                int oldEventType = checkEventType(sc.nextLine());
                if (oldEventType < 0) {
                    System.out.println("Sorry, the old eventType you input " +
                            "is " +
                            "not correct.");
                    continue;
                }
                System.out.println("Please input the old eventID:");
                String oldEventID = sc.nextLine().toUpperCase();
                System.out.println("Please input the new eventType:");
                int newEventType = checkEventType(sc.nextLine());
                if (newEventType < 0) {
                    System.out.println("Sorry, the new eventType you input " +
                            "is " +
                            "not correct.");
                    continue;
                }
                Customer customer = new Customer(customerID);
                System.out.println("Please input the new eventID:");
                String newEventID = sc.nextLine().toUpperCase();
                if (!customer.checkValid(newEventID)) {
                    System.out.println("Sorry, the new eventID you input is" +
                            " incorrect.");
                    continue;
                }
                int rs = obj.swapEvent(customerID, newEventID, newEventType,
                        oldEventID, oldEventType);
                if (rs == 0) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, newEventType, newEventID, 0),
                            Action.M_SWAPEVENTS, true, String.valueOf(rs));
                    System.out.println("Congratulation, you have " +
                            "successfully swap the events!");
                }
                if (rs == -1) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, newEventType, newEventID, 0),
                            Action.M_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you canceled " +
                            "doesn't exist.");
                }
                if (rs == -2) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, newEventType, newEventID, 0),
                            Action.M_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you swap from you " +
                            "did't book before!");
                }
                if (rs == -3) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, newEventType, newEventID, 0),
                            Action.M_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you swap to " +
                            "doesn't exist!");
                }
                if (rs == -4) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, newEventType, newEventID, 0),
                            Action.M_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you swap to " +
                            "has already been booked by yourself");
                }
                if (rs == -5) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, newEventType, newEventID, 0),
                            Action.M_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you swap to " +
                            "has no availability!");
                }
                if (rs == -6) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, newEventType, newEventID, 0),
                            Action.M_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you swap to " +
                            "has a conflict with your booked events!");
                }
                if (rs == -7) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, newEventType, newEventID, 0),
                            Action.M_SWAPEVENTS, false, String.valueOf(rs));
                    System.out.println("You can't book more than three " +
                            "events outside your city!");
                }


            }

            if (opt.equals("E") || opt.equals("e")) {
                clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                        "", 4, "", 0),
                        Action.LOGOUT, true, " ");
                System.exit(0);
            }
            if (opt.equals("B") || opt.equals("b")) {
                System.out.println("Please input the customerID");
                String customerID = sc.nextLine().toUpperCase();
                int isValid = checkID(customerID);
                if (isValid != 1) {
                    System.out.println("Sorry, the customerID you input is " +
                            "not correct!");
                    continue;
                }
                if (!checkManager(userID, customerID)) {
                    System.out.println("Sorry, you don't have the " +
                            "authorization!");
                    continue;
                }
                System.out.println("Please input the eventType:");
                int eventType = checkEventType(sc.nextLine());
                if (eventType < 0) {
                    System.out.println("Sorry, the eventType you input is " +
                            "not correct.");
                    continue;
                }
                Customer customer = new Customer(customerID);
                System.out.println("Please input the eventID:");
                String eventID = sc.nextLine().toUpperCase();
                if (!customer.checkValid(eventID)) {
                    System.out.println("Sorry, the eventID you input is " +
                            "incorrect.");
                    continue;
                }
                int rs = obj.bookEvent(customerID, eventID, eventType);
                if (rs == 0) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.M_BOOKEVENT, true, "0");
                    System.out.println("Congratulation, you have " +
                            "successfully booked " +
                            "this event!");
                } else if (rs == -1) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.M_BOOKEVENT, false, "-1");
                    System.out.println("Sorry, the event you booked doesn't" +
                            " exist.");
                } else if (rs == -2) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.C_BOOKEVENT, false, "-2");
                    System.out.println("Sorry, you can't book same events.");
                } else if (rs == -3) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.M_BOOKEVENT, false, "-3");
                    System.out.println("Sorry, there is no available seats " +
                            "for this event.");
                } else if (rs == -4) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.M_BOOKEVENT, false, "-4");
                    System.out.println("Sorry, you can not book events " +
                            "which are at same time slot.");
                } else if (rs == -5) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor("",
                            userID, eventType, eventID, 0),
                            Action.M_BOOKEVENT, false, "-5");
                    System.out.println("Sorry, you can book at most three " +
                            "events in one month outside your city!");
                }
                continue;
            }
            if (opt.equals("C") || opt.equals("c")) {
                System.out.println("Please input the customerID");
                String customerID = sc.nextLine().toUpperCase();
                int isValid = checkID(customerID);
                if (isValid != 1) {
                    System.out.println("Sorry, the customerID you input is " +
                            "not correct!");
                    continue;
                }
                if (!checkManager(userID, customerID)) {
                    System.out.println("Sorry, you don't have the " +
                            "authorization!");
                    continue;
                }
                System.out.println("Please input the eventType:");
                int eventType = checkEventType(sc.nextLine());
                if (eventType < 0) {
                    System.out.println("Sorry, the eventType you input is " +
                            "not correct.");
                    continue;
                }
                System.out.println("Please input the eventID:");
                String eventID = sc.nextLine().toUpperCase();
                int rs = obj.cancelEvent(customerID, eventID, eventType);
                if (rs == 0) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, eventType, eventID, 0),
                            Action.M_CANCELEVENT, true, String.valueOf(rs));
                    System.out.println("Congratulation, you have " +
                            "successfully canceled " +
                            "this event!");
                } else if (rs == -1) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, eventType, eventID, 0),
                            Action.M_CANCELEVENT, false, String.valueOf(rs));
                    System.out.println("Sorry, the event you canceled " +
                            "doesn't exist.");
                } else if (rs == -2) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            customerID, eventType, eventID, 0),
                            Action.M_CANCELEVENT, false, String.valueOf(rs));
                    System.out.println("Sorry, the customer didn't book " +
                            "this event.");
                }
                continue;
            }
            if (opt.equals("I") || opt.equals("i")) {
                System.out.println("Please input the customerID");
                String customerID = sc.nextLine().toUpperCase();
                int isValid = checkID(customerID);
                if (isValid != 1) {
                    System.out.println("Sorry, the customerID you input is " +
                            "not correct!");
                    continue;
                }
                if (!checkManager(userID, customerID)) {
                    System.out.println("Sorry, you don't have the " +
                            "authorization!");
                    continue;
                }
                Customer customer = new Customer(customerID);
                String rs = "";
                rs = obj.getBookingSchedule(customerID);
                String[] rsArray = rs.split(",");
                String[] rsAry = new String[rsArray.length];
                for (int i = 0; i < rsArray.length; i++) {
                    String[] temp = rsArray[i].split(" ");
                    if (temp.length > 1)
                        rsAry[i] = temp[1];
                    else
                        rsAry[i] = "";
                }
                customer.updateFromServer(rsAry[0] + rsAry[3] + rsAry[6], 2);
                customer.updateFromServer(rsAry[1] + rsAry[4] + rsAry[7], 1);
                customer.updateFromServer(rsAry[2] + rsAry[5] + rsAry[8], 3);
                clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                        customerID, 4, "", 0),
                        Action.M_GETBOOKINGSCHEDULE, true, rs);
                System.out.println(customer.printAll());
                continue;
            }
            if (opt.equals("A") || opt.equals("a")) {
                System.out.println("Please input the eventType:");
                int eventType = checkEventType(sc.nextLine());
                if (eventType < 0) {
                    System.out.println("Sorry, the eventType you input is " +
                            "not correct.");
                    continue;
                }
                System.out.println("Please input the eventID:");
                String eventID = sc.nextLine().toUpperCase();
                if (!checkEventID(eventID, userID.substring(0, 3))) {
                    System.out.println("Sorry, the eventID you input is " +
                            "not correct.");
                    continue;
                }
                System.out.println("Please input the bookingCapacity:");
                int bookingCapacity = Integer.parseInt(sc.nextLine());
                if (bookingCapacity < 0) {
                    System.out.println("Sorry, the format of " +
                            "bookingCapacity is incorrect");
                    continue;
                }
                obj.addEvent(userID, eventID, eventType, bookingCapacity);
                clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                        "", eventType, eventID, bookingCapacity),
                        Action.ADDEVENT, true, "true");
                System.out.println("The event has been successfully added!");
                continue;
            }
            if (opt.equals("R") || opt.equals("r")) {
                System.out.println("Please input the eventType:");
                int eventType = checkEventType(sc.nextLine());
                if (eventType < 0) {
                    System.out.println("Sorry, the eventType you input is " +
                            "not correct.");
                    continue;
                }
                System.out.println("Please input the eventID:");
                String eventID = sc.nextLine().toUpperCase();
                if (obj.removeEvent(userID, eventID, eventType)) {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            "", eventType, eventID, 0),
                            Action.REMOVEEVENT, true, "true");
                    System.out.println("The event has been successfully " +
                            "removed!");
                } else {
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            "", eventType, eventID, 0),
                            Action.REMOVEEVENT, false, "false");
                    System.out.println("The event you want to remove does " +
                            "not exists.");
                }

                continue;
            }
            if (opt.equals("L") || opt.equals("l")) {
                System.out.println("Please input the eventType:");
                int eventType = checkEventType(sc.nextLine());
                if (eventType < 0) {
                    System.out.println("Sorry, the eventType you input is " +
                            "not correct.");
                    continue;
                }
                String rs = obj.listEventAvailability(userID, eventType);
                if (rs.length() != 0) {
                    rs = rs.substring(0, rs.length() - 1);
                    if (eventType == 1)
                        rs = "Conferences: " + rs;
                    else if (eventType == 2)
                        rs = "Seminars: " + rs;
                    else if (eventType == 3)
                        rs = "Trade Shows: " + rs;
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            "", eventType, "", 0),
                            Action.LISTEVENTAVAILABILITY, true, rs);
                    System.out.println(rs);
                } else {
                    System.out.println("Sorry, there is no records of this " +
                            "type in database.");
                    clientRecorder.writeLog(clientRecorder.paraConstructor(userID,
                            "", eventType, "", 0),
                            Action.LISTEVENTAVAILABILITY, true, rs);
                }

            }

        }


    }

    public static CommonInterface getRemoteObj(
            String[] args,
            String userID) {
        ORB orb = ORB.init(args, null);
        //-ORBInitialPort 1050 -ORBInitialHost localhost
        org.omg.CORBA.Object objRef = null;
        CommonInterface obj = null;
        try {
            String objName = "mtl";
            if (userID.startsWith("OTW"))
                objName = "otw";
            else if (userID.startsWith("TOR"))
                objName = "tor";
            objRef = orb.resolve_initial_references(
                    "NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            obj = (CommonInterface) CommonInterfaceHelper.narrow(ncRef.resolve_str(
                    objName));

        } catch (InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (CannotProceed cannotProceed) {
            cannotProceed.printStackTrace();
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }

        return obj;
    }
}
