package DataBase;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Author: Rui
 * @Description:
 * @Date: Created in 4:34 PM 2019-07-06
 * @Modified by:
 */
public class EventDetails implements Serializable {
    private int bookingCapacity;
    private int currentBooking;
    private int availableSeats;
    private ArrayList<String> bookedList;

    public EventDetails(int bookingCapacity) {
        super();
        this.bookingCapacity = bookingCapacity;
        this.availableSeats = bookingCapacity;
        this.currentBooking = 0;
        this.bookedList = new ArrayList<>();
    }

    public EventDetails(int bookingCapacity, ArrayList<String> bookedList) {
        super();
        this.bookingCapacity = bookingCapacity;
        this.currentBooking = bookedList.size();
        this.availableSeats = bookingCapacity - this.currentBooking;
        this.bookedList = bookedList;
    }

    public int getBookingCapacity() {
        return bookingCapacity;
    }

    public void setBookingCapacity(int bookingCapacity) {
        this.bookingCapacity = bookingCapacity;
    }

    public int getCurrentBooking() {
        return currentBooking;
    }

    public void setCurrentBooking(int currentBooking) {
        this.currentBooking = currentBooking;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public ArrayList<String> getBookedList() {
        return bookedList;
    }

    public void setBookedList(ArrayList<String> bookedList) {
        this.bookedList = bookedList;
    }

    public synchronized int book(String customerID) {
        if (this.bookedList.contains(customerID))
            return -2;//the customer has already booked this event.
        else if (this.availableSeats == 0)
            return -3;// no space!
        else {
            this.bookedList.add(customerID);
            this.currentBooking += 1;
            this.availableSeats -= 1;
            return 0;
        }
    }

    @Override
    public String toString() {
        String output;
        if (this.bookedList != null) {
            output =
                    this.bookingCapacity + "," + this.availableSeats + "," + this.bookedList.toString();
        } else
            output = String.valueOf(this.bookingCapacity);
        return output;
    }

//    public synchronized boolean cancel(String customerID) {
//        if (this.currentBooking == 0)
//            return false;
//        else if (!this.bookedList.contains(customerID))
//            return false;
//        else {
//            this.bookedList.remove(customerID);
//            this.currentBooking -= 1;
//            this.availableSeats += 1;
//            return true;
//        }
//
//    }

    /*
     *
     * @Description:
     * mType:   0 for booking event
     *          1 for canceling event
     *
     * @author: Rui
     * @date: 2:48 PM 2019-06-10
     * @param: [customerID, mType]
     * @return: int
     *
     */
    public synchronized int modify(String customerID, int mType) {
        if (mType == 0)// book event;
        {
            if (this.bookedList.contains(customerID))
                return -2;//the customer has already booked this event.
            else if (this.availableSeats == 0)
                return -3;// no space!
            else {
                this.bookedList.add(customerID);
                this.currentBooking += 1;
                this.availableSeats -= 1;
                return 0;
            }
        } else if (mType == 1)// cancle event;
        {
            if (this.currentBooking == 0)
                return -2; // no one booked this event before.
            else if (!this.bookedList.contains(customerID))
                return -2; // not on the bookedList.
            else {
                this.bookedList.remove(customerID);
                this.currentBooking -= 1;
                this.availableSeats += 1;
                return 0;
            }
        } else
            return -100;

    }
}
