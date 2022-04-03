package com.onevue.spring.configuration;

import static com.onevue.spring.constants.CorbaConstants.CORBA_OBJECT_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_BEAN;
import static com.onevue.spring.constants.CorbaConstants.CORBA_POA_IMPL_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_POA_TIE_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ROOT_POA;
import static com.onevue.spring.util.OnevueSpringExpressionUtils.corbaObjectByTie;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class Corba01TieBeanFactoryPostProcessor
		implements BeanPostProcessor, ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	private BeanDefinitionRegistry registry;

	public void configure() {
		AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext) applicationContext;
		String beanNames[] = registry.getBeanDefinitionNames();

		ORB orb = applicationContext.getBean(CORBA_ORB_BEAN, ORB.class);

		for (String beanName : beanNames) {
			if (beanName.endsWith(CORBA_POA_TIE_SUFFIX)) {
				String servantBeanName = StringUtils.replace(beanName, CORBA_POA_TIE_SUFFIX, CORBA_POA_IMPL_SUFFIX);
				Servant servant = applicationContext.getBean(servantBeanName, Servant.class);
				POA rootPOA = applicationContext.getBean(CORBA_ROOT_POA, POA.class);
				BeanDefinition poaTieBeanDefinition = registry.getBeanDefinition(beanName);
				poaTieBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, servant);
				poaTieBeanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, rootPOA);

				Servant servantTie = applicationContext.getBean(beanName, Servant.class);
				Object corbaObjRef = corbaObjectByTie(servantTie, orb);
				String corbaRef = StringUtils.replace(beanName, CORBA_POA_TIE_SUFFIX, "") + CORBA_OBJECT_SUFFIX;
				context.registerBean(corbaRef, Object.class, () -> corbaObjRef);
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.registry = (BeanDefinitionRegistry) this.applicationContext.getAutowireCapableBeanFactory();
		configure();
	}

}
