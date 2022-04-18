package com.onevue.spring.beans.factory;

import static com.onevue.spring.constants.CorbaConstants.CORBA_OBJECT_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_POA_IMPL_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_POA_TIE_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_POA_OPERATIONS_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_GLASSFISH_TIE_SUFFIX;
import static com.onevue.spring.util.OnevueSpringExpressionUtils.corbaObjectByTie;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.onevue.spring.exception.OnevuCorbaException;

public class CorbaObjectByServantFactoryBean implements InitializingBean, BeanDefinitionCustomizer, FactoryBean<Object> {
	
	private final ApplicationContext context;
	
	private final ORB orb;

	private final POA rootPOA;
	
	private Object corbaObjectRef;
	
	private String corbaObjectBeanName;
	
	private Servant servant;
	
	public CorbaObjectByServantFactoryBean(ApplicationContext context, ORB orb, POA rootPOA) {
		this.context = context;
		this.orb = orb;
		this.rootPOA = rootPOA;
	}

	@Override
	public Object getObject() throws Exception {
		return this.corbaObjectRef;
	}
	
	public void prepare(String beanName, BeanDefinitionRegistry registry) {
		this.servant = context.getBean(beanName, Servant.class);		
		
		try {
			this.corbaObjectRef = this.rootPOA.servant_to_reference(this.servant);
		} catch (ServantNotActive | WrongPolicy e) {
			throw new OnevuCorbaException("ServantNotActive");
		}
		this.corbaObjectBeanName = StringUtils.replace(beanName, CORBA_POA_IMPL_SUFFIX, CORBA_OBJECT_SUFFIX);
	}
	
	public void prepareForOperations(String beanName, BeanDefinitionRegistry registry) {
		Object operations = context.getBean(beanName);
		String tieOperationBeanName = StringUtils.replace(beanName, CORBA_POA_OPERATIONS_SUFFIX, CORBA_GLASSFISH_TIE_SUFFIX);
		BeanDefinition beanDefinition = registry.getBeanDefinition(tieOperationBeanName);
		beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, operations);

		this.corbaObjectBeanName = StringUtils.replace(beanName, CORBA_POA_OPERATIONS_SUFFIX, CORBA_OBJECT_SUFFIX);
		this.corbaObjectRef = context.getBean(tieOperationBeanName);
		registry.removeBeanDefinition(tieOperationBeanName);
	}
	
	public void prepareForTie(String beanName, BeanDefinitionRegistry registry) {
		String servantBeanName = StringUtils.replace(beanName, CORBA_POA_TIE_SUFFIX, CORBA_POA_IMPL_SUFFIX);
		this.servant = context.getBean(servantBeanName, Servant.class);
		
		BeanDefinition poaTieBeanDefinition = registry.getBeanDefinition(beanName);
		customize(poaTieBeanDefinition);
		
		Servant servantTie = context.getBean(beanName, Servant.class);
		
		this.corbaObjectRef = corbaObjectByTie(servantTie, orb);
		this.corbaObjectBeanName = StringUtils.replace(beanName, CORBA_POA_TIE_SUFFIX, CORBA_OBJECT_SUFFIX);
		
	}
	
	public void registerCorbaBean() {
		Assert.notNull(corbaObjectRef, "Corba Object reference must not be null.");
		Assert.notNull(corbaObjectBeanName, "Corba Object bean name must not be null.");
		AnnotationConfigApplicationContext applicationContext = (AnnotationConfigApplicationContext) context;
		applicationContext.registerBean(this.corbaObjectBeanName, Object.class, () -> this.corbaObjectRef);
	}
	
	public String getCorbaObjectBeanName() {
		return corbaObjectBeanName;
	}

	@Override
	public Class<?> getObjectType() {
		return Object.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(context, "ApplicationContext can't be null");
		Assert.notNull(rootPOA, "Root POA can't be null");
		registerCorbaBean();
	}

	@Override
	public void customize(BeanDefinition bd) {
		bd.getConstructorArgumentValues().addIndexedArgumentValue(0, servant);
		bd.getConstructorArgumentValues().addIndexedArgumentValue(1, rootPOA);
	}
}
