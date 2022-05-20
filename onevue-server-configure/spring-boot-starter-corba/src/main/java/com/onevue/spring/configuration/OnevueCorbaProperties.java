package com.onevue.spring.configuration;

import static com.onevue.spring.constants.CorbaConstants.CORBA_CONTEXT_FACTORY_INTIAL_VALUE;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.onevue.spring.enums.CorbaRegistry;
import com.onevue.spring.enums.TLSVersion;
import com.onevue.spring.model.CorbaBindingProperty;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "onevue.corba")
public class OnevueCorbaProperties {

	private String orbClass;

	private String orbSingletonClass;

	private String orbInitialPort = "3700";

	private String orbInitialHost = "localhost";
	
	private TLSVersion corbaTlsVersion = TLSVersion.TLS_1_2;
	
	private String contextFactoryInitial = CORBA_CONTEXT_FACTORY_INTIAL_VALUE;
	
	private CorbaRegistry corbaRegistry = CorbaRegistry.NAMING_CONTEXT;
	
	private String contextProtocol = "iiop";

	private String orbName;

	private List<String> orbArgs = new ArrayList<String>();

	private List<CorbaBindingProperty> bindingProperties = new ArrayList<CorbaBindingProperty>();

	public String[] getOrbArguments() {
		if (CollectionUtils.isEmpty(getOrbArgs())) {
			return new String[0];
		}
		return getOrbArgs().toArray(new String[getOrbArgs().size()]);
	}
}
