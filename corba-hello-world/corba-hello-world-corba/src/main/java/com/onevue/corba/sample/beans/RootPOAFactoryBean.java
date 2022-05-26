package com.onevue.corba.sample.beans;

import static com.onevue.spring.constants.CorbaConstants.CORBA_ROOT_POA;

import org.omg.CORBA.ORB;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.onevue.spring.exception.OnevuCorbaException;
import com.onevue.spring.model.ORBBean;

@Service(value = CORBA_ROOT_POA)
public class RootPOAFactoryBean implements InitializingBean, FactoryBean<POA> {

	private String objectName = CORBA_ROOT_POA;

	private POA rootPOA;

	private final ORB orb;

	public RootPOAFactoryBean(@Lazy ORBBean orb) {
		this.orb = orb.getOrb();
	}

	private void init() throws AdapterInactive {
		Assert.notNull(orb, "ORB must be initialized, before initializing the [" + objectName + "]");
		org.omg.CORBA.Object objRef = createRootPOACorbaObject();
		this.rootPOA = POAHelper.narrow(objRef);
	}

	private org.omg.CORBA.Object createRootPOACorbaObject() {
		try {
			return orb.resolve_initial_references(objectName);
		} catch (InvalidName e) {
			throw new OnevuCorbaException(
					"Unable to resolve_initial_reference due to InvalidName [" + e.getMessage() + "]");
		} catch (SystemException se) {
			throw new OnevuCorbaException("SystemException caught when resolve_intial_references call: " + se);
		}
	}

	@Override
	public POA getObject() throws Exception {
		return this.rootPOA;
	}

	@Override
	public Class<?> getObjectType() {
		return POA.class;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
}