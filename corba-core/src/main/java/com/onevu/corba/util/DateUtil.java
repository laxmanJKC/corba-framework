package com.onevu.corba.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.Assert;

public abstract class DateUtil {

	public static Date getDate(String source, String format) throws ParseException {
		Assert.notNull(source, "Date must not be Null.");
		Assert.notNull(format, "Format must not be Null.");
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.parse(source);
	}

	public static String getDate(Date source, String format) throws ParseException {
		Assert.notNull(source, "Date must not be Null.");
		Assert.notNull(format, "Format must not be Null.");
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(source);
	}
}
