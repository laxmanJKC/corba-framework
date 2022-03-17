package com.onevue.test;

import com.onevu.corba.stereotype.CorbaComponent;

@CorbaComponent
public class Abx {
	private Pqx pqx;
	
	public Abx(Pqx pqx) {
		this.pqx = pqx;
	}
}
