package com.aeo.mylenses.dao;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aeo.mylenses.activity.MainActivity;
import com.aeo.mylenses.db.DB;
import com.aeo.mylenses.service.AlarmBroadcastReceiver;
import com.aeo.mylenses.service.BootBroadcastReceiver;
import com.aeo.mylenses.service.DailyAlarmBroadcastReceiver;
import com.aeo.mylenses.vo.AlarmVO;

public class AlarmDAO {

	private static String tableName = "alarm";
	private static Context context;
	private static String[] columns = { "hour", "minute", "days_before",
			"remind_every_day" };
	private SQLiteDatabase db;
	private static final int ID_ALARM_LEFT = 1;
	private static final int ID_ALARM_RIGHT = 2;
	private static final int ID_ALARM_NEXT_DAY = 3;
	private static final int ID_ALARM_DAILY = 4;
	private static AlarmDAO instance;

	/** Also cache a reference to the Backup Manager */
	BackupManager mBackupManager;

	public static AlarmDAO getInstance(Context context) {
		if (instance == null) {
			return new AlarmDAO(context);
		}
		return instance;
	}

	public AlarmDAO(Context context) {
		AlarmDAO.context = context;
		db = new DB(context).getWritableDatabase();

		mBackupManager = new BackupManager(context);
	}

	public boolean insert(AlarmVO vo) {
		synchronized (MainActivity.sDataLock) {
			mBackupManager.dataChanged();

			return db.insert(tableName, null, getContentValues(vo)) > 0;
		}
	}

	public boolean update(AlarmVO vo) {
		synchronized (MainActivity.sDataLock) {
			mBackupManager.dataChanged();

			return db.update(tableName, getContentValues(vo), null, null) > 0;
		}
	}

	private ContentValues getContentValues(AlarmVO vo) {
		ContentValues content = new ContentValues();
		content.put("hour", vo.getHour());
		content.put("minute", vo.getMinute());
		content.put("days_before", vo.getDaysBefore());
		content.put("remind_every_day", vo.getRemindEveryDay());

		return content;
	}

	public AlarmVO getAlarm() {
		Cursor rs = db.query(tableName, columns, null, null, null, null, null);

		AlarmVO vo = null;
		if (rs.moveToFirst()) {
			vo = new AlarmVO();
			vo.setHour(rs.getInt(rs.getColumnIndex("hour")));
			vo.setMinute(rs.getInt(rs.getColumnIndex("minute")));
			vo.setDaysBefore(rs.getInt(rs.getColumnIndex("days_before")));
			vo.setRemindEveryDay(rs.getInt(rs
					.getColumnIndex("remind_every_day")));
		}
		return vo;
	}

	public void setAlarm(int idLens) {
		cancelAlarm();
		cancelAlarmDaily();

		LensDAO lensDAO = LensDAO.getInstance(context);
		Calendar[] calendars = lensDAO.getDateAlarm(idLens);

		AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
		AlarmVO alarmVO = alarmDAO.getAlarm();

		if (alarmVO == null) {
			alarmVO = new AlarmVO();
			alarmVO.setHour(0);
			alarmVO.setMinute(0);
			alarmVO.setDaysBefore(0);
		}

		Calendar calendarLeft = Calendar.getInstance();
		Calendar calendarRight = Calendar.getInstance();

		calendarLeft.set(Calendar.DAY_OF_MONTH,
				calendars[0].get(Calendar.DAY_OF_MONTH));
		calendarLeft.set(Calendar.MONTH, calendars[0].get(Calendar.MONTH));
		calendarLeft.set(Calendar.YEAR, calendars[0].get(Calendar.YEAR));
		calendarLeft.set(Calendar.HOUR_OF_DAY, alarmVO.getHour());
		calendarLeft.set(Calendar.MINUTE, alarmVO.getMinute());
		calendarLeft.set(Calendar.SECOND, 0);
		calendarLeft.set(Calendar.MILLISECOND, 0);

		calendarRight.set(Calendar.DAY_OF_MONTH,
				calendars[1].get(Calendar.DAY_OF_MONTH));
		calendarRight.set(Calendar.MONTH, calendars[1].get(Calendar.MONTH));
		calendarRight.set(Calendar.YEAR, calendars[1].get(Calendar.YEAR));
		calendarRight.set(Calendar.HOUR_OF_DAY, alarmVO.getHour());
		calendarRight.set(Calendar.MINUTE, alarmVO.getMinute());
		calendarRight.set(Calendar.SECOND, 0);
		calendarRight.set(Calendar.MILLISECOND, 0);

		// Seta as datas para dia(s) antes para a notificacao.
		int daysBefore = alarmVO.getDaysBefore() * (-1);
		calendarLeft.add(Calendar.DATE, daysBefore);
		calendarRight.add(Calendar.DATE, daysBefore);

		// Se as datas das lentes esquerda e direita forem iguais seta apenas um
		// alarme, senao seta um para cada lente.
		if (calendarLeft.get(Calendar.DAY_OF_MONTH) == calendarRight
				.get(Calendar.DAY_OF_MONTH)
				&& calendarLeft.get(Calendar.MONTH) == calendarRight
						.get(Calendar.MONTH)
				&& calendarLeft.get(Calendar.YEAR) == calendarRight
						.get(Calendar.YEAR)) {

			setAlarmManager(ID_ALARM_LEFT, calendarLeft.getTimeInMillis());
		} else {
			setAlarmManager(ID_ALARM_LEFT, calendarLeft.getTimeInMillis());
			setAlarmManager(ID_ALARM_RIGHT, calendarRight.getTimeInMillis());
		}

		// Start daily notification if is checked and not expired
		if (alarmVO.getRemindEveryDay() == 1) {
			Long[] daysToExpire = lensDAO.getDaysToExpire(lensDAO
					.getLastIdLens());

			if (daysToExpire[0] > 0 || daysToExpire[1] > 0) {
				setAlarmManagerDaily(alarmVO.getHour(), alarmVO.getMinute());
			}
		}

		enableReceiverWhenBoot();
	}

	private void setAlarmManager(int idAlarm, long timeAlarm) {
		Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				idAlarm, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManagerLeft = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManagerLeft.set(AlarmManager.RTC, timeAlarm, pendingIntent);

	}

	public void setAlarmManagerDaily(int hour, int minute) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.add(Calendar.DATE, 1);

		Intent intent = new Intent(context, DailyAlarmBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
				ID_ALARM_DAILY, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		alarmManager.setInexactRepeating(AlarmManager.RTC,
				calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				pendingIntent);
	}

	public void cancelAlarm() {
		Intent intentLeft = new Intent(context, AlarmBroadcastReceiver.class);
		PendingIntent pendingIntentLeft = PendingIntent.getBroadcast(context,
				ID_ALARM_LEFT, intentLeft, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManagerLeft = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManagerLeft.cancel(pendingIntentLeft);

		Intent intentRight = new Intent(context, AlarmBroadcastReceiver.class);
		PendingIntent pendingIntentRight = PendingIntent.getBroadcast(context,
				ID_ALARM_RIGHT, intentRight, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManagerRight = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManagerRight.cancel(pendingIntentRight);

		Intent intentNexDay = new Intent(context, AlarmBroadcastReceiver.class);
		PendingIntent pendingIntentNextDay = PendingIntent.getBroadcast(
				context, ID_ALARM_NEXT_DAY, intentNexDay,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManagerNexDay = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManagerNexDay.cancel(pendingIntentNextDay);
	}

	public void cancelAlarmDaily() {
		Intent intent = new Intent(context, DailyAlarmBroadcastReceiver.class);
		PendingIntent pendingIntentLeft = PendingIntent.getBroadcast(context,
				ID_ALARM_DAILY, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntentLeft);
	}

	public void setAlarmNextDay(int idLens) {
		// Days to expire
		Long[] daysToExpire = LensDAO.getInstance(context).getDaysToExpire(
				idLens);

		Long daysToExpireLeft = daysToExpire[0];
		Long daysToExpireRight = daysToExpire[1];

		// Days before for notification
		AlarmVO alarmVO = AlarmDAO.getInstance(context).getAlarm();
		int daysBefore = alarmVO.getDaysBefore();
		int hour = alarmVO.getHour();
		int minute = alarmVO.getMinute();

		// Next day
		Calendar today = Calendar.getInstance();
		today.set(Calendar.HOUR_OF_DAY, hour);
		today.set(Calendar.MINUTE, minute);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		today.add(Calendar.DATE, 1);

		if (daysToExpireLeft - daysBefore <= 0
				|| daysToExpireRight - daysBefore <= 0) {
			setAlarmManager(ID_ALARM_NEXT_DAY, today.getTimeInMillis());
			enableReceiverWhenBoot();
		}

	}

	private void enableReceiverWhenBoot() {
		// Enabling receiver when boot
		ComponentName receiver = new ComponentName(context,
				BootBroadcastReceiver.class);
		PackageManager pm = context.getPackageManager();

		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}
}
