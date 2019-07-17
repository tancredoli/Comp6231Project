package CorbaApp;


/**
* CorbaApp/DEMSInterfOperations.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从D:/eclipse-workspace/DEMS1.3/src/corba.idl
* 2019年7月16日 星期二 上午10时51分31秒 EDT
*/

public interface DEMSInterfOperations 
{
  boolean manLogin (String manID);
  boolean cusLogin (String cusID);
  boolean addEvent (String eventID, String eventType, String manID, int capacity);
  boolean removeEvent (String eventID, String eventType, String manID);
  String listEventA (String manID, String eventType);
  boolean bookEvent (String custID, String eventID, String eventType);
  boolean cancelEvent (String custID, String eventID, String eventType);
  String getBookingSchedule (String custID);
  boolean swapEvent (String cusID, String newEventID, String newEventType, String oldEventID, String oldEventType);
} // interface DEMSInterfOperations
