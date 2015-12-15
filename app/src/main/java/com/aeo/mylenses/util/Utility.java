package com.aeo.mylenses.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;

public abstract class Utility {

	@SuppressLint("SimpleDateFormat")
	public static String formatDateDefault(String dateToFormat) {
		String date = null;
		try {
			if (dateToFormat != null) {
				date = new SimpleDateFormat("dd/MM/yyyy")
						.format(new SimpleDateFormat("yyyy-MM-dd")
								.parse(dateToFormat));
			}
		} catch (ParseException e) {
		}
		return date;
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDateToSqlite(String dateToFormat) {
		String date = null;
		try {
			if (dateToFormat != null) {
				date = new SimpleDateFormat("yyyy-MM-dd")
						.format(new SimpleDateFormat("dd/MM/yyyy")
								.parse(dateToFormat));
			}
		} catch (ParseException e) {
		}
		return date;
	}
}
