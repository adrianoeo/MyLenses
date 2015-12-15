package com.aeo.mylenses.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.aeo.mylenses.R;
import com.aeo.mylenses.analytics.Analytics;
import com.aeo.mylenses.analytics.Analytics.TrackerName;
import com.aeo.mylenses.dao.AlarmDAO;
import com.aeo.mylenses.dao.LensDAO;
import com.aeo.mylenses.vo.AlarmVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class AlarmFragment extends Fragment {
	private TimePicker timePicker;
	private NumberPicker numberDaysBefore;
	private CheckBox cbRemindEveryDay;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();

		View view = inflater.inflate(R.layout.activity_alarm, container, false);

		timePicker = (TimePicker) view.findViewById(R.id.timePickerAlarm);
		numberDaysBefore = (NumberPicker) view
				.findViewById(R.id.numberPickerDaysBefore);
		cbRemindEveryDay = (CheckBox) view.findViewById(R.id.cbRemindEveryDay);

		setHasOptionsMenu(true);

		setNumberPicker();
		setAlarm();

		/* Google Analytics */
		// Get a Tracker (should auto-report)
		Tracker t = ((Analytics) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName("AlarmFragment");
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
		
		return view;
	}

	private void setNumberPicker() {
		numberDaysBefore.setMinValue(0);
		numberDaysBefore.setMaxValue(30);
		numberDaysBefore.setWrapSelectorWheel(true);
	}

	@Override
	public void onResume() {
		super.onResume();
		save();
	}

	private void setAlarm() {
		AlarmDAO dao = AlarmDAO.getInstance(context);
		AlarmVO vo = dao.getAlarm();
		if (vo == null) {
			timePicker.setCurrentHour(0);
			timePicker.setCurrentMinute(0);
			numberDaysBefore.setValue(0);
			cbRemindEveryDay.setChecked(true);
		} else {
			timePicker.setCurrentHour(vo.getHour());
			timePicker.setCurrentMinute(vo.getMinute());
			numberDaysBefore.setValue(vo.getDaysBefore());
			cbRemindEveryDay.setChecked(vo.getRemindEveryDay() == 1 ? true
					: false);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		save();
	}

	private void save() {
		AlarmVO vo = new AlarmVO();
		vo.setHour(timePicker.getCurrentHour());
		vo.setMinute(timePicker.getCurrentMinute());
		vo.setDaysBefore(numberDaysBefore.getValue());
		vo.setRemindEveryDay(cbRemindEveryDay.isChecked() ? 1 : 0);

		AlarmDAO dao = AlarmDAO.getInstance(context);
		if (dao.getAlarm() == null) {
			dao.insert(vo);
		} else {
			dao.update(vo);
		}

		int idLenses = LensDAO.getInstance(context).getLastIdLens();
		if (idLenses != 0) {
			dao.setAlarm(idLenses);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem menuItemInsert = menu.findItem(R.id.menuInsertLens);
		menuItemInsert.setVisible(false);

		MenuItem menuItemHelp = menu.findItem(R.id.menuHelp);
		menuItemHelp.setVisible(false);
		
		super.onCreateOptionsMenu(menu, inflater);
	}

}
