package com.onevue.test;

import com.onevu.corba.boot.CorbaApplication;
import com.onevu.corba.factory.support.BeanDefinitionRegistry;

public class HelloWorld {

	public static void main(String[] args) throws Exception {
		BeanDefinitionRegistry registry = CorbaApplication.run(HelloWorld.class, args);
		System.out.println(registry.getBeanDefinitionCount());
	}
}
