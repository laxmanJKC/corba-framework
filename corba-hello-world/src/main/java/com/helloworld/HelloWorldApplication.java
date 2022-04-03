package com.helloworld;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.onevue.spring.beans.factory.ORBFactoryBean;
import com.onevue.spring.boot.CorbaApplication;
import com.onevue.spring.configuration.OnevueCorbaProperties;
import com.onevue.spring.configuration.OnevueSpringConfiguration;
import com.onevue.spring.configuration.annotation.EnableCorbaServant;

import HelloApp.HelloPOATie;

//@EnableConfigurationProperties(OnevueCorbaProperties.class)
@SpringBootApplication(scanBasePackages = {"com.helloworld"}, scanBasePackageClasses = OnevueSpringConfiguration.class)
public class HelloWorldApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(HelloWorldApplication.class, args);
		Environment environment = context.getEnvironment();
		System.out.println(context.getBean(ORBFactoryBean.class));
	}
}

