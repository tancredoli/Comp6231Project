package EventRecord;

public class Event {

	private String eventType;
	private String eventID;

	public Event(String eventID, String eventType) {
		this.eventID = eventID;
		this.eventType = eventType;

	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public String toString() {
		String eventInfo = eventType + "," + eventID;
		return eventInfo;
	}
}
