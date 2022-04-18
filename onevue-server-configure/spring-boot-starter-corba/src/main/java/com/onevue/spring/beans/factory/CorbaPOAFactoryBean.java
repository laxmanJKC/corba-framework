package com.onevue.spring.beans.factory;

import org.omg.PortableServer.POA;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import com.onevue.spring.configuration.CorbaBindingProperty;

public class CorbaPOAFactoryBean implements InitializingBean, FactoryBean<POA>  {
	
	private final CorbaBindingProperty bindingProperty;
	
	private final POA rootPOA;
	
	private final ApplicationContext context;
	
	public CorbaPOAFactoryBean(ApplicationContext context, CorbaBindingProperty bindingProperty, POA rootPOA) {
		this.context = context;
		this.rootPOA = rootPOA;
		this.bindingProperty = bindingProperty;
	}

	@Override
	public POA getObject() throws Exception {
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return POA.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

}
