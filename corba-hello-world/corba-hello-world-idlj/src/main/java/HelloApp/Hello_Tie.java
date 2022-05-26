package HelloApp;


/**
* HelloApp/Hello_Tie.java .
* Error!  A message was requested which does not exist.  The messages file does not contain the key: toJavaProlog1
* Error!  A message was requested which does not exist.  The messages file does not contain the key: toJavaProlog2
* Wednesday, May 25, 2022 at 12:07:14 AM India Standard Time
*/

public class Hello_Tie extends _HelloImplBase
{

  // Constructors
  public Hello_Tie ()
  {
  }

  public Hello_Tie (HelloApp.HelloOperations impl)
  {
    super ();
    _impl = impl;
  }

  public String sayHello ()
  {
    return _impl.sayHello();
  } // sayHello

  public void shutdown ()
  {
    _impl.shutdown();
  } // shutdown

  private HelloApp.HelloOperations _impl;

} // class Hello_Tie
