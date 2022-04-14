package com.onevue.spring.configuration;

import java.util.ArrayList;
import java.util.List;

import com.onevue.spring.enums.POAPolicy;

import lombok.Data;

@Data
public class CorbaPOAProperty {
	
	private String beanName;

	private String poaName;
	
	private String poaId;

	private boolean activate = true;
	
	private boolean rootPOAManagerNeeded = true;

	private List<POAPolicy> poaPolicy = new ArrayList<POAPolicy>();

}
