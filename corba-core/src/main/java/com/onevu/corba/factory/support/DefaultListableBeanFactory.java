package com.onevu.corba.factory.support;

import com.onevu.corba.beans.domain.BeanDefinition;
import com.onevu.corba.exception.BeanDefinitionStoreException;
import com.onevu.corba.exception.NoSuchBeanDefinitionException;

public class DefaultListableBeanFactory implements BeanDefinitionRegistry {

	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getBeanDefinitionNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBeanDefinitionCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isBeanNameInUse(String beanName) {
		// TODO Auto-generated method stub
		return false;
	}

}
