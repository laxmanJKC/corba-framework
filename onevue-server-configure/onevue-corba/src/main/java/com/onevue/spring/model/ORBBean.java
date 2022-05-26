package com.onevue.spring.model;

import org.omg.CORBA.ORB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

public class ORBBean implements Lifecycle {

	private static final Logger LOGGER = LoggerFactory.getLogger(ORBBean.class);

	private boolean isRunning = false;

	private final ORB orb;

	public ORBBean(ORB orb) {
		this.orb = orb;
	}

	@Override
	public void start() {
		this.isRunning = true;
		LOGGER.info("Corba SERVER STARTING !!");
		this.orb.run();
		LOGGER.info("Corba SERVER STARTED !!");
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
