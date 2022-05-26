package com.onevue.spring.model;

import static com.onevue.spring.constants.CorbaConstants.CORBA_NAME_SERVICE;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.Lifecycle;

public class NameServiceBean implements Lifecycle {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NameServiceBean.class);

	private boolean isRunning;

	private final ORB orb;

	private org.omg.CORBA.Object nameServiceRef;

	private NamingContextExt namingContextExtRef;

	public NameServiceBean(ORB orb) {
		this.orb = orb;
	}

	private List<NamingAction> nameComponents = new ArrayList<NamingAction>();

	@Override
	public void start() {
		this.isRunning = true;
		try {
			LOGGER.info("Corba Naming Service Binding STARTED !!");
			nameServiceRef = orb.resolve_initial_references(CORBA_NAME_SERVICE);
			namingContextExtRef = NamingContextExtHelper.narrow(nameServiceRef);
			for (NamingAction namingAction: nameComponents) {
				addNamingComponent(namingAction.getAction(), namingAction.getName(), namingAction.getObjRef());
			}
			LOGGER.info("Corba Naming Service Binding COMPLETED !!");
		} catch (InvalidName e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		this.isRunning = false;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	public void addNamingComponent(String action, String name, org.omg.CORBA.Object objRef) {
		try {
			switch (action) {
			case "to_name":
				NameComponent[] toNameComponents;
				toNameComponents = namingContextExtRef.to_name(name);
				namingContextExtRef.bind(toNameComponents, objRef);
				break;

			default:
				break;
			}
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AlreadyBound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public org.omg.CORBA.Object resolve_str(String name) {
		try {
			return this.namingContextExtRef.resolve_str(name);
		} catch (NotFound e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotProceed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void addNamingAction(String action, String name, org.omg.CORBA.Object corbaRef) {
		this.nameComponents.add(new NamingAction(action, name, corbaRef));
	}
	
	
	public NamingContextExt getNamingContextExtRef() {
		return namingContextExtRef;
	}

	public static class NamingAction {
		private final String name;
		
		private final String action;
		
		private final org.omg.CORBA.Object objRef;
		
		public NamingAction(String action, String name, org.omg.CORBA.Object objRef) {
			this.name = name;
			this.action = action;
			this.objRef = objRef;
		}

		public String getName() {
			return name;
		}

		public String getAction() {
			return action;
		}

		public org.omg.CORBA.Object getObjRef() {
			return objRef;
		}
	}

}
