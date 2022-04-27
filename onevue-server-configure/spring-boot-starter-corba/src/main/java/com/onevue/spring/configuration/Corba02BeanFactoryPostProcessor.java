package com.onevue.spring.configuration;

import static com.onevue.spring.constants.CorbaConstants.CORBA_INITIAL_CONTEXT;
import static com.onevue.spring.constants.CorbaConstants.CORBA_NAME_SERVICE;
import static com.onevue.spring.constants.CorbaConstants.CORBA_OBJECT_SUFFIX;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.naming.Context;

import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import com.onevue.spring.enums.CorbaRegistry;
import com.onevue.spring.model.CorbaBindingProperty;
import com.onevue.spring.util.CorbaBindUtils;

@Configuration
public class Corba02BeanFactoryPostProcessor implements BeanPostProcessor, ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	private BeanDefinitionRegistry registry;

	public void configure() {
		String beanNames[] = applicationContext.getBeanDefinitionNames();
		List<String> bindingBeans = Arrays.asList(beanNames).stream().filter(p -> p.endsWith(CORBA_OBJECT_SUFFIX))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(bindingBeans)) {
			return;
		}
		OnevueCorbaProperties onevueCorbaProperties = applicationContext.getBean(OnevueCorbaProperties.class);
		List<CorbaBindingProperty> corbaBindingProperties = onevueCorbaProperties.getBindingProperties();

		Supplier<Object> supplierObjRef = null;

		switch (onevueCorbaProperties.getCorbaRegistry()) {
		case CONTEXT:
			Context contextRef = applicationContext.getBean(CORBA_INITIAL_CONTEXT, Context.class);
			supplierObjRef = () -> contextRef;
			break;

		default:
			NamingContextExt namingContextExtRef = applicationContext.getBean(CORBA_NAME_SERVICE, NamingContextExt.class); 
			supplierObjRef = () -> namingContextExtRef;
			break;
		}

		for (String bindingBean : bindingBeans) {
			org.omg.CORBA.Object corbaObjRef = applicationContext.getBean(bindingBean, org.omg.CORBA.Object.class);
			Optional<CorbaBindingProperty> corbaBindPropertyOpt = corbaBindingProperties.stream()
					.filter(p -> p.getCorbaBeanRef() != null && p.getCorbaBeanRef().equals(bindingBean)).findFirst();
			NameComponent[] nameComponents = null;
			try {
				if (corbaBindPropertyOpt.isPresent()
						&& CorbaRegistry.CONTEXT == onevueCorbaProperties.getCorbaRegistry()) {
					Context context = (Context) supplierObjRef.get();
					CorbaBindingProperty bindingProperty = corbaBindPropertyOpt.get();
					CorbaBindUtils.contextBinding(context, bindingProperty, corbaObjRef);
				} else if (CorbaRegistry.NAMING_CONTEXT == onevueCorbaProperties.getCorbaRegistry()) {
					NamingContextExt namingContextExt = (NamingContextExt) supplierObjRef.get();
					if (corbaBindPropertyOpt.isEmpty() || (corbaBindPropertyOpt.isPresent()
							&& ("to_name".equalsIgnoreCase(corbaBindPropertyOpt.get().getNameComponent())
									|| corbaBindPropertyOpt.get().getExpression() == null))) {
						nameComponents = namingContextExt.to_name(bindingBean);
					}
					if (nameComponents == null && corbaObjRef == null) {
						return;
					}
					namingContextExt.rebind(nameComponents, corbaObjRef);
				}
			} catch (NotFound e) {
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
	public void afterPropertiesSet() throws Exception {
		this.registry = (BeanDefinitionRegistry) this.applicationContext.getAutowireCapableBeanFactory();
		configure();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
