package CorbaApp;


/**
* CorbaApp/_DEMSInterfStub.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从D:/eclipse-workspace/DEMS1.3/src/corba.idl
* 2019年7月16日 星期二 上午10时51分31秒 EDT
*/

public class _DEMSInterfStub extends org.omg.CORBA.portable.ObjectImpl implements CorbaApp.DEMSInterf
{

  public boolean manLogin (String manID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("manLogin", true);
                $out.write_string (manID);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return manLogin (manID        );
            } finally {
                _releaseReply ($in);
            }
  } // manLogin

  public boolean cusLogin (String cusID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("cusLogin", true);
                $out.write_string (cusID);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return cusLogin (cusID        );
            } finally {
                _releaseReply ($in);
            }
  } // cusLogin

  public boolean addEvent (String eventID, String eventType, String manID, int capacity)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("addEvent", true);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $out.write_string (manID);
                $out.write_long (capacity);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return addEvent (eventID, eventType, manID, capacity        );
            } finally {
                _releaseReply ($in);
            }
  } // addEvent

  public boolean removeEvent (String eventID, String eventType, String manID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("removeEvent", true);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $out.write_string (manID);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return removeEvent (eventID, eventType, manID        );
            } finally {
                _releaseReply ($in);
            }
  } // removeEvent

  public String listEventA (String manID, String eventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("listEventA", true);
                $out.write_string (manID);
                $out.write_string (eventType);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return listEventA (manID, eventType        );
            } finally {
                _releaseReply ($in);
            }
  } // listEventA

  public boolean bookEvent (String custID, String eventID, String eventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("bookEvent", true);
                $out.write_string (custID);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return bookEvent (custID, eventID, eventType        );
            } finally {
                _releaseReply ($in);
            }
  } // bookEvent

  public boolean cancelEvent (String custID, String eventID, String eventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("cancelEvent", true);
                $out.write_string (custID);
                $out.write_string (eventID);
                $out.write_string (eventType);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return cancelEvent (custID, eventID, eventType        );
            } finally {
                _releaseReply ($in);
            }
  } // cancelEvent

  public String getBookingSchedule (String custID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getBookingSchedule", true);
                $out.write_string (custID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getBookingSchedule (custID        );
            } finally {
                _releaseReply ($in);
            }
  } // getBookingSchedule

  public boolean swapEvent (String cusID, String newEventID, String newEventType, String oldEventID, String oldEventType)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("swapEvent", true);
                $out.write_string (cusID);
                $out.write_string (newEventID);
                $out.write_string (newEventType);
                $out.write_string (oldEventID);
                $out.write_string (oldEventType);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return swapEvent (cusID, newEventID, newEventType, oldEventID, oldEventType        );
            } finally {
                _releaseReply ($in);
            }
  } // swapEvent

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:CorbaApp/DEMSInterf:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _DEMSInterfStub
