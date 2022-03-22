package com.onevue.spring.beans.factory;

import org.omg.CORBA.ORB;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ORBFactoryBean implements InitializingBean, FactoryBean<ORB> {

	private ORB orb;

	public void init() {
	}

	@Override
	public ORB getObject() throws Exception {
		return this.orb;
	}

	@Override
	public Class<?> getObjectType() {
		return ORB.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

}
