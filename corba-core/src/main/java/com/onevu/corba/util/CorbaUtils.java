package com.onevu.corba.util;

import static com.onevu.corba.constants.CorbaConstants.CORBA_NAME_SERVICE;
import static com.onevu.corba.constants.CorbaConstants.CORBA_POA_SUFFIX;
import static com.onevu.corba.constants.CorbaConstants.CORBA_ROOT_POA;
import static com.onevu.corba.constants.DelimiterConstants.DOT_DELIMITER;

import java.util.Properties;
import java.util.StringTokenizer;

import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.Servant;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.AdapterAlreadyExists;
import org.omg.PortableServer.POAPackage.InvalidPolicy;

import com.onevu.corba.exception.OnevuCorbaException;

public abstract class CorbaUtils {

	public static ORB initializeCorba(Properties props, String... args) {
		Assert.notNull(args, "arguments for the application's during corba initialization must not be Null.");
		Properties properties = System.getProperties();
		if (props != null) {
			properties = props;
		}
		ORB orb = ORB.init(args, properties);
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
		try {
			return orb.string_to_object(name);
		} catch(SystemException se) {
			throw new OnevuCorbaException("SystemException caught when string_to_object! " + se.toString());
		}
	}
	
	public static org.omg.CORBA.Object resolveInitialReferences(ORB orb, String initRef) {
		Assert.notNull(orb, "ORB object must not be Null.");
		String ref = CORBA_NAME_SERVICE;
		if (StringUtils.hasLength(initRef)) {
			ref = initRef;
		}
		try {
			return orb.resolve_initial_references(ref);
		} catch (InvalidName e) {
			throw new OnevuCorbaException("Unable to resolve_initial_reference due to InvalidName [" + e.getMessage() + "]");
		} catch (SystemException se) {
			throw new OnevuCorbaException("SystemException caught when resolve_intial_references call: " + se);
		}
	}
	
	public static NamingContext fetchNSRootContext(ORB orb, String initRef, boolean isStringToObject) {
		Assert.notNull(orb, "ORB object must not be Null.");
		org.omg.CORBA.Object corbaObj = null;
		if (isStringToObject) {
			corbaObj = stringToObject(orb, initRef);
		} else {
			corbaObj = resolveInitialReferences(orb, CORBA_NAME_SERVICE);
		}
		return namingContext(corbaObj);
	}
	
	public static NamingContext namingContext(org.omg.CORBA.Object initObjRef) {
		Assert.notNull(initObjRef, "Corba initialized object Reference must not be Null.");
		try {
			return NamingContextHelper.narrow(initObjRef);
		} catch (org.omg.CORBA.BAD_PARAM bp) {
			throw new OnevuCorbaException("Failed to narrow NameService root context. BAD_PARAM caught when resolve_intial_references: " + bp);
		}
	}
	
	public static org.omg.CORBA.Object resolveName(NamingContext namingContext, String tokenName, NameComponent ...nameComponent) {
		Assert.notNull(nameComponent, "Namecomponent must not be Null.");
		try {
			return namingContext.resolve(nameComponent);
		} catch (NotFound ex) {
			throw new OnevuCorbaException("NotFound caught for " + tokenName + ": " + ex);
		}
		catch (CannotProceed ex) {
			throw new OnevuCorbaException("CannotProceed caught for " + tokenName + ": "+ ex);
		}
		catch (org.omg.CosNaming.NamingContextPackage.InvalidName ex ) {
			throw new OnevuCorbaException("InvalidName caught for " + tokenName + ": "+ ex);
		}
		catch (org.omg.CORBA.SystemException ex ) {
			throw new OnevuCorbaException("SystemException caught for " + tokenName + ": "+ ex);
		}
	}
	
	public static org.omg.CORBA.Object resolveName(ORB orb, String objName, boolean isStringToObject) {
		NamingContext namingContext = fetchNSRootContext(orb, objName, isStringToObject);
		StringTokenizer stringTokenizer = new StringTokenizer(objName, DOT_DELIMITER);
		int length = stringTokenizer.countTokens();
		if (length < 1) {
			throw new OnevuCorbaException("[resolveName] Invalid name " + objName);
		}
		NameComponent nameComponent[] = new NameComponent[length];
		for (int index = 0; index < length; index++) {
			// create a new context and bind it relative to
			// the objects context
			String tokenName = stringTokenizer.nextToken();
			nameComponent[index] = StringUtils.createNameComponent(tokenName);
		}
		return resolveName(namingContext, objName, nameComponent);
	}
	
	public static void registerName(ORB orb, String objName, org.omg.CORBA.Object object, boolean isStringToObject) {
		org.omg.CORBA.Object objRef = null;
		NamingContext namingContext = fetchNSRootContext(orb, objName, isStringToObject);
		StringTokenizer stringTokenizer = new StringTokenizer(objName, DOT_DELIMITER);
		int length = stringTokenizer.countTokens();
		if (length < 1) {
			throw new OnevuCorbaException("[registerName] Invalid name " + objName);
		}
		NameComponent nameComponent[] = new NameComponent[1];
		NamingContext objectContext = null;
		for (int index = 0; index < length - 1; index++) {
			boolean isExist = false;
			String tokenName = stringTokenizer.nextToken();
			nameComponent[0] = StringUtils.createNameComponent(tokenName);
			objRef = resolveName(namingContext, tokenName, nameComponent);
			objectContext = namingContext(objRef);
			isExist = true;
			if (isExist) {
				continue;
			}
			objectContext = bindNewContext(objectContext, tokenName, nameComponent);
		}
		String tokenName = stringTokenizer.nextToken();
		nameComponent[0] = StringUtils.createNameComponent(tokenName);
		rebindCorbaObject(objectContext, object, tokenName, nameComponent);
	}
	
	private static void rebindCorbaObject(NamingContext objectContext, org.omg.CORBA.Object objImpl, String tokenName, NameComponent ...nameComponents) {
		try
		{
			objectContext.rebind(nameComponents, objImpl);
		}
		catch (org.omg.CosNaming.NamingContextPackage.NotFound ex ) {
			throw new OnevuCorbaException("NotFound caught for " + tokenName + ": "+ex);
		}
		catch (org.omg.CosNaming.NamingContextPackage.CannotProceed ex ) {
			throw new OnevuCorbaException("CannotProceed caught for " +
					tokenName + ": "+ex);
		}
		catch (org.omg.CosNaming.NamingContextPackage.InvalidName ex ) {
			throw new OnevuCorbaException("InvalidName caught for " +tokenName + ": "+ex);
		}
		catch ( org.omg.CORBA.SystemException Ex ) {
			throw new OnevuCorbaException("SystemException caught for " + tokenName + ": "+Ex);
		}
	}
	
	public static NamingContext bindNewContext(NamingContext objectContext,  String tokenName, NameComponent ...objName) {
		Assert.notNull(objName, "[bindNewContext] NameComponent must not be Null.");
		Assert.notNull(objectContext, "[bindNewContext] NamingContext must not be Null.");
		Assert.notNull(tokenName, "[bindNewContext] TokenName must not be Null.");
		try
		{
			return objectContext.bind_new_context(objName);
		}
		catch (org.omg.CosNaming.NamingContextPackage.NotFound ex) {
			throw new OnevuCorbaException("NotFound caught for " + tokenName + ": "+ex);
		}
		catch (org.omg.CosNaming.NamingContextPackage.CannotProceed ex) {
			throw new OnevuCorbaException("CannotProceed caught for " +	tokenName + ": "+ex);
		}
		catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound ex) {
			throw new OnevuCorbaException("AlreadyBound caught for " + tokenName + ": "+ex);
		}
		catch (org.omg.CosNaming.NamingContextPackage.InvalidName ex) {
			throw new OnevuCorbaException("InvalidName caught for " + tokenName + ": "+ex);
		}
		catch ( org.omg.CORBA.SystemException ex ) {
			throw new OnevuCorbaException("SystemException caught for " + tokenName + ": "+ex);
		}		
	}
	
	public static org.omg.CORBA.Object servantToReference(POA customPOA, Servant servant) {
		Assert.notNull(customPOA, "POA must not be Null.");
		Assert.notNull(servant, "Corba Servant must not be Null.");
		try {
			return customPOA.servant_to_reference(servant);
		} catch(Exception ex) {
			throw new OnevuCorbaException("Exception during servant_to_reference due to " + ex.getMessage());
		}
	}

	public static void startCorbaServer(ORB orb) {
		Assert.notNull(orb, "ORB object must not be Null.");
		orb.run();
	}
}
