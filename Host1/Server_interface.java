package Host1;

public interface Server_interface {
	
	public String addEvent(String managerId, String eventID, String eventType,int bookingCapacity);
	public String removeEvent(String managerId, String eventID,String eventType);
	public String listEventAvailability(String managerId, String eventType);
	
	public String bookEvent(String customerID,String eventID, String eventType);
	public String getBookingSchedule(String customerID);
    public String cancelEvent(String customerID, String eventType, String eventID);
    public String swapEvent (String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType);
}
