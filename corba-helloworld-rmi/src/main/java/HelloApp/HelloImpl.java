package HelloApp;

import javax.rmi.PortableRemoteObject;

public class HelloImpl extends PortableRemoteObject implements HelloInterface {
  public HelloImpl() throws java.rmi.RemoteException {
    super();     // invoke rmi linking and remote object initialization
  }

  public String sayHello() throws java.rmi.RemoteException {
    System.out.println( "It works!  Hello World!!" );
    return "RMI Hello World";
  }
}

