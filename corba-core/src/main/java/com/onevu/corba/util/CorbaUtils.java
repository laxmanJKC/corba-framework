package com.onevu.corba.util;

import static com.onevu.corba.constants.CorbaConstants.CORBA_NAME_SERVICE;
import static com.onevu.corba.constants.CorbaConstants.CORBA_POA_SUFFIX;
import static com.onevu.corba.constants.CorbaConstants.CORBA_ROOT_POA;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;
import org.springframework.util.Assert;

import com.onevu.corba.exception.OnevuCorbaException;

public abstract class CorbaUtils {

	public static ORB initializeCorba(Properties props, String... args) {
		Assert.notNull(args, "arguments for the application's during corba initialization must not be Null.");
		ORB orb = ORB.init(args, props);
		return orb;
	}

	public static POA rootPOA(ORB orb) {
		POA rootPOA = POAHelper.narrow(resolveInitialReferences(orb, CORBA_ROOT_POA));
		return rootPOA;
	}
	
	public static POA createPOA(String adapterName, ORB orb, org.omg.CORBA.Policy ...policies) {
		Assert.notNull(adapterName, "POA adapter name must not be Null.");
		Assert.notNull(policies, "Corba Policy must not be Null.");
		StringBuilder name = new StringBuilder().append(adapterName)
											    .append(CORBA_POA_SUFFIX);
		POA rootPOA = rootPOA(orb);
		try {
			POA customPOA = rootPOA.create_POA(name.toString(), rootPOA.the_POAManager(), policies);
			return customPOA;
		} catch (AdapterAlreadyExists e) {
			throw new OnevuCorbaException("Adapter name ["+adapterName+"] already exist. Reason: " + e.getMessage());
		} catch (InvalidPolicy e) {
			throw new OnevuCorbaException("Invalid Policy, Reason: " + e.getMessage());
		} 
	}
	
	public static POA createPOA(String adapterName, POA rootPOA, org.omg.CORBA.Policy ...policies) {
		Assert.notNull(rootPOA, "Root POA must not be Null");
		Assert.notNull(adapterName, "POA adapter name must not be Null.");
		Assert.notNull(policies, "Corba Policy must not be Null.");
		StringBuilder name = new StringBuilder().append(adapterName)
											    .append(CORBA_POA_SUFFIX);
		try {
			POA customPOA = rootPOA.create_POA(name.toString(), rootPOA.the_POAManager(), policies);
			return customPOA;
			
		} catch (AdapterAlreadyExists e) {
			throw new OnevuCorbaException("Adapter name ["+adapterName+"] already exist. Reason: " + e.getMessage());
		} catch (InvalidPolicy e) {
			throw new OnevuCorbaException("Invalid Policy, Reason: " + e.getMessage());
		} 
	}
	
	public static void activatePOA(POA poa) {
		try {
			poa.the_POAManager().activate();
		} catch (AdapterInactive e) {
			throw new OnevuCorbaException("Unable to activatePOA due to "+e.getMessage());
		}
	}
	
	public static org.omg.CORBA.Object stringToObject(ORB orb, String name) {
		Assert.notNull(orb, "ORB object must not be Null.");
		return orb.string_to_object(name);
	}
	
	public static org.omg.CORBA.Object resolveInitialReferences(ORB orb, String initRef) {
		Assert.notNull(orb, "ORB object must not be Null.");
		String ref = CORBA_NAME_SERVICE;
		if (initRef != null) {
			ref = initRef;
		}
		try {
			return orb.resolve_initial_references(ref);
		} catch (InvalidName e) {
			throw new OnevuCorbaException("Unable to resolve_initial_reference due to InvalidName [" + e.getMessage() + "]");
		}
	}
	
	public static NamingContext namingContext(org.omg.CORBA.Object initObjRef) {
		Assert.notNull(initObjRef, "Corba initialized object Reference must not be Null.");
		return NamingContextHelper.narrow(initObjRef);
	}

	public static void startCorbaServer(ORB orb) {
		Assert.notNull(orb, "ORB object must not be Null.");
		orb.run();
	}
}
