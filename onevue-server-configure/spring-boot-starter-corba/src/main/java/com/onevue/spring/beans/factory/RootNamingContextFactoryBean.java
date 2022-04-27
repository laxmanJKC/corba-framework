package com.onevue.spring.beans.factory;

import static com.onevue.spring.constants.CorbaConstants.CORBA_NAME_SERVICE;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.BindingHolder;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextHelper;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "onevue.corba.corba-registry", havingValue = "namingcontext")
@Service(value = CORBA_NAME_SERVICE)
public class RootNamingContextFactoryBean implements InitializingBean, FactoryBean<NamingContextExt> {

	private NamingContextExt namingContext;
	
	/**
	 * The {@link org.omg.CORBA.ORB} used to resolving the
	 * {@link org.omg.CosNaming.NamingContextExt} root naming context
	 */
	private final ORB orb;

	/**
	 * The name of the root naming context object as it is known in the ORB.
	 * Defaults to NameService.
	 */
	private String name = CORBA_NAME_SERVICE;

	public RootNamingContextFactoryBean(@Lazy ORB orb) {
		this.orb = orb;
	}

	/**
	 * Initialises the root naming context object by obtaining a reference to the
	 * corresponding CORBA object. If the {@link org.omg.CORBA.ORB} or name is null
	 * an {@link IllegalArgumentException} is thrown.
	 * 
	 * @throws org.omg.CORBA.ORBPackage.InvalidName if the name passed in is not the
	 *                                              name of a naming context
	 */
	public void init() throws InvalidName {
		if (orb == null) {
			throw new IllegalArgumentException("The orb used to resolve the root naming context cannot be null.");
		}
		if (name == null) {
			throw new IllegalArgumentException("The name to resolve the root naming context cannot be null.");
		}
		org.omg.CORBA.Object obj = orb.resolve_initial_references(name);
		if (obj != null) {
			namingContext = NamingContextExtHelper.narrow(obj);
		} else {
			throw new IllegalStateException(name + " could not be resolved through the ORB.");

		}
		if (namingContext == null) {
			throw new IllegalStateException("NamingContext is null. The root naming context could not be resolved.");
		}
	}

	/**
	 * Returns a reference to the root naming context. The particular reference
	 * returned is actually the "extended" root naming context.
	 * 
	 * @return the extended root naming context
	 */
	public NamingContextExt getRootNamingContext() {
		return namingContext;
	}

	/**
	 * Clears the root naming context of all bindings. This is needed if the Data
	 * Agent is restarted without the naming service having been restarted.
	 * 
	 * @param root
	 * @param omissions
	 */
	public void clearRootNamingContext(String root, String[] omissions) {

		/*
		 * try { BindingIteratorHolder bi = new BindingIteratorHolder();
		 * BindingListHolder bl = new BindingListHolder(); // Get the list of bindings
		 * NamingContextExt pruneNC =
		 * NamingContextExtHelper.narrow(namingContext.resolve_str(root));
		 * pruneNC.list(0, bl, bi); BindingHolder b = new BindingHolder(); if (bi.value
		 * != null) { // Step through the bindings and delete them all while
		 * (bi.value.next_one(b)) { int i; if (omissions != null) { for (i = 0; i <
		 * omissions.length; i++) { if
		 * (pruneNC.to_string(b.value.binding_name).compareTo(omissions[i]) == 0) {
		 * break; } } if (i == omissions.length) { pruneNC.unbind(b.value.binding_name);
		 * } } else { pruneNC.unbind(b.value.binding_name); } } } } catch (Exception e)
		 * { e.printStackTrace(); }
		 */
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ORB getOrb() {
		return orb;
	}

	@Override
	public NamingContextExt getObject() throws Exception {
		return namingContext;
	}

	@Override
	public Class<?> getObjectType() {
		return NamingContext.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
}
