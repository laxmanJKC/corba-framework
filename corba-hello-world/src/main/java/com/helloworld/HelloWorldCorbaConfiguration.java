package com.helloworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.rmi.PortableRemoteObject;

import org.omg.PortableServer.Servant;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = { "com.helloworld", "HelloApp" }, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {Servant.class, PortableRemoteObject.class})})
public class HelloWorldCorbaConfiguration {
	
	public static final Class<?> [] DEFAULT_ASSIGNABLE_TYPE_CLASSES = new Class[] {
			Servant.class,
			PortableRemoteObject.class
	};
	
	public static final Class<?> [] DEFAULT_APPLICATION_ASSIGNABLE_TYPE_CLASSES = new Class[] {
	};
	
	public static final List<Class<?>> ACTIVE_ASSIGNABLE_TYPE_CLASSES_LIST = new ArrayList<Class<?>>();
	static {
		ACTIVE_ASSIGNABLE_TYPE_CLASSES_LIST.addAll(Arrays.asList(DEFAULT_ASSIGNABLE_TYPE_CLASSES));
		ACTIVE_ASSIGNABLE_TYPE_CLASSES_LIST.addAll(Arrays.asList(DEFAULT_APPLICATION_ASSIGNABLE_TYPE_CLASSES));
	}
	
	public static final Class<?> []ACTIVE_ASSIGNABLE_TYPE_CLASSES = ACTIVE_ASSIGNABLE_TYPE_CLASSES_LIST.toArray(new Class[ACTIVE_ASSIGNABLE_TYPE_CLASSES_LIST.size()]);
}
