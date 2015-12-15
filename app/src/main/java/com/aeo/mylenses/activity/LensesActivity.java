package com.aeo.mylenses.activity;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ShareActionProvider;
import android.widget.Spinner;

import com.aeo.mylenses.R;
import com.aeo.mylenses.adapter.LensesCollectionPagerAdapter;
import com.aeo.mylenses.analytics.Analytics;
import com.aeo.mylenses.analytics.Analytics.TrackerName;
import com.aeo.mylenses.dao.HistoryDAO;
import com.aeo.mylenses.dao.LensDAO;
import com.aeo.mylenses.dao.LensesDataDAO;
import com.aeo.mylenses.vo.LensesVO;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class LensesActivity extends FragmentActivity {

	LensesCollectionPagerAdapter mCollectionPagerAdapter;
	ViewPager mViewPager;

	private MenuItem menuItemEdit;
	private MenuItem menuItemSave;
	private MenuItem menuItemDelete;
	private MenuItem menuItemCancel;
	private MenuItem menuItemShare;

	private EditText editTextDescLeft;
	private AutoCompleteTextView editTextBrandLeft;
	private EditText editTextBuySiteLeft;
	private Spinner spinnerTypeLensLeft;
	private Spinner spinnerPowerLeft;
	private Spinner spinnerAddLeft;
	private Spinner spinnerAxisLeft;
	private Spinner spinnerCylinderLeft;
	private Spinner spinnerNumUnitsLeft;
	private Button btnDateIniLeft;

	private EditText editTextDescRight;
	private AutoCompleteTextView editTextBrandRight;
	private EditText editTextBuySiteRight;
	private Spinner spinnerTypeLensRight;
	private Spinner spinnerPowerRight;
	private Spinner spinnerAddRight;
	private Spinner spinnerAxisRight;
	private Spinner spinnerCylinderRight;
	private Spinner spinnerNumUnitsRight;
	private Button btnDateIniRight;

	private LinearLayout layoutLeft;
	private LinearLayout layoutRight;
	private ShareActionProvider mShareActionProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lenses);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mCollectionPagerAdapter = new LensesCollectionPagerAdapter(
				getSupportFragmentManager());

		final ActionBar actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Set up the ViewPager, attaching the adapter
		mViewPager = (ViewPager) findViewById(R.id.pagerLenses);
		mViewPager.setAdapter(mCollectionPagerAdapter);
		mViewPager.setOffscreenPageLimit(2);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}

				});

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
			}
		};

		actionBar.addTab(actionBar.newTab().setText(R.string.tabLeftLens)
				.setTabListener(tabListener));

		actionBar.addTab(actionBar.newTab().setText(R.string.tabRightLens)
				.setTabListener(tabListener));

		// Get a Tracker (should auto-report)
		Tracker t = ((Analytics) getApplication())
				.getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName("LensesActivity");

		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lenses, menu);
		menuItemEdit = menu.findItem(R.id.menuEditLenses);
		menuItemSave = menu.findItem(R.id.menuSaveLenses);
		menuItemDelete = menu.findItem(R.id.menuDeleteLenses);
		menuItemCancel = menu.findItem(R.id.menuCancelLenses);
		menuItemShare = menu.findItem(R.id.menuShareDataLenses);
		mShareActionProvider = (ShareActionProvider) menuItemShare
				.getActionProvider();
		setShareIntent(getDefaultIntent());
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menuItemDelete.setVisible(false);
		enableMenuSaveCancel(false);
		return super.onPrepareOptionsMenu(menu);
	}

	// Call to update the share intent
	private void setShareIntent(Intent shareIntent) {
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(shareIntent);
		}
	}

	private Intent getDefaultIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT,
				getString(R.string.str_email_subject_data_lenses));
		intent.putExtra(Intent.EXTRA_TEXT, getDataLenses());
		return intent;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		layoutLeft = (LinearLayout) findViewById(R.id.id_layout_Lens_left);
		layoutRight = (LinearLayout) findViewById(R.id.id_layout_Lens_right);
		switch (item.getItemId()) {
		case R.id.menuEditLenses:
			enableControls(true, layoutLeft);
			enableControls(true, layoutRight);
			enableMenuEdit(false);
			enableMenuSaveCancel(true);
			editTextBrandLeft = (AutoCompleteTextView) findViewById(R.id.editTextLeftBrand);
			editTextBrandLeft.setFocusable(true);
			editTextBrandLeft.setFocusableInTouchMode(true);
			return true;
		case R.id.menuSaveLenses:
			enableControls(false, layoutLeft);
			enableControls(false, layoutRight);
			save();
			enableMenuEdit(true);
			enableMenuSaveCancel(false);
			return true;
		case R.id.menuCancelLenses:
			enableControls(false, layoutLeft);
			enableControls(false, layoutRight);
			enableMenuEdit(true);
			enableMenuSaveCancel(false);
			return true;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void enableMenuEdit(boolean enabled) {
		if (menuItemEdit != null) {
			menuItemEdit.setVisible(enabled);
		}
		if (menuItemShare != null) {
			menuItemShare.setVisible(enabled);
		}
	}

	private void enableMenuSaveCancel(boolean enabled) {
		if (menuItemSave != null) {
			menuItemSave.setVisible(enabled);
		}
		if (menuItemCancel != null) {
			menuItemCancel.setVisible(enabled);
		}
	}

	private void enableControls(boolean enabled, ViewGroup view) {
		if (view != null) {
			for (int i = 0; i < view.getChildCount(); i++) {
				View child = view.getChildAt(i);
				child.setEnabled(enabled);
				if (child instanceof ViewGroup) {
					enableControls(enabled, (ViewGroup) child);
				}
			}
		}
	}

	private void save() {
		editTextDescLeft = (EditText) findViewById(R.id.EditTextDescLeft);
		editTextBrandLeft = (AutoCompleteTextView) findViewById(R.id.editTextLeftBrand);
		editTextBuySiteLeft = (EditText) findViewById(R.id.EditTextBuySiteLeft);
		spinnerTypeLensLeft = (Spinner) findViewById(R.id.spinnerTypeLensLeft);
		spinnerPowerLeft = (Spinner) findViewById(R.id.spinnerPowerLeft);
		spinnerAddLeft = (Spinner) findViewById(R.id.spinnerAddLeft);
		spinnerAxisLeft = (Spinner) findViewById(R.id.spinnerAxisLeft);
		spinnerCylinderLeft = (Spinner) findViewById(R.id.spinnerCylinderLeft);
		spinnerNumUnitsLeft = (Spinner) findViewById(R.id.spinnerNumUnitsLeft);
		btnDateIniLeft = (Button) findViewById(R.id.btnDateIniLeft);

		editTextDescRight = (EditText) findViewById(R.id.EditTextDescRight);
		editTextBrandRight = (AutoCompleteTextView) findViewById(R.id.editTextRightBrand);
		editTextBuySiteRight = (EditText) findViewById(R.id.EditTextBuySiteRight);
		spinnerTypeLensRight = (Spinner) findViewById(R.id.spinnerTypeLensRight);
		spinnerPowerRight = (Spinner) findViewById(R.id.spinnerPowerRight);
		spinnerAddRight = (Spinner) findViewById(R.id.spinnerAddRight);
		spinnerAxisRight = (Spinner) findViewById(R.id.spinnerAxisRight);
		spinnerCylinderRight = (Spinner) findViewById(R.id.spinnerCylinderRight);
		spinnerNumUnitsRight = (Spinner) findViewById(R.id.spinnerNumUnitsRight);
		btnDateIniRight = (Button) findViewById(R.id.btnDateIniRight);

		LensesVO vo = new LensesVO();

		/* Left Lens */
		vo.setBrand_left(editTextBrandLeft.getText().toString());
		vo.setDescription_left(editTextDescLeft.getText().toString());
		vo.setBuy_site_left(editTextBuySiteLeft.getText().toString());
		vo.setType_left(String.valueOf(spinnerTypeLensLeft
				.getSelectedItemPosition()));
		vo.setNumber_units_left(spinnerNumUnitsLeft.getSelectedItemPosition());
		vo.setDate_ini_left(btnDateIniLeft.getText().toString());

		// Myopia/Hypermetropia
		if (spinnerTypeLensLeft.getSelectedItemPosition() == 0) {
			vo.setPower_left(String.valueOf(spinnerPowerLeft
					.getSelectedItemPosition()));
			vo.setAdd_left(null);
			vo.setAxis_left(null);
			vo.setCylinder_left(null);
			// Astigmatism
		} else if (spinnerTypeLensLeft.getSelectedItemPosition() == 1) {
			vo.setPower_left(String.valueOf(spinnerPowerLeft
					.getSelectedItemPosition()));
			vo.setCylinder_left(String.valueOf(spinnerCylinderLeft
					.getSelectedItemPosition()));
			vo.setAxis_left(String.valueOf(spinnerAxisLeft
					.getSelectedItemPosition()));
			vo.setAdd_left(null);
			// Multifocal/Presbyopia
		} else if (spinnerTypeLensLeft.getSelectedItemPosition() == 2) {
			vo.setPower_left(String.valueOf(spinnerPowerLeft
					.getSelectedItemPosition()));
			vo.setAdd_left(String.valueOf(spinnerAddLeft
					.getSelectedItemPosition()));
			vo.setAxis_left(null);
			vo.setCylinder_left(null);
			// Colored/No degree
		} else if (spinnerTypeLensLeft.getSelectedItemPosition() == 3) {
			vo.setPower_left(null);
			vo.setAdd_left(null);
			vo.setAxis_left(null);
			vo.setCylinder_left(null);
		}

		/* Right Lens */
		vo.setBrand_right(editTextBrandRight.getText().toString());
		vo.setDescription_right(editTextDescRight.getText().toString());
		vo.setBuy_site_right(editTextBuySiteRight.getText().toString());
		vo.setType_right(String.valueOf(spinnerTypeLensRight
				.getSelectedItemPosition()));
		vo.setNumber_units_right(spinnerNumUnitsRight.getSelectedItemPosition());
		vo.setDate_ini_right(btnDateIniRight.getText().toString());

		// Myopia/Hypermetropia
		if (spinnerTypeLensRight.getSelectedItemPosition() == 0) {
			vo.setPower_right(String.valueOf(spinnerPowerRight
					.getSelectedItemPosition()));
			vo.setAdd_right(null);
			vo.setAxis_right(null);
			vo.setCylinder_right(null);
			// Astigmatism
		} else if (spinnerTypeLensRight.getSelectedItemPosition() == 1) {
			vo.setPower_right(String.valueOf(spinnerPowerRight
					.getSelectedItemPosition()));
			vo.setCylinder_right(String.valueOf(spinnerCylinderRight
					.getSelectedItemPosition()));
			vo.setAxis_right(String.valueOf(spinnerAxisRight
					.getSelectedItemPosition()));
			vo.setAdd_right(null);
			// Multifocal/Presbyopia
		} else if (spinnerTypeLensRight.getSelectedItemPosition() == 2) {
			vo.setPower_right(String.valueOf(spinnerPowerRight
					.getSelectedItemPosition()));
			vo.setAdd_right(String.valueOf(spinnerAddRight
					.getSelectedItemPosition()));
			vo.setAxis_right(null);
			vo.setCylinder_right(null);
			// Colored/No degree
		} else if (spinnerTypeLensRight.getSelectedItemPosition() == 3) {
			vo.setPower_right(null);
			vo.setAdd_right(null);
			vo.setAxis_right(null);
			vo.setCylinder_right(null);
		}

		LensesDataDAO lensesDataDAO = LensesDataDAO.getInstance(this);
		LensDAO lensDAO = new LensDAO(this);

		int idLastItemLensesData = lensesDataDAO.getLastIdLens();
		int idLastItemLens = lensDAO.getLastIdLens();

		// If there are not lenses data then insert. If there are lenses
		// replacement then insert history.
		if (idLastItemLensesData == 0) {
			if (lensesDataDAO.insert(vo)) {
				if (idLastItemLens != 0) {
					HistoryDAO.getInstance(this).insert();
				}
			}
			// If there are lenses data then update. If there are lenses
			// replacement then update history.
		} else {
			vo.setId(idLastItemLensesData);
			if (lensesDataDAO.update(vo)) {
				if (idLastItemLens != 0) {
					HistoryDAO.getInstance(this).update();
				}
			}
		}
	}

	private String getDataLenses() {
		LensesDataDAO dao = LensesDataDAO.getInstance(this);
		LensesVO vo = dao.getById(dao.getLastIdLens());

		StringBuilder text = new StringBuilder();

		if (vo != null) {
			text.append("\n");
			text.append("- ").append(getString(R.string.tabLeftLens))
					.append("\n\n");
			text.append(getLeftText(vo)).append("\n\n");
			text.append("- ").append(getString(R.string.tabRightLens))
					.append("\n\n");
			text.append(getRightText(vo)).append("\n");
		}

		return text.toString();
	}

	private String getLeftText(LensesVO vo) {
		String typeLensLeft = vo.getType_left() == null
				|| "".equals(vo.getType_left()) ? "" : getResources()
				.getStringArray(R.array.array_type_lens)[Integer.valueOf(vo
				.getType_left())];

		String powerLeft = vo.getPower_left() == null
				|| "".equals(vo.getPower_left()) ? "" : getResources()
				.getStringArray(R.array.array_power)[Integer.valueOf(vo
				.getPower_left())];

		String addLeft = vo.getAdd_left() == null
				|| "".equals(vo.getAdd_left()) ? "" : getResources()
				.getStringArray(R.array.array_add)[Integer.valueOf(vo
				.getAdd_left())];

		String axisLeft = vo.getAxis_left() == null
				|| "".equals(vo.getAxis_left()) ? "" : getResources()
				.getStringArray(R.array.array_axis)[Integer.valueOf(vo
				.getAxis_left())];

		String cylinderLeft = vo.getCylinder_left() == null
				|| "".equals(vo.getCylinder_left()) ? "" : getResources()
				.getStringArray(R.array.array_cylinder)[Integer.valueOf(vo
				.getCylinder_left())];

		StringBuilder sbLeft = new StringBuilder();
		sbLeft.append(getString(R.string.lbl_brand)).append(": ")
				.append(vo.getBrand_left() == null ? "" : vo.getBrand_left())
				.append("\n");
		sbLeft.append(getString(R.string.lbl_desc_lens))
				.append(": ")
				.append(vo.getDescription_left() == null ? "" : vo
						.getDescription_left()).append("\n");
		sbLeft.append(getString(R.string.lbl_type_lens)).append(": ")
				.append(typeLensLeft).append("\n");

		// Miophya/Hipermetrophya
		if (vo.getType_left() != null) {
			if ("0".equals(vo.getType_left().toString())) {
				sbLeft.append(getString(R.string.lbl_power)).append(": ")
						.append(powerLeft).append("\n");
				// Astigmatism
			} else if ("1".equals(vo.getType_left().toString())) {
				sbLeft.append(getString(R.string.lbl_power)).append(": ")
						.append(powerLeft).append("\n");
				sbLeft.append(getString(R.string.lbl_cylinder)).append(": ")
						.append(cylinderLeft).append("\n");
				sbLeft.append(getString(R.string.lbl_axis)).append(": ")
						.append(axisLeft).append("\n");
				// Multifocal/Presbyopia
			} else if ("2".equals(vo.getType_left().toString())) {
				sbLeft.append(getString(R.string.lbl_power)).append(": ")
						.append(powerLeft).append("\n");
				sbLeft.append(getString(R.string.lbl_add)).append(": ")
						.append(addLeft).append("\n");
			}
		}
		return sbLeft.toString();
	}

	private String getRightText(LensesVO vo) {
		String typeLensRight = vo.getType_right() == null
				|| "".equals(vo.getType_right()) ? "" : getResources()
				.getStringArray(R.array.array_type_lens)[Integer.valueOf(vo
				.getType_right())];

		String powerRight = vo.getPower_right() == null
				|| "".equals(vo.getPower_right()) ? "" : getResources()
				.getStringArray(R.array.array_power)[Integer.valueOf(vo
				.getPower_right())];

		String addRight = vo.getAdd_right() == null
				|| "".equals(vo.getAdd_right()) ? "" : getResources()
				.getStringArray(R.array.array_add)[Integer.valueOf(vo
				.getAdd_right())];

		String axisRight = vo.getAxis_right() == null
				|| "".equals(vo.getAxis_right()) ? "" : getResources()
				.getStringArray(R.array.array_axis)[Integer.valueOf(vo
				.getAxis_right())];

		String cylinderRight = vo.getCylinder_right() == null
				|| "".equals(vo.getCylinder_right()) ? "" : getResources()
				.getStringArray(R.array.array_cylinder)[Integer.valueOf(vo
				.getCylinder_right())];

		StringBuilder sbRight = new StringBuilder();
		sbRight.append(getString(R.string.lbl_brand)).append(": ")
				.append(vo.getBrand_right() == null ? "" : vo.getBrand_right())
				.append("\n");
		sbRight.append(getString(R.string.lbl_desc_lens))
				.append(": ")
				.append(vo.getDescription_right() == null ? "" : vo
						.getDescription_right()).append("\n");
		sbRight.append(getString(R.string.lbl_type_lens)).append(": ")
				.append(typeLensRight).append("\n");

		// Miophya/Hipermetrophya
		if (vo.getType_right() != null) {
			if ("0".equals(vo.getType_right().toString())) {
				sbRight.append(getString(R.string.lbl_power)).append(": ")
						.append(powerRight).append("\n");
				// Astigmatism
			} else if ("1".equals(vo.getType_right().toString())) {
				sbRight.append(getString(R.string.lbl_power)).append(": ")
						.append(powerRight).append("\n");
				sbRight.append(getString(R.string.lbl_cylinder)).append(": ")
						.append(cylinderRight).append("\n");
				sbRight.append(getString(R.string.lbl_axis)).append(": ")
						.append(axisRight).append("\n");
				// Multifocal/Presbyopia
			} else if ("2".equals(vo.getType_right().toString())) {
				sbRight.append(getString(R.string.lbl_power)).append(": ")
						.append(powerRight).append("\n");
				sbRight.append(getString(R.string.lbl_add)).append(": ")
						.append(addRight).append("\n");
			}
		}
		return sbRight.toString();
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
