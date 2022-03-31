package com.onevue.spring.configuration;

import static com.onevue.spring.constants.CorbaConstants.CORBA_ONEVU;

import java.util.ArrayList;
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

	private String orbInitialPort = "3700";

	private String orbInitialHost;
	
	private String orbName;
	
	private List<CorbaBindingProperty> bindingProperties = new ArrayList<CorbaBindingProperty>();

	private List<String> orbArgs;

	public String[] getOrbArgs() {
		if (!CollectionUtils.isEmpty(orbArgs)) {
			return orbArgs.toArray(new String[orbArgs.size()]);
		}
		return null;
	}

	public String getOrbName() {
		return orbName;
	}

	public void setOrbName(String orbName) {
		if (orbName == null) {
			return ;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(CORBA_ONEVU);
		sb.append(".");
		sb.append(orbName.toLowerCase());
		this.orbName = sb.toString();
	}
}
