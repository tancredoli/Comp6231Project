package ServerInterface;

/**
* ServerInterface/CommonInterfaceHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Project/Comp6231A2/src/ServerInterface.idl
* Saturday, July 6, 2019 4:09:45 o'clock PM EDT
*/

public final class CommonInterfaceHolder implements org.omg.CORBA.portable.Streamable
{
  public ServerInterface.CommonInterface value = null;

  public CommonInterfaceHolder ()
  {
  }

  public CommonInterfaceHolder (ServerInterface.CommonInterface initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = ServerInterface.CommonInterfaceHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    ServerInterface.CommonInterfaceHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return ServerInterface.CommonInterfaceHelper.type ();
  }

}
