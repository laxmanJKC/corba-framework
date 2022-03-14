package com.onevu.corba.beans.domain;

public class RootBeanDefinition extends AbstractBeanDefinition {
	
	private String beanClassName;
	
	private Object source;
	
	private boolean isAbstract;
	
	private boolean autowireCandidate;

	public RootBeanDefinition(String beanClassName) {
		this.beanClassName = beanClassName;
	}
	
	public RootBeanDefinition(String beanClassName, Object source) {
		this.beanClassName = beanClassName;
		this.source = source;
	}

	@Override
	public void setParentName(String parentName) {
	}

	@Override
	public String getParentName() {
		return null;
	}

	@Override
	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}

	@Override
	public String getBeanClassName() {
		return this.beanClassName;
	}

	@Override
	public void setLazyInit(boolean lazyInit) {
	}

	@Override
	public boolean isLazyInit() {
		return false;
	}

	@Override
	public void setAutowireCandidate(boolean autowireCandidate) {
		this.autowireCandidate = autowireCandidate;
	}

	@Override
	public boolean isAutowireCandidate() {
		return this.autowireCandidate;
	}
	
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
	}
	
	public void setSource(Object source) {
		this.source = source;
	}

	@Override
	public Object getSource() {
		return this.source;
	}

	@Override
	public AbstractBeanDefinition cloneBeanDefinition() {
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(beanClassName);
		rootBeanDefinition.setAutowireCandidate(isAutowireCandidate());
		rootBeanDefinition.setSource(getSource());
		rootBeanDefinition.setAbstract(isAbstract());
		return rootBeanDefinition; 
	}

}
