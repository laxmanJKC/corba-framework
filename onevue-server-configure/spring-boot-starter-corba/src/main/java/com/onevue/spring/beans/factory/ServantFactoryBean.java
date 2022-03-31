package com.onevue.spring.beans.factory;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class ServantFactoryBean implements InitializingBean, FactoryBean<Servant> {

	private final Servant servant;
	
	private final POA rootPOA;
	
	public ServantFactoryBean(Servant servant, POA rootPOA) {
		this.servant = servant;
		this.rootPOA = rootPOA;
	}

	@Override
	public Servant getObject() throws Exception {
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return Servant.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub

	}

}
