package com.onevue.spring.util;

import javax.naming.Context;
import javax.naming.NamingException;

import org.springframework.util.Assert;

import com.onevue.spring.exception.OnevuCorbaException;
import com.onevue.spring.model.CorbaBindingProperty;

public class CorbaBindUtils {

	public static void contextBinding(Context context, CorbaBindingProperty bindingProperty, org.omg.CORBA.Object corbaObjRef) {
		Assert.notNull(context, "[RMI] javax.naming.Context must not be null");
		Assert.notNull(bindingProperty, "Context binding property must not be null");
		Assert.notNull(bindingProperty.getPublishRef(), "Context Service must not be null");
		Assert.notNull(corbaObjRef, "org.omg.CORBA.Object must not be null for javax.naming.Context binding");
		try {
			context.bind(bindingProperty.getPublishRef(), corbaObjRef);
		} catch (NamingException e) {
			throw new OnevuCorbaException("[NamingException] ContextBinding failed ["+e.getCause()+"]");
		}
	}
}
