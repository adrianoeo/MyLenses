package com.aeo.mylenses.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.aeo.mylenses.R;
import com.aeo.mylenses.activity.MainActivity;

public class ServiceWearLens extends Service implements Runnable {

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
		setNotification();
		stopSelf();
	}

	private void setNotification() {
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				intent, 0);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				this);
		builder.setAutoCancel(true);
		builder.setContentTitle(getString(R.string.app_name));
		builder.setContentText(getString(R.string.msg_select_which_lens));
		builder.setSubText(getString(R.string.msg_are_you_wearing_today));
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentIntent(pendingIntent);
		builder.setPriority(NotificationCompat.PRIORITY_MAX); 
		
		Intent intentYes = new Intent(this,
				DailyNotificationBroadcastReceiver.class);
		intentYes.setAction("YES_ACTION");
//		intentYes.putExtra("ID_BUTTON", 1);

		PendingIntent btYesPendingIntent = PendingIntent.getBroadcast(this, 2,
				intentYes, PendingIntent.FLAG_UPDATE_CURRENT);

		builder.addAction(R.drawable.ic_action_accept_dark,
				getString(R.string.btn_yes), btYesPendingIntent);

		Intent intentNo = new Intent(this,
				DailyNotificationBroadcastReceiver.class);
		intentNo.setAction("NO_ACTION");
//		intentNo.putExtra("ID_BUTTON", 0);

		PendingIntent btNoPendingIntent = PendingIntent.getBroadcast(this, 2,
				intentNo, PendingIntent.FLAG_UPDATE_CURRENT);

		builder.addAction(R.drawable.ic_action_cancel_dark,
				getString(R.string.btn_no), btNoPendingIntent);

		Notification notification = builder.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notificationManager.notify(9999, notification);

	}
}
