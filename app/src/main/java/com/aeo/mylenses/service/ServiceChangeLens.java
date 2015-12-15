package com.aeo.mylenses.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.aeo.mylenses.R;
import com.aeo.mylenses.activity.MainActivity;
import com.aeo.mylenses.dao.AlarmDAO;
import com.aeo.mylenses.dao.LensDAO;
import com.aeo.mylenses.dao.LensesDataDAO;
import com.aeo.mylenses.fragment.ListReplaceLensFragment;
import com.aeo.mylenses.vo.AlarmVO;

public class ServiceChangeLens extends Service implements Runnable {

	private final int NOTIFICATION_ID = 1;

	@Override
	public void onCreate() {
		super.onCreate();
		new Thread(this).start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void run() {

		LensDAO dao = LensDAO.getInstance(this);

		int idLenses = ListReplaceLensFragment.listLenses == null ? dao
				.getLastIdLens() : ListReplaceLensFragment.listLenses.get(0)
				.getId();

		Long[] days = dao.getDaysToExpire(idLenses);

		// Get notification of days before.
		AlarmDAO alarmDAO = AlarmDAO.getInstance(this);
		AlarmVO alarmVO = alarmDAO.getAlarm();
		int daysBefore = alarmVO.getDaysBefore();

		long dayLeftEye = days[0] - daysBefore;
		long dayRightEye = days[1] - daysBefore;

		if (dayLeftEye <= 0 && dayRightEye <= 0) {
			notifyUser("B", idLenses);
		} else if (dayLeftEye <= 0) {
			notifyUser("L", idLenses);
		} else if (dayRightEye <= 0) {
			notifyUser("R", idLenses);
		}
	}

	@SuppressLint("NewApi")
	private void notifyUser(String eye, int idLens) {
		String msgEye = null;
		if (eye.equals("L")) {
			msgEye = getString(R.string.msgNotificationLeftEye);
		} else if (eye.equals("R")) {
			msgEye = getString(R.string.msgNotificationRightEye);
		} else {
			msgEye = getString(R.string.msgNotificationBothEyes);
		}

		int[] units = LensesDataDAO.getInstance(this).getUnitsRemaining();
		int unitsLeft = units[0];
		int unitsRight = units[1];

		StringBuilder sbMsg = new StringBuilder();
		sbMsg.append(getString(R.string.str_units_remaining)).append(": ")
				.append(unitsLeft < 0 ? 0 : unitsLeft).append(" ")
				.append(getString(R.string.str_left)).append(" - ")
				.append(unitsRight < 0 ? 0 : unitsRight).append(" ")
				.append(getString(R.string.str_right));

		setNotification(msgEye, sbMsg.toString());
		stopSelf();

		AlarmDAO.getInstance(this).setAlarmNextDay(idLens);
	}

	private void setNotification(String contentText, String subText) {
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		builder.setAutoCancel(true);
		builder.setContentTitle(getString(R.string.msgNotificationTitle));
		builder.setContentText(contentText);
		builder.setSubText(subText);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentIntent(pendingIntent);
		builder.setPriority(NotificationCompat.PRIORITY_MAX); 

		Intent buttonIntent = new Intent(this,
				NotificationBroadcastReceiver.class);
		buttonIntent.putExtra("NOTIFICATION_ID", NOTIFICATION_ID);

		PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, 1,
				buttonIntent, 0);

		builder.addAction(R.drawable.ic_action_cancel_dark,
				getString(R.string.msg_cancel_notification), btPendingIntent);

		Notification notification = builder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(R.string.app_name, notification);

	}
}
