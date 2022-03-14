package com.onevu.corba.exception;

import java.lang.reflect.Constructor;

public class BeanInstantiationException extends Exception {

	private Class<?> beanClass;

	public <T> BeanInstantiationException(Class<T> clazz, String string) {
		// TODO Auto-generated constructor stub
	}

	public BeanInstantiationException(Class<?> beanClass, String msg, Throwable cause) {
		super("Could not instantiate bean class [" + beanClass.getName() + "]: " + msg, cause);
		this.beanClass = beanClass;
	}

	public <T> BeanInstantiationException(Constructor<T> ctor, String string, Throwable ex) {
		// TODO Auto-generated constructor stub
	}

}
