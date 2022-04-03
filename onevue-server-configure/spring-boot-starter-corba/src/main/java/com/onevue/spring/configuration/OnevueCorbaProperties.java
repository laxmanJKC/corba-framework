package com.onevue.spring.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "onevue.corba")
public class OnevueCorbaProperties {

	private String orbClass;

	private String orbSingletonClass;

	private String orbInitialPort = "3700";

	private String orbInitialHost = "localhost";

	private String orbName;

	private List<String> orbArgs = new ArrayList<String>();

	private List<CorbaBindingProperty> bindingProperties = new ArrayList<CorbaBindingProperty>();

	public String[] getOrbArguments() {
		if (!CollectionUtils.isEmpty(getOrbArgs())) {
			return new String[0];
		}
		return getOrbArgs().toArray(new String[getOrbArgs().size()]);
	}
}
