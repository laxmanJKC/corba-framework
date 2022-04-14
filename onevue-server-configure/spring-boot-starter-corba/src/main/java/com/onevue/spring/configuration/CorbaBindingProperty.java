package com.onevue.spring.configuration;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.POA;
import org.springframework.util.Assert;

import com.onevue.spring.enums.POAPolicy;

import lombok.Data;

@Data
public class CorbaBindingProperty {

	private String beanName;

	private String poaName;
	
	private String publishRef;

	private String nameComponent;

	private String expression;

	private boolean activate = true;

	private boolean rootPOAManagerNeeded = true;

	private List<POAPolicy> poaPolicy = new ArrayList<POAPolicy>();

	public Policy[] fetchPolicy(POA rootPOA) {
		Assert.notNull(rootPOA, "RootPOA must not be null");
		if (poaPolicy == null || poaPolicy.isEmpty()) {
			return new Policy[0];
		}
		List<Policy> policies = new ArrayList<Policy>();
		for (POAPolicy poaPolicy: this.poaPolicy) {
			policies.add(poaPolicy.createPolicy(rootPOA, poaPolicy));
		}
		return policies.toArray(new Policy[policies.size()]);
	}
}
