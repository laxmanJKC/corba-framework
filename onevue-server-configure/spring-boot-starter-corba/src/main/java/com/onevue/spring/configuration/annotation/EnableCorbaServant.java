package com.onevue.spring.configuration.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan.Filter;

import com.onevue.spring.configuration.CorbaServantRegistry;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CorbaServantRegistry.class)
public @interface EnableCorbaServant {
	
	String[] value() default {};

	String[] basePackages() default {};

	Class<?>[] basePackageClasses() default {};
	
	Filter[] includeFilters() default {};
}
