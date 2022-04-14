package com.onevue.spring.configuration;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.portable.IDLEntity;
import org.omg.PortableServer.Servant;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import com.onevue.spring.configuration.annotation.EnableCorbaServant;
import com.sun.corba.ee.impl.javax.rmi.PortableRemoteObject;

public class CorbaServantRegistry extends AbstractCorbaBeanRegistry {

	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return EnableCorbaServant.class;
	}

	@Override
	protected TypeFilter[] getIncludeFilter() {
		List<TypeFilter> typeFilters = new ArrayList<TypeFilter>();
		typeFilters.add(new AssignableTypeFilter(Servant.class));
		typeFilters.add(new AssignableTypeFilter(IDLEntity.class));
		typeFilters.add(new AssignableTypeFilter(PortableRemoteObject.class));
		return typeFilters.toArray(new TypeFilter[typeFilters.size()]);
	}

}
