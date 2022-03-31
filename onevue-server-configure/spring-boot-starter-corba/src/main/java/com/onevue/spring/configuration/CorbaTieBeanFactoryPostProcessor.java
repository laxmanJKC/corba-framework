package com.onevue.spring.configuration;

import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_BEAN;
import static com.onevue.spring.constants.CorbaConstants.CORBA_POA_IMPL_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_OBJECT_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_POA_TIE_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ROOT_POA;

import static com.onevue.spring.util.OnevueSpringExpressionUtils.corbaObjectByTie;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class CorbaTieBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String beanNames [] = beanFactory.getBeanDefinitionNames();
		ORB orb = beanFactory.getBean(CORBA_ORB_BEAN, ORB.class);
		BeanDefinition rootPOABeanDefinition = beanFactory.getBeanDefinition(CORBA_ROOT_POA);
		rootPOABeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, orb);
		for (String beanName: beanNames) {
			if (beanName.endsWith(CORBA_POA_TIE_SUFFIX)) {
				String servantBeanName = StringUtils.replace(beanName, CORBA_POA_TIE_SUFFIX, CORBA_POA_IMPL_SUFFIX);
				Servant servant = beanFactory.getBean(servantBeanName, Servant.class);
				POA rootPOA = beanFactory.getBean(CORBA_ROOT_POA, POA.class);
				BeanDefinition poaTieBeanDefinition = beanFactory.getBeanDefinition(beanName);
				poaTieBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, servant);
				poaTieBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, rootPOA);
				
				Servant servantTie = beanFactory.getBean(beanName, Servant.class);
				Object corbaObjRef = corbaObjectByTie(servantTie, orb);
				String corbaRef = StringUtils.replace(beanName, CORBA_POA_TIE_SUFFIX, "") + CORBA_OBJECT_SUFFIX;
				beanFactory.registerSingleton(corbaRef, corbaObjRef);
			}
		}
	}

}
