package com.aeo.mylenses.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.app.backup.BackupManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.aeo.mylenses.R;
import com.aeo.mylenses.adapter.DrawerAdapter;
import com.aeo.mylenses.dao.AlarmDAO;
import com.aeo.mylenses.dao.LensDAO;
import com.aeo.mylenses.dao.LensesDataDAO;
import com.aeo.mylenses.fragment.AlarmFragment;
import com.aeo.mylenses.fragment.DaysFragment;
import com.aeo.mylenses.fragment.HistoryFragment;
import com.aeo.mylenses.fragment.ListReplaceLensFragment;
import com.aeo.mylenses.vo.LensesVO;
import com.google.android.gms.analytics.GoogleAnalytics;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	ViewPager mViewPager;

	// public static Spinner spinnerLeft;
	// public static Spinner spinnerRight;
	// public static Button btnDateLeft;
	// public static Button btnDateRight;
	// public static NumberPicker numberPickerLeft;
	// public static NumberPicker numberPickerRight;
	// public static Menu menus;
	// public static CheckBox cbInUseLeft;
	// public static CheckBox cbInUseRight;

	private String[] drawerListViewItems;
	private ListView drawerListView;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	// Object for intrinsic lock
	public static final Object sDataLock = new Object();

	/** Also cache a reference to the Backup Manager */
	BackupManager mBackupManager;

	private boolean doubleBackToExitPressedOnce;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// get list items from strings.xml
		drawerListViewItems = getResources().getStringArray(R.array.array_menu);
		mDrawerTitle = getTitle();
		mTitle = drawerListViewItems[0];

		// get ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.left_drawer);

		String[] listMenu = getResources().getStringArray(R.array.array_menu);

		List<String> listDrawerItem = new ArrayList<String>();
		for (String menu : listMenu) {
			listDrawerItem.add(menu);
		}

		// Set the adapter for the list view
		DrawerAdapter adapter = new DrawerAdapter(this, R.layout.drawer_item,
				listDrawerItem);
		drawerListView.setAdapter(adapter);

		// App Icon
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		actionBarDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		drawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {
			/**
			 * Called when a drawer has settled in a completely closed state.
			 */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(mTitle);
			}

			/**
			 * Called when a drawer has settled in a completely open state.
			 */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

				getActionBar().setTitle(mDrawerTitle);
			}
		};

		// Set actionBarDrawerToggle as the DrawerListener
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// just styling option add shadow the right edge of the drawer
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		drawerListView.setOnItemClickListener(new DrawerItemClickListener());

		if (savedInstanceState == null) {
			selectItem(0);
		}

		/** It is handy to keep a BackupManager cached */
		mBackupManager = new BackupManager(this);

		setNullToViews();

		/* Google Analytics */
/*		// Get a Tracker (should auto-report)
		Tracker t = ((Analytics) getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName("MainActivity");
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		// MainActivity.menus = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		actionBarDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());

		// spinnerLeft = (Spinner) findViewById(R.id.spinnerLeft);
		// spinnerRight = (Spinner) findViewById(R.id.spinnerRight);
		// numberPickerLeft = (NumberPicker)
		// findViewById(R.id.numberPickerLeft);
		// numberPickerRight = (NumberPicker)
		// findViewById(R.id.numberPickerRight);
		// btnDateLeft = (Button) findViewById(R.id.btnDateLeft);
		// btnDateRight = (Button) findViewById(R.id.btnDateRight);
		// cbInUseLeft = (CheckBox) findViewById(R.id.cbxWearLeft);
		// cbInUseRight = (CheckBox) findViewById(R.id.cbxWearRight);
		//
		// if (btnDateLeft != null) {
		// outState.putString("btnDateLeft", btnDateLeft.getText().toString());
		// }
		// if (btnDateRight != null) {
		// outState.putString("btnDateRight", btnDateRight.getText()
		// .toString());
		// }
		// if (numberPickerLeft != null) {
		// outState.putInt("numberPickerLeft", numberPickerLeft.getValue());
		// }
		// if (numberPickerRight != null) {
		// outState.putInt("numberPickerRight", numberPickerRight.getValue());
		// }
		// if (spinnerLeft != null) {
		// outState.putInt("spinnerLeft",
		// spinnerLeft.getSelectedItemPosition());
		// }
		// if (spinnerRight != null) {
		// outState.putInt("spinnerRight",
		// spinnerRight.getSelectedItemPosition());
		// }
		// if (cbInUseLeft != null) {
		// outState.putBoolean("cbInUseLeft", cbInUseLeft.isChecked());
		// }
		// if (cbInUseRight != null) {
		// outState.putBoolean("cbInUseRight", cbInUseRight.isChecked());
		// }
		// super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		int idLenses = ListReplaceLensFragment.listLenses == null ? LensDAO
				.getInstance(this).getLastIdLens()
				: ListReplaceLensFragment.listLenses.get(0).getId();

		if (idLenses != 0) {
			AlarmDAO alarmDAO = AlarmDAO.getInstance(this);
			alarmDAO.setAlarm(idLenses);

			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notificationManager.cancel(R.string.app_name);
		}
	}

	private void setNullToViews() {
		// btnDateLeft = null;
		// btnDateRight = null;
		// spinnerLeft = null;
		// spinnerRight = null;
		// numberPickerLeft = null;
		// numberPickerRight = null;
		// cbInUseLeft = null;
		// cbInUseRight = null;
	}

	private void shop() {
		LensesDataDAO lensesDataDAO = LensesDataDAO.getInstance(this);
		LensesVO lensesVO = lensesDataDAO
				.getById(lensesDataDAO.getLastIdLens());

		if (lensesVO != null) {
			String urlLeft = lensesVO.getBuy_site_left();
			String urlRight = lensesVO.getBuy_site_right();

			if ((urlLeft == null || "".equals(urlLeft))
					&& (urlRight == null || "".equals(urlRight))) {
				showAlertDialog();
			} else if (urlLeft.equals(urlRight)) {
				if (!urlLeft.startsWith("http://")
						&& !urlLeft.startsWith("https://")) {
					urlLeft = "http://" + urlLeft;
				}

				Uri uri = Uri.parse(urlLeft);
				startActivity(new Intent(Intent.ACTION_VIEW, uri));
			} else {
				if (urlLeft != null && !"".equals(urlLeft)) {
					if (!urlLeft.startsWith("http://")
							&& !urlLeft.startsWith("https://")) {
						urlLeft = "http://" + urlLeft;
					}
					Uri uriLeft = Uri.parse(urlLeft);
					startActivity(new Intent(Intent.ACTION_VIEW, uriLeft));
				}
				if (urlRight != null && !"".equals(urlRight)) {
					if (!urlRight.startsWith("http://")
							&& !urlRight.startsWith("https://")) {
						urlRight = "http://" + urlRight;
					}

					Uri uriRight = Uri.parse(urlRight);
					startActivity(new Intent(Intent.ACTION_VIEW, uriRight));
				}
			}
		} else {
			showAlertDialog();
		}
	}

	// Opening browser
	private void showAlertDialog() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.msg_no_buy_site);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.btn_yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri
								.parse("http://www.google.com")));
					}
				});
		builder.setNegativeButton(R.string.btn_no, null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	// OnClick listener of Navigator Drawer Item
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		switch (position) {
		case 0:
			replaceFragment(new DaysFragment());
			mTitle = drawerListViewItems[position];
			setTitle(mTitle);
			break;
		case 1:
			replaceFragment(new ListReplaceLensFragment());
			mTitle = drawerListViewItems[position];
			setTitle(mTitle);
			break;
		case 2:
			startActivity(new Intent(getBaseContext(), LensesActivity.class));
			break;
		case 3:
			replaceFragment(new AlarmFragment());
			mTitle = drawerListViewItems[position];
			setTitle(mTitle);
			break;
		case 4:
			replaceFragment(new HistoryFragment());
			mTitle = drawerListViewItems[position];
			setTitle(mTitle);
			break;
		case 5:
			shop();
			break;
		default:
			break;
		}
		// Highlight the selected item, update the title, and close the drawer
		drawerListView.setItemChecked(position, true);
		drawerLayout.closeDrawer(drawerListView);
	}

	private void replaceFragment(Fragment fragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction trans = fm.beginTransaction();

		trans.replace(R.id.fragment_container, fragment);

		/*
		 * IMPORTANT: The following lines allow us to add the fragment to the
		 * stack and return to it later, by pressing back
		 */
		trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

		// remove back stack
		// getFragmentManager().popBackStack(null,
		// FragmentManager.POP_BACK_STACK_INCLUSIVE);

		// trans.addToBackStack(null);

		trans.commit();
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, R.string.msg_press_once_again_to_exit,
				Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	public void showMsgUnits(View view) {

		Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.msg_units);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.btn_ok, null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	protected void onStart() {
		// Get an Analytics tracker to report app starts & uncaught exceptions
		// etc.
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		// Stop the analytics tracking
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
		super.onStop();
	}
}
