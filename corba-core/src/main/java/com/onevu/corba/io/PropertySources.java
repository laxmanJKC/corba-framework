package com.onevu.corba.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertySources {
	
	private List<PropertySource> propertySources = new ArrayList<PropertySource>();
	
	public void addProperty(String key, String value) {
		PropertySource propertySource = new PropertySource(key, value);
		propertySources.add(propertySource);
	}
	
	public Properties toProperties() {
		Properties properties = System.getProperties();
		if (properties == null) {
			properties = new Properties();
		}
		for (PropertySource propertySource: propertySources) {
			properties.setProperty(propertySource.getKey(), propertySource.getValue());
		}
		return properties;
	}
}
