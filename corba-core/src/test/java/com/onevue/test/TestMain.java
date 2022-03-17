package com.onevue.test;

import java.io.IOException;

import com.onevu.corba.exception.BeanDefinitionStoreException;
import com.onevu.corba.exception.BeanInstantiationException;
import com.onevu.corba.exception.NoSuchBeanDefinitionException;
import com.onevu.corba.factory.annotation.ClassPathScanningCandidateComponentProvider;
import com.onevu.corba.factory.support.DefaultListableBeanFactory;

public class TestMain {

	public static void main(String[] args) throws IOException, ClassNotFoundException, BeanDefinitionStoreException, BeanInstantiationException, NoSuchBeanDefinitionException {
		ClassPathScanningCandidateComponentProvider candidateComponentProvider = new ClassPathScanningCandidateComponentProvider(new DefaultListableBeanFactory(), true);
		//org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider candidateComponentProvider = new org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider(false);
		//candidateComponentProvider.addIncludeFilter(new AnnotationTypeFilter(Component.class));
		candidateComponentProvider.scan("com.onevue.test");
		candidateComponentProvider.refresh();
		//Set<org.springframework.beans.factory.config.BeanDefinition> beanDefinitions = candidateComponentProvider.findCandidateComponents("com.onevue.test");
		System.out.println(candidateComponentProvider.getRegistry());
	}

}
