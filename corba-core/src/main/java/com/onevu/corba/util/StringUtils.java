package com.onevu.corba.util;

import static com.onevu.corba.constants.DelimiterConstants.COLON_DELIMITER;

import org.omg.CosNaming.NameComponent;
import org.springframework.util.Assert;

public abstract class StringUtils {

	public static boolean isEmpty(Object str) {
		return (str == null || "".equals(str));
	}
	
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	public static NameComponent createNameComponent(String tokenName) {
		Assert.notNull(tokenName, "TokenName must not be Null.");
		int index = tokenName.indexOf(COLON_DELIMITER);
		NameComponent nameComponent = null;
		if (index < 0) {
			// simple object name hierarchy
			nameComponent = new NameComponent(tokenName, "");
		} else {
			nameComponent = new NameComponent(tokenName.substring(0, index), tokenName.substring(index + 1));
		}
		return nameComponent;
	}

}
