package com.onevu.corba.beans.domain;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.onevu.corba.exception.BeanInstantiationException;
import com.onevu.corba.util.BeanUtils;
import com.onevu.corba.util.ClassUtils;
import com.onevu.corba.util.ReflectionUtils;

public abstract class AbstractBeanDefinition implements BeanDefinition, Cloneable {

	private volatile Object beanClass;

	private String beanClassName;

	private Object source;

	private ConstructorArgumentValues constructorArgumentValues;

	public AbstractBeanDefinition() {
	}

	public AbstractBeanDefinition(ConstructorArgumentValues cargs) {
		setConstructorArgumentValues(cargs);
	}

	public boolean hasConstructorArgumentValues() {
		return (this.constructorArgumentValues != null && !this.constructorArgumentValues.isEmpty()) || !hasDefaultConstructor() || !fetchConstructorArgument().isEmpty();
	}

	public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
		this.constructorArgumentValues = constructorArgumentValues;
	}
	
	public boolean hasDefaultConstructor() {
		Object srcObj = null;
		try {
			srcObj = BeanUtils.instantiateClass((Class)getBeanClass());
		} catch (BeanInstantiationException e) {
			srcObj = null;
		}
		return srcObj != null;
	}

	/**
	 * Return constructor argument values for this bean (never {@code null}).
	 */
	public ConstructorArgumentValues getConstructorArgumentValues() {
		if (this.constructorArgumentValues == null) {
			this.constructorArgumentValues = new ConstructorArgumentValues((Constructor) null);
		}
		return this.constructorArgumentValues;
	}

	public String getBeanClassName() {
		Object beanClassObject = this.beanClass;
		if (beanClassObject instanceof Class) {
			return ((Class<?>) beanClassObject).getName();
		} else {
			return (String) beanClassObject;
		}
	}

	public void setBeanClass(Object beanClass) {
		this.beanClass = beanClass;
	}

	public List<ConstructorArgumentValues> fetchConstructorArgument() {
		List<ConstructorArgumentValues> constructorArgumentValues = new ArrayList<ConstructorArgumentValues>();
		Class clazz = (Class) getBeanClass();
		Constructor[] ctors = clazz.getConstructors();
		for (Constructor ctor : ctors) {
			ReflectionUtils.makeAccessible(ctor);
			Parameter[] parameters = ctor.getParameters();
			if (parameters !=null && parameters.length > 0) {
				ConstructorArgumentValues argumentValues = new ConstructorArgumentValues(ctor);
				for (Parameter parameter : parameters) {
					argumentValues.addIndexedArgumentValue(0, parameter.getType());
				}
				constructorArgumentValues.add(argumentValues);
			}
		}
		Collections.sort(constructorArgumentValues, new ConstructorArgumentValues((Constructor)null));
		return constructorArgumentValues;
	}

	public void setSource(Object source) {
		this.source = source;
	}

	@Override
	public Object getSource() {
		return this.source;
	}

	public Object getBeanClass() {
		return beanClass;
	}

	@Override
	public void setBeanClassName(String beanClassName) {
		this.beanClassName = beanClassName;
	}

	public Class<?> resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException {
		String className = getBeanClassName();
		if (className == null) {
			return null;
		}
		Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
		this.beanClass = resolvedClass;
		return resolvedClass;
	}

	public boolean hasBeanClass() {
		return (this.beanClass instanceof Class);
	}

	@Override
	public Object clone() {
		return cloneBeanDefinition();
	}

	public abstract AbstractBeanDefinition cloneBeanDefinition();
}
