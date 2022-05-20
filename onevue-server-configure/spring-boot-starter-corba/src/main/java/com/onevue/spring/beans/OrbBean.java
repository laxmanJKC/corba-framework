package com.onevue.spring.beans;

import org.omg.CORBA.ORB;
import org.springframework.context.Lifecycle;
import com.sun.corba.ee.spi.transport.ContactInfoListFactory;

public class OrbBean implements Lifecycle {
	
	private boolean isRunning = false;
	
	private final ORB orb;
	
	public OrbBean(ORB orb) {
		this.orb = orb;
	}

	@Override
	public void start() {
		this.isRunning = true;
		ContactInfoListFactory contactInfoListFactory =((com.sun.corba.ee.impl.orb.ORBImpl)this.orb).getCorbaContactInfoListFactory();
		this.orb.run();
	}

	@Override
	public void stop() {
		this.isRunning = false;
		this.orb.shutdown(isRunning);
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	public ORB getOrb() {
		return orb;
	}
}
