package com.onevue.sample;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import com.onevue.example.socket.Common;
import com.onevue.example.socket.CorbaSimpleSocketFactory;
import com.onevue.example.socket.CorbaTLSSimpleSocketFactory;
import com.onevue.example.socket.MySocketFactory;
import com.sun.corba.ee.spi.legacy.connection.ORBSocketFactory;
import com.sun.corba.ee.spi.misc.ORBConstants;

import HelloApp.Hello;
import HelloApp.HelloHelper;

public class SocketClientAppMain {

	public static void main(String[] args) throws Exception {
		Properties props = new Properties();

		//props.put(ORBConstants.SOCKET_FACTORY_CLASS_PROPERTY, CorbaSimpleSocketFactory.class.getName());
		props.setProperty(ORBConstants.SOCKET_FACTORY_CLASS_PROPERTY, CorbaTLSSimpleSocketFactory.class.getName());
		props.setProperty(ORBConstants.INITIAL_PORT_PROPERTY, "1050");

		ORB orb = ORB.init(args, props);

		Common.upDownReset();

		resolveAndInvoke(orb, "hello_BindRef");


// Make sure that the factory that was used matches the name given.
		/*
		 * MySocketFactory socketFactory =
		 * (MySocketFactory)((com.sun.corba.ee.spi.orb.ORB)
		 * orb).getORBData().getSocketFactory(); Socket clientSocket =
		 * socketFactory.createSocket("TEST", new InetSocketAddress(1050)); PrintWriter
		 * out = new PrintWriter(clientSocket.getOutputStream(), true); BufferedReader
		 * in = new BufferedReader(new
		 * InputStreamReader(clientSocket.getInputStream()));
		 * 
		 * out.write("GG"); in.close(); out.close(); clientSocket.close();
		 */
		orb.shutdown(false);
		orb.destroy();
	}

	public static void resolveAndInvoke(ORB orb, String name) throws Exception {
		Hello exIRef;

		System.out.println();
		System.out.println("BEGIN: invoke on " + name);

		exIRef = HelloHelper.narrow(resolve("hello_BindRef", name, orb));

		invoke("Fifth", exIRef);

		System.out.println("END: invoke on " + name);
	}

	public static org.omg.CORBA.Object resolve(String msg, String name, ORB orb) throws Exception {
// List initial references.

		System.out.println();
		System.out.println("BEGIN: " + msg + " list_initial_references.");

		String services[] = orb.list_initial_services();
		for (int i = 0; i < services.length; i++) {
			System.out.print(" " + services[i]);
		}
		System.out.println();

		System.out.println("END: " + msg + " list_initial_references.");

// Resolve.

		System.out.println();
		System.out.println("BEGIN: " + msg + " resolve.");
		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

		Hello helloImpl = HelloHelper.narrow(ncRef.resolve_str(name));
		org.omg.CORBA.Object ref = ncRef.resolve(Common.makeNameComponent(name));

		System.out.println("END: " + msg + " resolve.");
		return ref;
	}

	public static void invoke(String msg, Hello exIRef) {
		System.out.println();
		System.out.println("BEGIN: " + msg + " invocation.");

		String helloInvoke = exIRef.sayHello();

		System.out.println(msg + " END: " + helloInvoke + " invocation.");
	}
}
