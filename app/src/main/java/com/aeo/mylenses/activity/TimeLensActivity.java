package com.aeo.mylenses.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.aeo.mylenses.R;
import com.aeo.mylenses.dao.LensDAO;
import com.aeo.mylenses.fragment.LensFragment;
import com.aeo.mylenses.vo.LensVO;
import com.google.android.gms.analytics.GoogleAnalytics;

public class TimeLensActivity extends FragmentActivity {
	public static final String TAG_LENS = "TAG_LENS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time_lens);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("");

		Bundle bundle = getIntent().getExtras();

		if (bundle != null) {
			int idLens = bundle.getInt("ID_LENS");
			replaceFragment(LensFragment.newInstance(idLens));
		} else {
			replaceFragment(new LensFragment());
		}
/*
		// Get a Tracker (should auto-report)
		Tracker t = ((Analytics) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		// Where path is a String representing the screen name.
		t.setScreenName("TimeLensActivity");

		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
*/
	}

	private void replaceFragment(Fragment fragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction trans = fm.beginTransaction();

		trans.replace(R.id.fragment_time_lens_container, fragment, TAG_LENS);
		trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		trans.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			saveLens();
			finish();
			// NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		saveLens();
	}

	@SuppressLint("SimpleDateFormat")
	private void saveLens() {
		LensVO lensVO = LensFragment.setLensVO();

		LensDAO lensDAO = LensDAO.getInstance(this);

		// Save lenses
		lensDAO.save(lensVO);

		// LensDAO lensDAO = LensDAO.getInstance(this);
		// AlarmDAO alarmDAO = AlarmDAO.getInstance(this);
		// int idLens = lensVO.getId() == null ? 0 : lensVO.getId();
		// //lensDAO.getLastIdLens();
		// if (idLens != 0) {
		// lensVO.setId(idLens);
		// if (!lensVO.equals(lensDAO.getById(idLens))) {
		// if (lensDAO.update(lensVO)) {
		// HistoryDAO.getInstance(this).insert();
		// alarmDAO.setAlarm(idLens);
		// }
		// }
		// } else {
		// if (lensDAO.insert(lensVO)) {
		// alarmDAO.setAlarm(idLens);
		// HistoryDAO.getInstance(this).insert();
		// }
		// }
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Get an Analytics tracker to report app starts & uncaught exceptions
		// etc.
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Stop the analytics tracking
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

}
