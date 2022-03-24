package com.onevue.corba.server.nbi;

import org.omg.CORBA.ORB;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.onevue.spring.boot.CorbaApplication;
import com.onevue.spring.configuration.OnevueSpringConfiguration;

@SpringBootApplication(scanBasePackages = { "com.onevue.corba.server.nbi" }, scanBasePackageClasses = {
		OnevueSpringConfiguration.class })
public class NBICorbaApplicationServer {

	public static void main(String[] args) throws BeansException, Exception {
		ConfigurableApplicationContext ctx = CorbaApplication.run(NBICorbaApplicationServer.class, args);
		ORB orb = ctx.getBean(ORB.class);
		System.out.println(orb);
	}
}
