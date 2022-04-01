package com.helloworld;

import org.omg.CORBA.ORB;

import HelloApp.HelloPOA;

public class HelloImpl extends HelloPOA {
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
