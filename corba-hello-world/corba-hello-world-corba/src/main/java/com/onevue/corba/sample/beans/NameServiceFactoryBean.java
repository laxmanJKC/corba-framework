package com.onevue.corba.sample.beans;

import static com.onevue.spring.constants.CorbaConstants.CORBA_NAME_SERVICE;

import org.omg.CORBA.ORB;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.onevue.spring.model.NameServiceBean;
import com.onevue.spring.model.ORBBean;

@Service(CORBA_NAME_SERVICE)
public class NameServiceFactoryBean implements InitializingBean, FactoryBean<NameServiceBean> {

	private ORB orb;

	public NameServiceFactoryBean(ORBBean orbBean) {
		this.orb = orbBean.getOrb();
	}

	@Override
	public NameServiceBean getObject() throws Exception {
		return new NameServiceBean(orb);
	}

	@Override
	public Class<?> getObjectType() {
		return NameServiceBean.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(orb, "ORB Bean must not be null");
	}

}
