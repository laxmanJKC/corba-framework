package com.onevue.spring.configuration;

import java.lang.annotation.Annotation;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;

public abstract class AbstractCorbaBeanRegistry implements ImportBeanDefinitionRegistrar, EnvironmentAware {
	
	private Environment environment;
	
	protected abstract Class<? extends Annotation> getAnnotation();

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
