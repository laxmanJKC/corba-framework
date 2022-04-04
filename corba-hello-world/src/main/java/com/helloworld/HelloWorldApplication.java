package com.helloworld;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.onevue.spring.boot.CorbaApplication;
import com.onevue.spring.configuration.OnevueSpringConfiguration;
import com.onevue.spring.configuration.annotation.EnableCorbaServant;

@EnableCorbaServant(basePackages = {"HelloApp", "com.helloworld"})
@SpringBootApplication(scanBasePackages = {
		"com.helloworld" }, scanBasePackageClasses = OnevueSpringConfiguration.class)
public class HelloWorldApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = CorbaApplication.run(HelloWorldApplication.class, args);
	}
}
