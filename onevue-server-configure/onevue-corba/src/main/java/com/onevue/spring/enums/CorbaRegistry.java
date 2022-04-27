package com.onevue.spring.enums;

public enum CorbaRegistry {
	
	CONTEXT("context"),
	NAMING_CONTEXT("naming-context");
	
	private final String corbaRegistry;
	
	private CorbaRegistry(String corbaRegistry) {
		this.corbaRegistry = corbaRegistry;
	}

	public String getCorbaRegistry() {
		return corbaRegistry;
	}
}
