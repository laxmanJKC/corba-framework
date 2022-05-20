package com.onevue.spring.beans.factory;

import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_CLASS;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_BEAN;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_INITIAL_HOST;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_INITIAL_PORT;
import static com.onevue.spring.constants.CorbaConstants.CORBA_CONTEXT_FACTORY_INTIAL_KEY;
import static com.onevue.spring.constants.CorbaConstants.CORBA_CONTEXT_FACTORY_INITIAL_URL_KEY;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_SINGLETON_CLASS;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.omg.CORBA.ORB;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.onevue.spring.beans.OrbBean;
import com.onevue.spring.configuration.OnevueCorbaProperties;

@Component(value = CORBA_ORB_BEAN)
public class ORBFactoryBean implements InitializingBean, FactoryBean<OrbBean> {

	private ORB orb;

	private final OnevueCorbaProperties onevueCorbaProperties;

	public ORBFactoryBean(OnevueCorbaProperties onevueCorbaProperties) {
		this.onevueCorbaProperties = onevueCorbaProperties;
	}

	public void init() {
		Properties properties = properties();
		String[] orbArgs = onevueCorbaProperties.getOrbArguments();
		this.orb = ORB.init(orbArgs, properties);
	}

	@Override
	public OrbBean getObject() throws Exception {
		return new OrbBean(this.orb);
	}

	@Override
	public Class<?> getObjectType() {
		return OrbBean.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	private Properties properties() {
		Properties properties = System.getProperties();
		List<String> args = new ArrayList<String>();

		// Check if ORBClass exist.
		String orbClass = onevueCorbaProperties.getOrbClass();
		if (!isBlank(orbClass)) {
			properties.put(CORBA_ORB_CLASS, orbClass);
			args.add(CORBA_ORB_SINGLETON_CLASS+"="+orbClass);
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
		if (!StringUtils.isEmpty(onevueCorbaProperties.getContextFactoryInitial()) && !StringUtils.isEmpty(orbInitialHost) && !StringUtils.isEmpty(orbInitialPort)) {
			StringBuilder protocol = new StringBuilder();
			protocol.append(onevueCorbaProperties.getContextProtocol()).append("://").append(onevueCorbaProperties.getOrbInitialHost()).append(":").append(onevueCorbaProperties.getOrbInitialPort());
			System.getProperties().put(CORBA_CONTEXT_FACTORY_INITIAL_URL_KEY, protocol.toString());
			System.getProperties().put(CORBA_CONTEXT_FACTORY_INTIAL_KEY, onevueCorbaProperties.getContextFactoryInitial());
			properties.put(CORBA_CONTEXT_FACTORY_INITIAL_URL_KEY, protocol.toString());
			properties.put(CORBA_CONTEXT_FACTORY_INTIAL_KEY, onevueCorbaProperties.getContextFactoryInitial());
		}
		return properties;
	}

	@Override
	public String toString() {
		return "ORBFactoryBean [orb=" + orb + ", onevueCorbaProperties=" + onevueCorbaProperties + "]";
	}

}
