package CorbaApp;

/**
* CorbaApp/DEMSInterfHolder.java .
* 由IDL-to-Java 编译器 (可移植), 版本 "3.2"生成
* 从D:/eclipse-workspace/DEMS1.3/src/corba.idl
* 2019年7月16日 星期二 上午10时51分31秒 EDT
*/

public final class DEMSInterfHolder implements org.omg.CORBA.portable.Streamable
{
  public CorbaApp.DEMSInterf value = null;

  public DEMSInterfHolder ()
  {
  }

  public DEMSInterfHolder (CorbaApp.DEMSInterf initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = CorbaApp.DEMSInterfHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    CorbaApp.DEMSInterfHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return CorbaApp.DEMSInterfHelper.type ();
  }

}
