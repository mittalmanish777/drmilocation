package com.pge.drmi.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtility {

	public static Date addDays(Date date, int days) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);

		return cal.getTime();
	}

	public static Date subtractDays(Date date, int days) {
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, -days);

		return cal.getTime();
	}

	public static Date addYear(Date date, int i) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, i);
		return cal.getTime();
	}

	public static Date getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}
}
