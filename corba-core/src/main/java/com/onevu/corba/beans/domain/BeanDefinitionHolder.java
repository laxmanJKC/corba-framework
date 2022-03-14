package com.onevu.corba.beans.domain;

import com.onevu.corba.util.Assert;

public class BeanDefinitionHolder {

	private final BeanDefinition beanDefinition;

	private final String beanName;
	
	public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");
		Assert.notNull(beanName, "Bean name must not be null");
		this.beanDefinition = beanDefinition;
		this.beanName = beanName;
	}
	
	/**
	 * Return the wrapped BeanDefinition.
	 */
	public BeanDefinition getBeanDefinition() {
		return this.beanDefinition;
	}

	/**
	 * Return the primary name of the bean, as specified for the bean definition.
	 */
	public String getBeanName() {
		return this.beanName;
	}
	
	public boolean matchesName(String candidateName) {
		return candidateName != null && candidateName.equals(this.beanName);
	}
	
	public Object getSource() {
		return this.beanDefinition.getSource();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BeanDefinitionHolder)) {
			return false;
		}
		BeanDefinitionHolder otherHolder = (BeanDefinitionHolder) other;
		return this.beanDefinition.equals(otherHolder.beanDefinition) &&
				this.beanName.equals(otherHolder.beanName);
	}

	@Override
	public int hashCode() {
		int hashCode = this.beanDefinition.hashCode();
		hashCode = 29 * hashCode + this.beanName.hashCode();
		return hashCode;
	}
}
