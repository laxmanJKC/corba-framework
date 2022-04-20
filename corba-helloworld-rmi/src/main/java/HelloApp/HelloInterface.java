package HelloApp;

import java.rmi.Remote;

public interface HelloInterface extends java.rmi.Remote {
  public String sayHello() throws java.rmi.RemoteException;
}
