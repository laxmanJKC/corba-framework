package com.onevue.spring.constants;

import org.glassfish.jndi.cosnaming.CNCtxFactory;

public class CorbaConstants {
	
	public static final String CORBA_ORB_BEAN = "ORBBean";
	
	public static final String CORBA_ROOT_POA = "RootPOA";
	
	public static final String CORBA_INITIAL_CONTEXT = "RMI-Corba_Context";
	
	public static final String CORBA_POA_TIE_SUFFIX = "POATie";
	
	public static final String CORBA_GLASSFISH_TIE_SUFFIX = "_Tie";
	
	public static final String CORBA_CONTEXT_FACTORY_INTIAL_KEY = "java.naming.factory.initial";
	
	public static final String CORBA_CONTEXT_FACTORY_INITIAL_URL_KEY = "java.naming.provider.url";
	
	public static final String CORBA_CONTEXT_FACTORY_INTIAL_VALUE = CNCtxFactory.class.getName();// "com.sun.jndi.cosnaming.CNCtxFactory";
	
	public static final String CORBA_POA_IMPL_SUFFIX = "Impl";
	
	public static final String CORBA_POA_OPERATIONS_SUFFIX = CORBA_POA_IMPL_SUFFIX + "Operations";
	
	public static final String CORBA_OBJECT_SUFFIX = "_BindRef";
	
	public static final String EXPRESSION_CORBA_ORB_OBJ = "orb";
	
	public static final String EXPRESSION_CORBA_OBJECT_TIE = "_this(#"+EXPRESSION_CORBA_ORB_OBJ+")";
	
	public static final String CORBA_ORB_CLASS = "org.omg.CORBA.ORBClass";
	
	public static final String CORBA_ORB_CLASS_VALUE = "com.iona.corba.art.artimpl.ORBImpl";
	
	public static final String CORBA_ORB_SINGLETON_CLASS = "org.omg.CORBA.ORBSingletonClass";
	
	public static final String CORBA_ORB_SINGLETON_CLASS_VALUE = "com.iona.corba.art.artimpl.ORBSingleton";
	
	public static final String CORBA_ORB_INITIAL_PORT = "org.omg.CORBA.ORBInitialPort";
	
	public static final String CORBA_ORB_INITIAL_HOST = "org.omg.CORBA.ORBInitialHost";
	
	public static final String CORBA_ORB_NAME = "ORBname";
	
	public static final String CORBA_NAME_SERVICE = "NameService";
	
	public static final String CORBA_ONEVU = "onevu";
	
	public static final String CORBA_POA_SUFFIX = "_poa";
	
	public static final String CORBA_PROPERTIES = "corba.properties";
	
	public static final String ENVIRONMENT_KEY = "environment-properties";
}
