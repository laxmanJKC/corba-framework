package com.onevue.corba.sample.service;

import org.springframework.stereotype.Service;

import com.onevue.spring.model.NameServiceBean;

import HelloApp.Hello;
import HelloApp.HelloHelper;

@Service
public class HelloServiceImpl {

	private final NameServiceBean nameServiceBean;

	public HelloServiceImpl(NameServiceBean nameServiceBean) {
		this.nameServiceBean = nameServiceBean;
	}

	public String sayHello() {
		String name = "hello_BindRef";
		Hello helloImpl = HelloHelper.narrow(this.nameServiceBean.resolve_str(name));
		return helloImpl.sayHello();
	}
}
