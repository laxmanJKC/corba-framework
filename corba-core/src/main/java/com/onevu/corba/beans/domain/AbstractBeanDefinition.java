package com.onevu.corba.beans.domain;

public abstract class AbstractBeanDefinition implements BeanDefinition, Cloneable {
	
	private volatile Object beanClass;
	
	@Override
	public Object clone() {
		return cloneBeanDefinition();
	}
	
	public abstract AbstractBeanDefinition cloneBeanDefinition();
}
