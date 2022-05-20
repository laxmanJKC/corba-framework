package com.onevue.spring.beans.factory;

import static com.onevue.spring.constants.CorbaConstants.CORBA_INITIAL_CONTEXT;

import java.util.Hashtable;
import java.util.Optional;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.onevue.spring.configuration.OnevueCorbaProperties;
import com.onevue.spring.enums.TLSVersion;

@ConditionalOnProperty(name = "onevue.corba.corba-registry", havingValue = "context")
@Component(value = CORBA_INITIAL_CONTEXT)
public class RMICorbaContextFactoryBean implements InitializingBean, FactoryBean<Context> {
	
	private Context context;
	
	private final OnevueCorbaProperties onevueCorbaProperties;

	public RMICorbaContextFactoryBean(OnevueCorbaProperties onevueCorbaProperties) {
		this.onevueCorbaProperties = onevueCorbaProperties;
	}

	@Override
	public Context getObject() throws Exception {
		return this.context;
	}

	@Override
	public Class<?> getObjectType() {
		return InitialContext.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Hashtable<String, String> initialContextProps = new Hashtable<String, String>();
		TLSVersion tlsVersion = Optional.ofNullable(onevueCorbaProperties.getCorbaTlsVersion()).orElse(TLSVersion.TLS_1_2);
		initialContextProps.put(Context.SECURITY_PROTOCOL, tlsVersion.getTlsVersion());
		this.context = new InitialContext(initialContextProps);
	}
}
