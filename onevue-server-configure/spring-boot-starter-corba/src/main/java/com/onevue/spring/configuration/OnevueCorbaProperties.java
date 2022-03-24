package com.onevue.spring.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "onevue.corba", ignoreInvalidFields = true)
public class OnevueCorbaProperties {

	private String orbClass;

	private String orbSingletonClass;

	private String orbInitialPort;

	private String orbInitialHost;

	private List<String> orbArgs;

	public String[] getOrbArgs() {
		if (!CollectionUtils.isEmpty(orbArgs)) {
			return orbArgs.toArray(new String[orbArgs.size()]);
		}
		return null;
	}
}
