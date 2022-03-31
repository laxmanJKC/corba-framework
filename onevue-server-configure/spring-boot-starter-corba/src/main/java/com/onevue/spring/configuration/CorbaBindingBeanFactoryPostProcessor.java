package com.onevue.spring.configuration;

import static com.onevue.spring.constants.CorbaConstants.CORBA_NAME_SERVICE;
import static com.onevue.spring.constants.CorbaConstants.CORBA_OBJECT_SUFFIX;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_BEAN;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ROOT_POA;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

public class CorbaBindingBeanFactoryPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware {

	private Environment environment;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String beanNames[] = beanFactory.getBeanDefinitionNames();
		List<String> bindingBeans = Arrays.asList(beanNames).stream().filter(p -> p.endsWith(CORBA_OBJECT_SUFFIX))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(bindingBeans)) {
			return;
		}
		OnevueCorbaProperties onevueCorbaProperties = beanFactory.getBean(OnevueCorbaProperties.class);
		List<CorbaBindingProperty> corbaBindingProperties = onevueCorbaProperties.getBindingProperties();
		ORB orb = beanFactory.getBean(CORBA_ORB_BEAN, ORB.class);
		POA rootPOA = beanFactory.getBean(CORBA_ROOT_POA, POA.class);
		NamingContextExt namingContextExt = beanFactory.getBean(CORBA_NAME_SERVICE, NamingContextExt.class);
		for (String bindingBean : bindingBeans) {
			org.omg.CORBA.Object corbaObjRef = beanFactory.getBean(bindingBean, org.omg.CORBA.Object.class);
			Optional<CorbaBindingProperty> corbaBindPropertyOpt = corbaBindingProperties.stream()
					.filter(p -> p.equals(bindingBean)).findFirst();
			NameComponent[] nameComponents = null;
			try {
				if (corbaBindPropertyOpt.isEmpty()
						|| "to_name".equalsIgnoreCase(corbaBindPropertyOpt.get().getNameComponent())
						|| corbaBindPropertyOpt.get().getExpression() == null) {
					nameComponents = namingContextExt.to_name(bindingBean);
				}
				if (nameComponents ==null && corbaObjRef == null) {
					return ;
				}
				namingContextExt.rebind(nameComponents, corbaObjRef);

			} catch (NotFound e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CannotProceed e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidName e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
}
