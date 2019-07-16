package ServerInterface;


/**
* ServerInterface/CommonInterfaceOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Project/Comp6231A2/src/ServerInterface.idl
* Saturday, July 6, 2019 4:09:45 o'clock PM EDT
*/

public interface CommonInterfaceOperations 
{
  boolean addEvent (String userID, String eventID, int eventType, int bookingCapacity);
  boolean removeEvent (String userID, String eventID, int eventType);
  String listEventAvailability (String userID, int eventType);
  int bookEvent (String customerID, String eventID, int eventType);
  String getBookingSchedule (String customerID);
  int cancelEvent (String customerID, String eventID, int eventType);
  String getBookedList (String eventID, int eventType);
  int swapEvent (String customerID, String newEventID, int newEventType, String oldEventID, int oldEventType);
} // interface CommonInterfaceOperations