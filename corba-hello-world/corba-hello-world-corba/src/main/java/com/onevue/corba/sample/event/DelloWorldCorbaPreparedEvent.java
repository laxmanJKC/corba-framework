package com.onevue.corba.sample.event;

import static com.onevue.spring.constants.CorbaConstants.CORBA_NAME_SERVICE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.onevue.corba.sample.configuration.HelloImpl;
import com.onevue.spring.model.NameServiceBean;

import HelloApp.Hello_Tie;

@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class DelloWorldCorbaPreparedEvent implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DelloWorldCorbaPreparedEvent.class);

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOGGER.info("HelloWorld Corba Prepared Event STARTED Registered ...!! ");
		ConfigurableApplicationContext ctx = event.getApplicationContext();
		NameServiceBean objRef = ctx.getBean(CORBA_NAME_SERVICE, NameServiceBean.class);

		HelloImpl helloImpl = ctx.getBean("helloImpl", HelloImpl.class);
		org.omg.CORBA.Object helloRef = new Hello_Tie(helloImpl);

		// bind the Object Reference in Naming
		String name = "hello_BindRef";
		objRef.addNamingAction("to_name", name, helloRef);
		LOGGER.info("HelloWorld Corba Prepared Event COMPLETED Registered ...!! ");
	}

}
