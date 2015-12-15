package com.aeo.mylenses.service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aeo.mylenses.dao.LensDAO;

public class DailyNotificationBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if ("NO_ACTION".equals(action)) {
			LensDAO lensDAO = LensDAO.getInstance(context);
			lensDAO.incrementDaysNotUsed(lensDAO.getLastLens());
		}
/*		
		Bundle extras = intent.getExtras();
		if (extras != null) {
			int idBtn = extras.getInt("ID_BUTTON");
			
			//No button
			if (idBtn == 0) {
				LensDAO lensDAO = LensDAO.getInstance(context);
				lensDAO.incrementDaysNotUsed(lensDAO.getLastLens());
			}
		}
*/
		// Clean Daily Notification in action bar
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(9999);

	}
}
