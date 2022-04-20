package com.onevue.spring.util;

import java.rmi.Remote;

import javax.rmi.CORBA.Util;

public class TieUtils {

	public static <T> T convertToServant(Class<T> clazz, Remote remoteObj) {
		return (T) Util.getTie(remoteObj);
	}
}
