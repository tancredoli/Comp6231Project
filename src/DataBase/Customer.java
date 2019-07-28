package DataBase;

import java.util.*;

/**
 * @Author: Rui
 * @Description:
 * @Date: Created in 4:34 PM 2019-07-06
 * @Modified by:
 */
public class Customer {
    public boolean isUpdated;
    private ArrayList<String> conList;
    private ArrayList<String> semList;
    private ArrayList<String> tsList;
    private String customerID;
    private String city;
    private ArrayList<String> timeUsed;
    private HashMap<String, Integer> monCount;


    public Customer(String customerID) {
        this.customerID = customerID;
        if (customerID.startsWith("MTL"))
            this.city = "MTL";
        else if (customerID.startsWith("OTW"))
            this.city = "OTW";
        else if (customerID.startsWith("TOR"))
            this.city = "TOR";
        this.conList = new ArrayList();
        this.semList = new ArrayList();
        this.tsList = new ArrayList();

        this.timeUsed = new ArrayList();
        this.isUpdated = false;
        this.monCount = new HashMap<>();
    }


    public void updateFromServer(String input, int eventType) {
        List<String> eventList = new ArrayList<>();
        if (input.isEmpty()) {


        } else {
            String[] eventArray =
                    input.substring(0, input.length() - 1).split(
                            ";");
            eventList = Arrays.asList(eventArray);
        }


        if (eventType == 1) {
            conList.clear();
            conList.addAll(eventList);
        }

        if (eventType == 2) {
            semList.clear();
            semList.addAll(eventList);
        }
        if (eventType == 3) {
            tsList.clear();
            tsList.addAll(eventList);

        }

        isUpdated = true;

    }

    //initialize the time slots the used booked
    public void iniUsedtime() {
        timeUsed.clear();
        monCount.clear();
        Iterator itr1 = conList.iterator();
        Iterator itr2 = semList.iterator();
        Iterator itr3 = tsList.iterator();
        while (itr1.hasNext()) {
            timeUsed.add(itr1.next().toString().substring(3));
        }
        while (itr2.hasNext()) {
            timeUsed.add(itr2.next().toString().substring(3));
        }
        while (itr3.hasNext()) {
            timeUsed.add(itr3.next().toString().substring(3));
        }

        ArrayList allList = (ArrayList) conList.clone();
        allList.addAll(semList);
        allList.addAll(tsList);
        Iterator i = allList.iterator();
        while (i.hasNext()) {
            String cur = i.next().toString();
            if (!cur.startsWith(city)) {
                String mon = cur.substring(6);
                if (!monCount.keySet().contains(mon))
                    monCount.put(mon, 1);
                else
                    monCount.put(mon, monCount.get(mon) + 1);
            }
        }
    }

    public int checkBookable(String eventID) {

        iniUsedtime();
        Iterator itr = timeUsed.iterator();
        while (itr.hasNext()) {
            if (eventID.substring(3).equals(itr.next().toString()))
                return -1;
        }
        if (!eventID.startsWith(city)) {
            String mon = eventID.substring(6);
            if (monCount.keySet().contains(mon) && monCount.get(mon) > 2)
                return -2;
        }
        return 0;
    }

    public boolean checkValid(String eventID) {
        if (eventID.length() == 10) {
            String inputCity = eventID.substring(0, 3);
            String inputSlot = eventID.substring(3, 4);
            int inputDay = Integer.parseInt(eventID.substring(4, 6));
            int inputMon = Integer.parseInt(eventID.substring(6, 8));
            int inputYear = Integer.parseInt(eventID.substring(8, 10));
            if (inputCity.equals("MTL") || inputCity.equals("TOR") || inputCity.equals("OTW"))
                if (inputSlot.equals("M") || inputSlot.equals("A") || inputSlot.equals("E"))
                    if (inputYear > 18)
                        if (inputMon > 0 && inputMon < 13)
                            if (inputDay > 0 && inputDay < 32)
                                return true;


        }
        return false;
    }

    public boolean removeEvent(int eventType, String eventID) {
        boolean rs = false;
        if (eventType == 1) {
            rs = this.conList.remove(eventID);

        }
        if (eventType == 2) {
            rs = this.semList.remove(eventID);
        }
        if (eventType == 3) {
            rs = this.tsList.remove(eventID);
        }
        return rs;
    }

    public boolean addEvent(int eventType, String eventID) {
        boolean rs = false;
        if (eventType == 1) {
            rs = this.conList.add(eventID);

        }
        if (eventType == 2) {
            rs = this.semList.add(eventID);
        }
        if (eventType == 3) {
            rs = this.tsList.add(eventID);
        }
        return rs;
    }

    public String printAll() {

        String rs = "";
        Iterator itr1 = conList.iterator();
        Iterator itr2 = semList.iterator();
        Iterator itr3 = tsList.iterator();
        rs += "Conferences: ";
        while (itr1.hasNext()) {
            rs += itr1.next();
            rs += " ";
        }
        rs += System.getProperty("line.separator") + "Seminars: ";
        while (itr2.hasNext()) {
            rs += itr2.next();
            rs += " ";
        }
        rs += System.getProperty("line.separator") + "Trade Shows: ";
        while (itr3.hasNext()) {
            rs += itr3.next();
            rs += " ";
        }
        return rs;

    }
}
