package com.onevue.spring.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.onevue.spring.model.ORBBean;

public class ORBFactoryBean implements InitializingBean, FactoryBean<ORBBean> {

	@Override
	public ORBBean getObject() throws Exception {
		return null;
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}
}
