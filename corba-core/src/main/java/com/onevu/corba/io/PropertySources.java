package com.onevu.corba.io;

import java.util.ArrayList;
import java.util.List;

public class PropertySources {
	private List<PropertySource> propertySources = new ArrayList<PropertySource>();
	
	public void addProperty(String key, String value) {
		PropertySource propertySource = new PropertySource(key, value);
		propertySources.add(propertySource);
	}
}
