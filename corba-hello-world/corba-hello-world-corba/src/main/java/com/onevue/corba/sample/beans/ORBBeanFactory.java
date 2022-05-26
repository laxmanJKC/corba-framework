package com.onevue.corba.sample.beans;

import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_BEAN;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_CLASS;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_INITIAL_HOST;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_INITIAL_PORT;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_SINGLETON_CLASS;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.onevue.spring.configuration.OnevueCorbaProperties;
import com.onevue.spring.model.ORBBean;

@Component(value = CORBA_ORB_BEAN)
public class ORBBeanFactory implements InitializingBean, FactoryBean<ORBBean> {

	private ORB orb;

	private final OnevueCorbaProperties onevueCorbaProperties;

	public ORBBeanFactory(OnevueCorbaProperties onevueCorbaProperties) {
		this.onevueCorbaProperties = onevueCorbaProperties;
	}

	@Override
	public ORBBean getObject() throws Exception {
		return new ORBBean(orb);
	}

	@Override
	public Class<ORBBean> getObjectType() {
		return ORBBean.class;
	}

	public void init() {
		Properties properties = properties();
		String[] orbArgs = onevueCorbaProperties.getOrbArguments();
		this.orb = ORB.init(orbArgs, properties);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	private Properties properties() {
		Properties properties = System.getProperties();

		// Check if ORBClass exist.
		String orbClass = onevueCorbaProperties.getOrbClass();
		if (!isBlank(orbClass)) {
			properties.put(CORBA_ORB_CLASS, orbClass);
		}

		// Check if ORBSingletonClass exist
		String orbSingletonClass = onevueCorbaProperties.getOrbSingletonClass();
		if (!isBlank(orbSingletonClass)) {
			properties.put(CORBA_ORB_SINGLETON_CLASS, orbSingletonClass);
		}

		// Check if ORBInitialHost exist
		String orbInitialHost = onevueCorbaProperties.getOrbInitialHost();
		if (!isBlank(orbInitialHost)) {
			properties.put(CORBA_ORB_INITIAL_HOST, orbInitialHost);
		}

		// Check if ORBInitialPort exist
		String orbInitialPort = onevueCorbaProperties.getOrbInitialPort();
		if (!isBlank(orbInitialPort)) {
			properties.put(CORBA_ORB_INITIAL_PORT, orbInitialPort);
		}
		return properties;
	}
}
