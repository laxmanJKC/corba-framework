package HelloApp;


/**
* HelloApp/HelloHelper.java .
* Error!  A message was requested which does not exist.  The messages file does not contain the key: toJavaProlog1
* Error!  A message was requested which does not exist.  The messages file does not contain the key: toJavaProlog2
* Wednesday, May 25, 2022 at 12:07:14 AM India Standard Time
*/

abstract public class HelloHelper
{
  private static String  _id = "IDL:HelloApp/Hello:1.0";

  public static void insert (org.omg.CORBA.Any a, HelloApp.Hello that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static HelloApp.Hello extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (HelloApp.HelloHelper.id (), "Hello");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static HelloApp.Hello read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_HelloStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, HelloApp.Hello value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static HelloApp.Hello narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof HelloApp.Hello)
      return (HelloApp.Hello)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      HelloApp._HelloStub stub = new HelloApp._HelloStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static HelloApp.Hello unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof HelloApp.Hello)
      return (HelloApp.Hello)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      HelloApp._HelloStub stub = new HelloApp._HelloStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
