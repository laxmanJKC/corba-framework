package com.onevu.corba.boot;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.onevu.corba.beans.domain.AbstractBeanDefinition;
import com.onevu.corba.beans.domain.RootBeanDefinition;
import com.onevu.corba.exception.BeanDefinitionStoreException;
import com.onevu.corba.exception.BeanInstantiationException;
import com.onevu.corba.exception.NoSuchBeanDefinitionException;
import com.onevu.corba.exception.OnevuCorbaException;
import com.onevu.corba.factory.annotation.ClassPathScanningCandidateComponentProvider;
import com.onevu.corba.factory.support.BeanDefinitionRegistry;
import com.onevu.corba.io.PropertySources;
import com.onevu.corba.util.Assert;

public class CorbaApplication {
	
	private static PropertySources doMain(Class<?> clazz, String ...args) {
		PropertySources propertySources = new PropertySources();
		if (args == null || args.length ==0) {
			return propertySources;
		}
		Properties properties = new Properties();
		for (String arg: args) {
			if (arg.startsWith("corba.properties") || arg.startsWith("-Dcorba.properties")) {
				String []value = arg.split("=");
				InputStream inputStream;
				try {
					inputStream = new FileInputStream(value[1]);
					properties.load(inputStream);
				} catch (FileNotFoundException e) {
					throw new OnevuCorbaException("No configuration file");
				} catch (IOException e) {
					throw new OnevuCorbaException("Incorrect Configuration File");
				}
				Set<Entry<Object, Object>> property = properties.entrySet();
				for (Entry<Object, Object> entry: property) {
					propertySources.addProperty((String)entry.getKey(), (String)entry.getValue());
				}
			}
		}
		return propertySources;
	}
	
	public static BeanDefinitionRegistry run(Class<?> clazz, String ...args) throws Exception {
		Assert.notNull(clazz, "Class Name not found");
		PropertySources propertySources = doMain(clazz, args);
		String packageName = clazz.getPackage().getName();
		ClassPathScanningCandidateComponentProvider candidateComponentProvider = new ClassPathScanningCandidateComponentProvider();
		BeanDefinitionRegistry beanDefinitionRegistry = candidateComponentProvider.getRegistry();
		AbstractBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(propertySources.getClass());
		beanDefinition.setSource(propertySources);
		beanDefinitionRegistry.registerBeanDefinition("environment-properties", beanDefinition);
		try {
			candidateComponentProvider.scan(packageName);
			candidateComponentProvider.refresh();
		} catch (ClassNotFoundException | IOException | BeanDefinitionStoreException e) {
			throw e;
		} catch (BeanInstantiationException e) {
			throw e;
		} catch (NoSuchBeanDefinitionException e) {
			throw e;
		}
		return candidateComponentProvider.getRegistry();
		
	}
}
