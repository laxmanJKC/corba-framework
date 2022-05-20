package com.onevue.sample;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.Policy;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantAlreadyActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.onevue.example.socket.Common;
import com.onevue.example.socket.CorbaSimpleSocketFactory;
import com.onevue.example.socket.CorbaTLSSimpleSocketFactory;
import com.onevue.example.socket.MySocketFactory;
import com.onevue.example.socket.ServerORBInitializer;
import com.sun.corba.ee.spi.misc.ORBConstants;
import HelloApp.HelloHelper;
import HelloApp.HelloPOA;

public class SocketAppMain {

	public static final String MyType1 = "MyType1";

	public static final int MyType3TransientPort = 0;

	public static final String serverName1 = "ExI1";

	public static final String CUSTOM_FACTORY_CLASS = MySocketFactory.class.getName();

	public static void main(String[] args) throws InvalidName, AdapterInactive, ServantAlreadyActive, WrongPolicy, AdapterAlreadyExists, InvalidPolicy, ObjectNotActive, NotFound, CannotProceed, org.omg.CosNaming.NamingContextPackage.InvalidName {
		Properties props = System.getProperties();

		String value = Common.MyType1 + ":" + Common.MyType1PersistentPort;
		
        props.setProperty(ORBConstants.PI_ORB_INITIALIZER_CLASS_PREFIX +
                ServerORBInitializer.class.getName(),
                "dummy");

		props.setProperty(ORBConstants.LISTEN_SOCKET_PROPERTY, value);
		// REVISIT: not sure why I have to explicitly set these here
		// but not in other tests.
		props.setProperty(ORBConstants.INITIAL_PORT_PROPERTY, "1050");

		//props.setProperty(ORBConstants.SOCKET_FACTORY_CLASS_PROPERTY, CorbaSimpleSocketFactory.class.getName());
		props.setProperty(ORBConstants.SOCKET_FACTORY_CLASS_PROPERTY, CorbaTLSSimpleSocketFactory.class.getName());
		ORB orb = ORB.init(args, props);
	      com.sun.corba.ee.spi.orb.ORB ourORB
          = (com.sun.corba.ee.spi.orb.ORB)orb;
		System.out.println("==== Server GIOP version "
                + ourORB.getORBData().getGIOPVersion()
                + " with strategy "
                + ourORB.getORBData().getGIOPBuffMgrStrategy(
                		ourORB.getORBData().getGIOPVersion())
                + "====");



		// get reference to rootpoa & activate the POAManager
		POA rootPoa = POAHelper.narrow(orb.resolve_initial_references(ORBConstants.ROOT_POA_NAME));
		rootPoa.the_POAManager().activate();
		
        Policy[] policies = new Policy[1];

        // Create child POA
        policies[0] =
            true ?
            rootPoa.create_lifespan_policy(LifespanPolicyValue.TRANSIENT):
            rootPoa.create_lifespan_policy(LifespanPolicyValue.PERSISTENT);
        POA childPoa =rootPoa.create_POA("childPoa", null, policies);
        childPoa.the_POAManager().activate();

    // REVISIT - bind a root and transient.

    // create servant and register it with the ORB
    HelloImpl exIServant = new HelloImpl();
    exIServant.setORB(orb);
    byte[] id = childPoa.activate_object(exIServant);
    org.omg.CORBA.Object ref = childPoa.id_to_reference(id);

    Common.getNameService(orb).rebind(Common.makeNameComponent("hello_BindRef"), ref);
    System.out.println("STARTED");		
		orb.run();

	}
}

class HelloImpl extends HelloPOA {
	private ORB orb;

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	// implement sayHello() method
	public String sayHello() {
		return "\nHello world !!\n";
	}

	// implement shutdown() method
	public void shutdown() {
		// orb.shutdown(false);
	}
}
