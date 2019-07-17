package CorbaApp;


/**
* CorbaApp/DEMSInterfHelper.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从D:/eclipse-workspace/DEMS1.3/src/corba.idl
* 2019年7月16日 星期二 上午10时51分31秒 EDT
*/

abstract public class DEMSInterfHelper
{
  private static String  _id = "IDL:CorbaApp/DEMSInterf:1.0";

  public static void insert (org.omg.CORBA.Any a, CorbaApp.DEMSInterf that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static CorbaApp.DEMSInterf extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (CorbaApp.DEMSInterfHelper.id (), "DEMSInterf");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static CorbaApp.DEMSInterf read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_DEMSInterfStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, CorbaApp.DEMSInterf value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static CorbaApp.DEMSInterf narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof CorbaApp.DEMSInterf)
      return (CorbaApp.DEMSInterf)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      CorbaApp._DEMSInterfStub stub = new CorbaApp._DEMSInterfStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static CorbaApp.DEMSInterf unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof CorbaApp.DEMSInterf)
      return (CorbaApp.DEMSInterf)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      CorbaApp._DEMSInterfStub stub = new CorbaApp._DEMSInterfStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
