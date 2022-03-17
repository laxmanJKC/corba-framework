package com.onevu.corba.boot;

import static com.onevu.corba.constants.CorbaConstants.ENVIRONMENT_KEY;
import static com.onevu.corba.constants.CorbaConstants.CORBA_PROPERTIES;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.onevu.corba.beans.domain.AbstractBeanDefinition;
import com.onevu.corba.beans.domain.BeanDefinition;
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
	
	private PropertySources doMain(Class<?> clazz, String ...args) {
		PropertySources propertySources = new PropertySources();
		if (args == null || args.length ==0) {
			return propertySources;
		}
		Properties properties = new Properties();
		for (String arg: args) {
			if (arg.startsWith(CORBA_PROPERTIES) || arg.startsWith("-D" + CORBA_PROPERTIES)) {
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
		CorbaApplication corbaApplication = new CorbaApplication();
		PropertySources propertySources = corbaApplication.doMain(clazz, args);
		String packageName = clazz.getPackage().getName();
		ClassPathScanningCandidateComponentProvider candidateComponentProvider = new ClassPathScanningCandidateComponentProvider();
		BeanDefinitionRegistry beanDefinitionRegistry = candidateComponentProvider.getRegistry();
		AbstractBeanDefinition beanDefinition = new RootBeanDefinition();
		beanDefinition.setBeanClass(propertySources.getClass());
		beanDefinition.setSource(propertySources);
		beanDefinitionRegistry.registerBeanDefinition(ENVIRONMENT_KEY, beanDefinition);
		try {
			candidateComponentProvider.scan(packageName);
			candidateComponentProvider.refresh();
			corbaApplication.configureCorbaServer(beanDefinitionRegistry);
		} catch (ClassNotFoundException | IOException | BeanDefinitionStoreException e) {
			throw e;
		} catch (BeanInstantiationException e) {
			throw e;
		} catch (NoSuchBeanDefinitionException e) {
			throw e;
		}
		return beanDefinitionRegistry;
	}
	
	private void configureCorbaServer(BeanDefinitionRegistry registry) throws NoSuchBeanDefinitionException {
		BeanDefinition propertySourcesBeanDefinition = registry.getBeanDefinition(ENVIRONMENT_KEY);
		PropertySources propertySources = (PropertySources) propertySourcesBeanDefinition.getSource();
	}
}
