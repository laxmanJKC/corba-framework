package com.onevue.corba.sample.event;

import static com.onevue.spring.constants.CorbaConstants.CORBA_NAME_SERVICE;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ORB_BEAN;
import static com.onevue.spring.constants.CorbaConstants.CORBA_ROOT_POA;

import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.onevue.spring.exception.OnevuCorbaException;
import com.onevue.spring.handler.Handlers;
import com.onevue.spring.model.NameServiceBean;
import com.onevue.spring.model.ORBBean;

@Order(value = Ordered.LOWEST_PRECEDENCE)
@Component
public class CorbaStartUpEvent implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CorbaStartUpEvent.class);

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		ConfigurableApplicationContext ctx = event.getApplicationContext();
		LOGGER.info("Corba Server [{}] has been STARTING...", ctx.getApplicationName());
		ORBBean orb = ctx.getBean(CORBA_ORB_BEAN, ORBBean.class);
		POA rootPOA = ctx.getBean(CORBA_ROOT_POA, POA.class);
		NameServiceBean nameServiceBean = ctx.getBean(CORBA_NAME_SERVICE, NameServiceBean.class);
		try {
			rootPOA.the_POAManager().activate();
		} catch (AdapterInactive e) {
			LOGGER.error("Corba Application [{}] has been processing to start application", ctx.getApplicationName(),
					e);
			throw new OnevuCorbaException("Activation of RootPOA failed as AdapterInactive " + e.getMessage());
		}
		Handlers.addHandler(nameServiceBean).startHandler();
		Handlers.addHandler(orb).startHandler();;
	}

}
