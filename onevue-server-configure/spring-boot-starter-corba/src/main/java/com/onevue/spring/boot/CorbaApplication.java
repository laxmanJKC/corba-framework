package com.onevue.spring.boot;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.omg.CORBA.ORB;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;

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
		ORB orb = ctx.getBean(ORB.class);
		orb.run();
		return ctx;
	}
}
