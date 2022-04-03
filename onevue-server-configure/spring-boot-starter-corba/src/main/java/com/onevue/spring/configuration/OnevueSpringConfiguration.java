package com.onevue.spring.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "com.onevue.spring" })
//@EnableConfigurationProperties(value = OnevueCorbaProperties.class)
public class OnevueSpringConfiguration {

}
