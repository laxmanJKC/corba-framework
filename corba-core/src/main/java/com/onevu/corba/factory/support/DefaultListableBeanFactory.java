package com.onevu.corba.factory.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.onevu.corba.beans.domain.AbstractBeanDefinition;
import com.onevu.corba.beans.domain.BeanDefinition;
import com.onevu.corba.exception.BeanDefinitionStoreException;
import com.onevu.corba.exception.NoSuchBeanDefinitionException;

public class DefaultListableBeanFactory implements BeanDefinitionRegistry {
	
	private Map<String, BeanDefinition> registeredBeanDefinition = new ConcurrentHashMap<>();
	
	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException {
		registeredBeanDefinition.put(beanName, beanDefinition);
	}

	@Override
	public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		registeredBeanDefinition.remove(beanName);
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		return registeredBeanDefinition.get(beanName);
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return registeredBeanDefinition.containsKey(beanName);
	}

	@Override
	public String[] getBeanDefinitionNames() {
		List<String> beanNames = new ArrayList<>();
		for (Entry<String, BeanDefinition> beanEntry:registeredBeanDefinition.entrySet()) {
			beanNames.add(beanEntry.getKey());
		}
		return beanNames.toArray(new String[beanNames.size()]);
	}

	@Override
	public int getBeanDefinitionCount() {
		return registeredBeanDefinition.size();
	}

	@Override
	public boolean isBeanNameInUse(String beanName) {
		return false;
	}

	@Override
	public BeanDefinition getInitializedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		AbstractBeanDefinition abstractBeanDefinition = (AbstractBeanDefinition) getBeanDefinition(beanName);
		return abstractBeanDefinition.getSource() == null ? null : abstractBeanDefinition;
	}

}
