package com.onevue.corba.sample.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.onevue.spring.model.ORBBean;

@Configuration
public class HelloServiceConfiguration {
	
	@Bean(value = "helloImpl")
	public HelloImpl helloImpl(ORBBean orbBean) {
		HelloImpl helloImpl = new HelloImpl();
		helloImpl.setORB(orbBean.getOrb());
		return helloImpl;
	}
	
}
