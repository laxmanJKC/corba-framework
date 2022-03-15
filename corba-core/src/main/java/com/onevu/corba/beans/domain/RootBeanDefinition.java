package com.onevu.corba.beans.domain;

public class RootBeanDefinition extends AbstractBeanDefinition {
	
	private String parentName;
	
	private String beanClassName;
	
	private Object source;
	
	private boolean isAbstract;
	
	private boolean autowireCandidate;
	
	private boolean lazyInit;

	public RootBeanDefinition(String beanClassName) {
		this.beanClassName = beanClassName;
	}
	
	public RootBeanDefinition(String beanClassName, Object source) {
		this.beanClassName = beanClassName;
		this.source = source;
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
	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}

	@Override
	public String getBeanClassName() {
		return this.beanClassName;
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
		RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(getBeanClassName());
		rootBeanDefinition.setParentName(getParentName());
		rootBeanDefinition.setAutowireCandidate(isAutowireCandidate());
		rootBeanDefinition.setSource(getSource());
		rootBeanDefinition.setAbstract(isAbstract());
		rootBeanDefinition.setLazyInit(isLazyInit());
		return rootBeanDefinition; 
	}

}
