package CorbaApp;


/**
* CorbaApp/DEMSInterfPOA.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从D:/eclipse-workspace/DEMS1.3/src/corba.idl
* 2019年7月16日 星期二 上午10时51分31秒 EDT
*/

public abstract class DEMSInterfPOA extends org.omg.PortableServer.Servant
 implements CorbaApp.DEMSInterfOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("manLogin", new java.lang.Integer (0));
    _methods.put ("cusLogin", new java.lang.Integer (1));
    _methods.put ("addEvent", new java.lang.Integer (2));
    _methods.put ("removeEvent", new java.lang.Integer (3));
    _methods.put ("listEventA", new java.lang.Integer (4));
    _methods.put ("bookEvent", new java.lang.Integer (5));
    _methods.put ("cancelEvent", new java.lang.Integer (6));
    _methods.put ("getBookingSchedule", new java.lang.Integer (7));
    _methods.put ("swapEvent", new java.lang.Integer (8));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // CorbaApp/DEMSInterf/manLogin
       {
         String manID = in.read_string ();
         boolean $result = false;
         $result = this.manLogin (manID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 1:  // CorbaApp/DEMSInterf/cusLogin
       {
         String cusID = in.read_string ();
         boolean $result = false;
         $result = this.cusLogin (cusID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 2:  // CorbaApp/DEMSInterf/addEvent
       {
         String eventID = in.read_string ();
         String eventType = in.read_string ();
         String manID = in.read_string ();
         int capacity = in.read_long ();
         boolean $result = false;
         $result = this.addEvent (eventID, eventType, manID, capacity);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 3:  // CorbaApp/DEMSInterf/removeEvent
       {
         String eventID = in.read_string ();
         String eventType = in.read_string ();
         String manID = in.read_string ();
         boolean $result = false;
         $result = this.removeEvent (eventID, eventType, manID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 4:  // CorbaApp/DEMSInterf/listEventA
       {
         String manID = in.read_string ();
         String eventType = in.read_string ();
         String $result = null;
         $result = this.listEventA (manID, eventType);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 5:  // CorbaApp/DEMSInterf/bookEvent
       {
         String custID = in.read_string ();
         String eventID = in.read_string ();
         String eventType = in.read_string ();
         boolean $result = false;
         $result = this.bookEvent (custID, eventID, eventType);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 6:  // CorbaApp/DEMSInterf/cancelEvent
       {
         String custID = in.read_string ();
         String eventID = in.read_string ();
         String eventType = in.read_string ();
         boolean $result = false;
         $result = this.cancelEvent (custID, eventID, eventType);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 7:  // CorbaApp/DEMSInterf/getBookingSchedule
       {
         String custID = in.read_string ();
         String $result = null;
         $result = this.getBookingSchedule (custID);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 8:  // CorbaApp/DEMSInterf/swapEvent
       {
         String cusID = in.read_string ();
         String newEventID = in.read_string ();
         String newEventType = in.read_string ();
         String oldEventID = in.read_string ();
         String oldEventType = in.read_string ();
         boolean $result = false;
         $result = this.swapEvent (cusID, newEventID, newEventType, oldEventID, oldEventType);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:CorbaApp/DEMSInterf:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public DEMSInterf _this() 
  {
    return DEMSInterfHelper.narrow(
    super._this_object());
  }

  public DEMSInterf _this(org.omg.CORBA.ORB orb) 
  {
    return DEMSInterfHelper.narrow(
    super._this_object(orb));
  }


} // class DEMSInterfPOA
