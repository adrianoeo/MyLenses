package com.aeo.mylenses.vo;

public class AlarmVO {

	private int hour;
	private int minute;
	private int daysBefore;
	private int remindEveryDay;
	
	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getDaysBefore() {
		return daysBefore;
	}

	public void setDaysBefore(int daysBefore) {
		this.daysBefore = daysBefore;
	}

	public int getRemindEveryDay() {
		return remindEveryDay;
	}

	public void setRemindEveryDay(int remindEveryDay) {
		this.remindEveryDay = remindEveryDay;
	}

}
