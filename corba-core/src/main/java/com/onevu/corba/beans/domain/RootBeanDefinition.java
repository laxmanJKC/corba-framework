package com.onevu.corba.beans.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class RootBeanDefinition extends AbstractBeanDefinition {
	
	private String parentName;
	
	private boolean autowireCandidate;
	
	private boolean lazyInit;
	
	
	
	public RootBeanDefinition() {
	}

	public RootBeanDefinition(Class<?> beanClass) {
		setBeanClass(beanClass);
	}
	
	public <T> RootBeanDefinition(Class<T> beanClass, Object source) {
		this(beanClass);
		setSource(source);
	}
		
	public RootBeanDefinition(Class<?> beanClass, ConstructorArgumentValues cargs) {
		super(cargs);
		setBeanClass(beanClass);
	}

	@Override
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	@Override
	public String getParentName() {
		return this.parentName;
	}

	@Override
	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	@Override
	public boolean isLazyInit() {
		return this.lazyInit;
	}

	@Override
	public void setAutowireCandidate(boolean autowireCandidate) {
		this.autowireCandidate = autowireCandidate;
	}

	@Override
	public boolean isAutowireCandidate() {
		return this.autowireCandidate;
	}

	@Override
	public boolean isAbstract() {
		if (hasBeanClass()) {
			Class clazz = (Class) getBeanClass();
			return Modifier.isAbstract(clazz.getModifiers());
		}
		return false;
	}
	
	@Override
	public AbstractBeanDefinition cloneBeanDefinition() {
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
		rootBeanDefinition.setBeanClassName(getBeanClassName());
		rootBeanDefinition.setParentName(getParentName());
		rootBeanDefinition.setBeanClass(getBeanClass());
		rootBeanDefinition.setConstructorArgumentValues(getConstructorArgumentValues());
		rootBeanDefinition.setAutowireCandidate(isAutowireCandidate());
		rootBeanDefinition.setSource(getSource());
		rootBeanDefinition.setLazyInit(isLazyInit());
		return rootBeanDefinition; 
	}
}
