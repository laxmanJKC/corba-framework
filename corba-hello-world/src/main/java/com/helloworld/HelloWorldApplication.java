package com.helloworld;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.PropertySource;

import com.onevue.spring.boot.CorbaApplication;
import com.onevue.spring.configuration.OnevueSpringConfiguration;
import com.onevue.spring.configuration.annotation.EnableCorbaServant;

import HelloApp.HelloPOATie;

@PropertySource(value = "application.properties", ignoreResourceNotFound = true)
@EnableCorbaServant(basePackages = {"HelloApp", "com.helloworld"})
@SpringBootApplication(scanBasePackageClasses = {OnevueSpringConfiguration.class})
public class HelloWorldApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = CorbaApplication.run(HelloWorldApplication.class, args);
		System.out.println(context.getBean(HelloPOATie.class));
	}
}
