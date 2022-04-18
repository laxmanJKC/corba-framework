package com.helloworld;

import org.omg.PortableServer.Servant;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import HelloApp.HelloOperations;

@Configuration
@ComponentScan(basePackages = { "com.helloworld", "HelloApp" }, includeFilters = {
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = { Servant.class, HelloOperations.class }) })
public class HelloWorldCorbaConfiguration {

}
