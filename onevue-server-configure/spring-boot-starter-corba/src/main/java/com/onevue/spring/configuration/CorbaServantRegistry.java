package com.onevue.spring.configuration;

import java.lang.annotation.Annotation;

import org.omg.PortableServer.Servant;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import com.onevue.spring.configuration.annotation.EnableCorbaServant;

public class CorbaServantRegistry extends AbstractCorbaBeanRegistry {

	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableCorbaServant.class;
	}

	@Override
	protected TypeFilter getIncludeFilter() {
		return new AssignableTypeFilter(Servant.class);
	}

}
