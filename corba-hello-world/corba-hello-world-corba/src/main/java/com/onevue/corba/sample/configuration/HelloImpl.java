package com.onevue.corba.sample.configuration;

import org.omg.CORBA.ORB;

import HelloApp.HelloOperations;

public class HelloImpl implements HelloOperations {

	private ORB orb;

	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	@Override
	public String sayHello() {
		 return "\nHello world !!\n";
	}

	@Override
	public void shutdown() {
	}

}
