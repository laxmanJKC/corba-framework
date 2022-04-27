package com.sample;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import HelloApp.HelloInterface;

public class HelloRmiClient {
	public static void main(String args[]) {
		Context ic;
		Object objref;
		HelloInterface hi;

		try {
			Hashtable<String, String> hashtable = new Hashtable<>();
			hashtable.put("java.naming.factory.initial", "org.glassfish.jndi.cosnaming.CNCtxFactory");
			hashtable.put("java.naming.provider.url", "iiop://localhost:1050");
			ic = new InitialContext(hashtable);
		} catch (NamingException e) {
			System.out.println("failed to obtain context" + e);
			e.printStackTrace();
			return;
		}

		// STEP 1: Get the Object reference from the Name Service
		// using JNDI call.
		try {
			objref = ic.lookup("HelloService");
			System.out.println("Client: Obtained a ref. to Hello server.");
		} catch (NamingException e) {
			System.out.println("failed to lookup object reference");
			e.printStackTrace();
			return;
		}

		// STEP 2: Narrow the object reference to the concrete type and
		// invoke the method.
		try {
			hi = (HelloInterface) PortableRemoteObject.narrow(objref, HelloInterface.class);
			System.out.println(hi.sayHello());
		} catch (ClassCastException e) {
			System.out.println("narrow failed");
			e.printStackTrace();
			return;
		} catch (Exception e) {
			System.err.println("Exception " + e + "Caught");
			e.printStackTrace();
			return;
		}
	}
}
