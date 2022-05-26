package com.onevue.corba.sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onevue.corba.sample.service.HelloServiceImpl;

@RestController
public class HelloController {
	
	@Autowired
	private HelloServiceImpl helloServiceImpl;

	@GetMapping("/sayHello")
	public String index() {
		return helloServiceImpl.sayHello();
	}
}