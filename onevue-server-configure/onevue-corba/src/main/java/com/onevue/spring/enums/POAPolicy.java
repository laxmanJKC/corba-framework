package com.onevue.spring.enums;

import static com.onevue.spring.constants.CorbaPolicyConstants.LIFE_SPAN_POLICY;
import static com.onevue.spring.constants.CorbaPolicyConstants.REQUEST_PROCESSING_POLICY;
import static com.onevue.spring.constants.CorbaPolicyConstants.SERVANT_RETENTION_POLICY;

import static org.omg.PortableServer.LifespanPolicyValue._TRANSIENT;
import static org.omg.PortableServer.LifespanPolicyValue._PERSISTENT;
import static org.omg.PortableServer.RequestProcessingPolicyValue._USE_ACTIVE_OBJECT_MAP_ONLY;
import static org.omg.PortableServer.RequestProcessingPolicyValue._USE_DEFAULT_SERVANT;
import static org.omg.PortableServer.RequestProcessingPolicyValue._USE_SERVANT_MANAGER;
import static org.omg.PortableServer.ServantRetentionPolicyValue._RETAIN;

import org.omg.CORBA.Policy;
import org.omg.PortableServer.LifespanPolicyValue;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.RequestProcessingPolicyValue;
import org.omg.PortableServer.ServantRetentionPolicyValue;

import static org.omg.PortableServer.ServantRetentionPolicyValue._NON_RETAIN;

public enum POAPolicy {

	LIFE_SPAN_TRANSIENT(_TRANSIENT, LIFE_SPAN_POLICY), 
	LIFE_SPAN_PERSISTENT(_PERSISTENT, LIFE_SPAN_POLICY),
	REQUEST_PROCESSING_USE_ACTIVE_OBJECT_MAP_ONLY(_USE_ACTIVE_OBJECT_MAP_ONLY, REQUEST_PROCESSING_POLICY),
	REQUEST_PROCESSING_USE_DEFAULT_SERVANT(_USE_DEFAULT_SERVANT, REQUEST_PROCESSING_POLICY),
	REQUEST_PROCESSING_USE_SERVANT_MANAGER(_USE_SERVANT_MANAGER, REQUEST_PROCESSING_POLICY),
	SERVANT_RENTENTION_RETAIN(_RETAIN, SERVANT_RETENTION_POLICY),
	SERVANT_RENTENTION_NON_RETAIN(_NON_RETAIN, SERVANT_RETENTION_POLICY);

	private int policyValue;

	private int policyType;

	private POAPolicy(int policyValue, int policyType) {
		this.policyValue = policyValue;
		this.policyType = policyType;
	}
	
	public static Policy createPolicy(POA rootPOA, POAPolicy policyProperties) {
		Policy policy = null;
		switch (policyProperties.getPolicyType()) {
		case LIFE_SPAN_POLICY:
			LifespanPolicyValue lifeSpanValue = LifespanPolicyValue.from_int(policyProperties.getPolicyValue());
			policy = rootPOA.create_lifespan_policy(lifeSpanValue);
			break;
			
		case REQUEST_PROCESSING_POLICY:
			RequestProcessingPolicyValue requestProcessingValue = RequestProcessingPolicyValue.from_int(policyProperties.getPolicyValue());
			policy = rootPOA.create_request_processing_policy(requestProcessingValue);
			break;
			
		case SERVANT_RETENTION_POLICY:
			ServantRetentionPolicyValue servantRetentionValue = ServantRetentionPolicyValue.from_int(policyProperties.getPolicyValue());
			policy = rootPOA.create_servant_retention_policy(servantRetentionValue);
			break;
		default:
			break;
		}
		return policy;
	}

	public int getPolicyValue() {
		return policyValue;
	}

	public int getPolicyType() {
		return policyType;
	}
}
