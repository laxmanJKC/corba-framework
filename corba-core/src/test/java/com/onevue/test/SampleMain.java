package com.onevue.test;

import java.lang.reflect.Constructor;

import com.onevu.corba.exception.BeanInstantiationException;
import com.onevu.corba.util.BeanUtils;
import com.onevu.corba.util.ClassUtils;

public class SampleMain {

	public static void main(String[] args) throws ClassNotFoundException, LinkageError, BeanInstantiationException, NoSuchMethodException, SecurityException {
		Class clzA = ClassUtils.forName("com.onevue.test.Abx", ClassUtils.getDefaultClassLoader());
		Class clzP = ClassUtils.forName("com.onevue.test.Pqx", ClassUtils.getDefaultClassLoader());
		
		Object objP = BeanUtils.instantiateClass(clzP);
		Object objA = BeanUtils.instantiateClass(clzA.getConstructor(clzP), objP);
		
		Constructor []ctors = clzA.getConstructors();
		for (Constructor ctor : ctors) {
			System.out.println(ctor.getParameters());
		}
		System.out.println(objA);
	}

}
