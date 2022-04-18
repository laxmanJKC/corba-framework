package com.onevue.spring.beans.factory;

import static com.onevue.spring.constants.CorbaConstants.CORBA_INITIAL_CONTEXT;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component(value = CORBA_INITIAL_CONTEXT)
public class RMICorbaContextFactoryBean implements InitializingBean, FactoryBean<Context> {

	@Override
	public Context getObject() throws Exception {
		return new InitialContext();
	}

	@Override
	public Class<?> getObjectType() {
		return InitialContext.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}
}
