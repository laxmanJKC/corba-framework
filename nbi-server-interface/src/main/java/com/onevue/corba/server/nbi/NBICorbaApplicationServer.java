package com.onevue.corba.server.nbi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.onevue.spring.configuration.OnevueSpringConfiguration;

@SpringBootApplication(scanBasePackages = { "com.onevue.corba.server.nbi" }, scanBasePackageClasses = {
		OnevueSpringConfiguration.class })
public class NBICorbaApplicationServer {

	public static void main(String[] args) {
		SpringApplication.run(NBICorbaApplicationServer.class, args);
	}
}
