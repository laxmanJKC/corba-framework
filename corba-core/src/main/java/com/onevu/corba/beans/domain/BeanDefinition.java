package com.onevu.corba.beans.domain;

public interface BeanDefinition {
	
	void setParentName(String parentName);

	String getParentName();

	void setBeanClassName(String beanClassName);

	String getBeanClassName();
	
	void setLazyInit(boolean lazyInit);
	
	boolean isLazyInit();
	
	void setAutowireCandidate(boolean autowireCandidate);
	
	boolean isAutowireCandidate();
	
	boolean isAbstract();

	Object getSource();
}
