package com.onevue.spring.configuration;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

import com.onevue.spring.configuration.annotation.EnableCorbaServant;

public class CorbaServantRegistry extends AbstractCorbaBeanRegistry {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(getAnnotation().getName());
		
	}

	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableCorbaServant.class;
	}

}
