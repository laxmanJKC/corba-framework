package com.onevue.spring.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "onevue.corba")
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
