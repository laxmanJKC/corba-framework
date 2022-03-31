package com.onevue.spring.boot;

import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_BEAN;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ROOT_POA;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

import com.onevue.spring.exception.OnevuCorbaException;

public class CorbaApplication {

	private Set<Class<?>> primarySources;

	public CorbaApplication(Class<?>... primarySources) {
		Assert.notNull(primarySources, "PrimarySources must not be null");
		this.primarySources = new LinkedHashSet<>(Arrays.asList(primarySources));
	}

	public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
		return run(new Class<?>[] { primarySource }, args);
	}

	public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
		return new CorbaApplication(primarySources).run(args);
	}

	private ConfigurableApplicationContext run(String... args) {
		Class<?>[] primaryClazz = primarySources.toArray(new Class<?>[primarySources.size()]);
		ConfigurableApplicationContext ctx = SpringApplication.run(primaryClazz, args);
		// ctx.addBeanFactoryPostProcessor(new CorbaBindingBeanFactoryPostProcessor());
		// ctx.addBeanFactoryPostProcessor(new CorbaTieBeanFactoryPostProcessor());
		ORB orb = ctx.getBean(CORBA_ORB_BEAN, ORB.class);
		POA rootPOA = ctx.getBean(CORBA_ROOT_POA, POA.class);
		try {
			rootPOA.the_POAManager().activate();
		} catch (AdapterInactive e) {
			throw new OnevuCorbaException("Activation of RootPOA failed as AdapterInactive " + e.getMessage());
		}
		orb.run();
		System.out.println("This Corba Application");
		return ctx;
	}
}
