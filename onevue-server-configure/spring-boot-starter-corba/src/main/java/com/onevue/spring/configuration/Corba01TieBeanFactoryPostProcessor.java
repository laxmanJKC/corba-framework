package com.onevue.spring.configuration;

import static com.onevue.spring.constants.CorbaConstants.CORBA_OBJECT_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_BEAN;
import static com.onevue.spring.constants.CorbaConstants.CORBA_POA_IMPL_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_POA_OPERATIONS_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_POA_TIE_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_GLASSFISH_TIE_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ROOT_POA;

import java.rmi.Remote;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import com.onevue.spring.beans.OrbBean;
import com.onevue.spring.beans.factory.CorbaObjectByServantFactoryBean;
import com.onevue.spring.model.CorbaBindingProperty;


@Configuration
public class Corba01TieBeanFactoryPostProcessor
		implements BeanPostProcessor, ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	private BeanDefinitionRegistry registry;

	public void configure() throws Exception {
		AnnotationConfigApplicationContext context = (AnnotationConfigApplicationContext) applicationContext;
		String beanNames[] = registry.getBeanDefinitionNames();
		List<String> corbaBeanNames = uniqueBeanName(beanNames);
		OrbBean orbBean = applicationContext.getBean(CORBA_ORB_BEAN, OrbBean.class);
		ORB orb = orbBean.getOrb();
		POA rootPOA = applicationContext.getBean(CORBA_ROOT_POA, POA.class);
		OnevueCorbaProperties corbaProperties = applicationContext.getBean(OnevueCorbaProperties.class);
		List<CorbaBindingProperty> bindingProperties = corbaProperties.getBindingProperties();
		for (String beanName : corbaBeanNames) {
			if (beanName.endsWith(CORBA_POA_OPERATIONS_SUFFIX)) {
				CorbaObjectByServantFactoryBean corbaObjectRef = new CorbaObjectByServantFactoryBean(context, orb, rootPOA);
				corbaObjectRef.prepareForOperations(beanName, context);
				corbaObjectRef.afterPropertiesSet();
			} else if (beanName.startsWith("_") && beanName.endsWith(CORBA_GLASSFISH_TIE_SUFFIX)) {
				String tieName = StringUtils.uncapitalize(beanName.substring(1, beanName.length()));
				String name = StringUtils.replace(tieName, CORBA_GLASSFISH_TIE_SUFFIX, "");
				CorbaObjectByServantFactoryBean corbaObjectRef = new CorbaObjectByServantFactoryBean(context, orb, rootPOA);
				Optional<CorbaBindingProperty> bindingPropertyOpt = bindingProperties.stream().filter(p -> name.equals(p.getBeanName())).findFirst();
				if (bindingPropertyOpt.isPresent()) {
					corbaObjectRef.prepareFromRemote(context, name, bindingPropertyOpt.get());
				}
				corbaObjectRef.afterPropertiesSet();
			} else if (beanName.endsWith(CORBA_GLASSFISH_TIE_SUFFIX)) {
				String servantBeanName = StringUtils.replace(beanName, CORBA_GLASSFISH_TIE_SUFFIX, CORBA_POA_IMPL_SUFFIX);
				Remote servant = applicationContext.getBean(servantBeanName, Remote.class);
				String corbaRef = StringUtils.replace(beanName, CORBA_GLASSFISH_TIE_SUFFIX, CORBA_OBJECT_SUFFIX);
				//context.registerBean(corbaRef, Object.class, () -> Util.getTie(servant));
			} else if (beanName.endsWith(CORBA_POA_TIE_SUFFIX)) {
				CorbaObjectByServantFactoryBean corbaObjectRef = new CorbaObjectByServantFactoryBean(context, orb, rootPOA);
				corbaObjectRef.prepareForTie(beanName, context);
				corbaObjectRef.afterPropertiesSet();
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
	
	private List<String> uniqueBeanName(String[] beanNames)  {
		if (beanNames==null || beanNames.length < 1) {
			return Collections.emptyList();
		}
		List<String> uniqueBeanNames = new ArrayList<String>();
		List<String> listBeanNames = Arrays.asList(beanNames);
		for (String beanName: beanNames) {
			if ((beanName.endsWith(CORBA_POA_IMPL_SUFFIX) && listBeanNames.stream().anyMatch(p -> p.endsWith(CORBA_POA_TIE_SUFFIX))) || (beanName.endsWith(CORBA_GLASSFISH_TIE_SUFFIX) && listBeanNames.stream().anyMatch(p -> p.endsWith(CORBA_POA_OPERATIONS_SUFFIX))) || (beanName.endsWith(CORBA_POA_IMPL_SUFFIX) && listBeanNames.stream().anyMatch(p -> p.startsWith("_") && p.endsWith(CORBA_GLASSFISH_TIE_SUFFIX)))) {
				continue;
			} else if (beanName.endsWith(CORBA_POA_IMPL_SUFFIX) || beanName.endsWith(CORBA_GLASSFISH_TIE_SUFFIX) || beanName.endsWith(CORBA_POA_TIE_SUFFIX) || beanName.endsWith(CORBA_POA_OPERATIONS_SUFFIX)) {
				uniqueBeanNames.add(beanName);
			}
		}
		return uniqueBeanNames;
	}
}
