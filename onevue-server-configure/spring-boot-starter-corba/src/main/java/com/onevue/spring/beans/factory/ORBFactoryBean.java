package com.onevue.spring.beans.factory;

import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_CLASS;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_INITIAL_HOST;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_INITIAL_PORT;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_SINGLETON_CLASS;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import com.onevue.spring.configuration.OnevueCorbaProperties;

@Service
public class ORBFactoryBean implements InitializingBean, FactoryBean<ORB> {

	private ORB orb;

	private final OnevueCorbaProperties onevueCorbaProperties;

	public ORBFactoryBean(OnevueCorbaProperties onevueCorbaProperties) {
		this.onevueCorbaProperties = onevueCorbaProperties;
	}

	public void init() {
		Properties properties = properties();
		String []orbArgs = onevueCorbaProperties.getOrbArgs();
		this.orb = ORB.init(orbArgs, properties);
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

	private Properties properties() {
		Properties properties = new Properties();

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
