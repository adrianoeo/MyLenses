package com.aeo.mylenses.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.aeo.mylenses.R;
import com.aeo.mylenses.analytics.Analytics;
import com.aeo.mylenses.analytics.Analytics.TrackerName;
import com.aeo.mylenses.dao.AlarmDAO;
import com.aeo.mylenses.dao.LensDAO;
import com.aeo.mylenses.vo.LensVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

@SuppressLint("SimpleDateFormat")
public class LensFragment extends DialogFragment {
	private static Button btnDateLeft;
	private static Button btnDateRight;
	private DatePickerFragment fragmentDate;
	private static NumberPicker numberPickerLeft;
	private static NumberPicker numberPickerRight;
	private static CheckBox cbInUseLeft;
	private static CheckBox cbInUseRight;
	private static CheckBox cbCountUnitLeft;
	private static CheckBox cbCountUnitRight;
	private static TextView tvIdLens;

	public static final String DATE_LEFT_EYE = "DATE_LEFT_EYE";
	public static final String DATE_RIGHT_EYE = "DATE_RIGHT_EYE";
	public static final String KEY_ID_LENS = "KEY_ID_LENS";

	private View view;
	private static Spinner spinnerLeft;
	private static Spinner spinnerRight;
	private MenuItem menuItemEdit;
	private MenuItem menuItemSave;
	private MenuItem menuItemCancel;
	private MenuItem menuItemDelete;
	public static int idLenses;

	private Context context;

	public static LensVO lensVO;

	public static LensFragment newInstance(int idLens) {
		LensFragment lensFragment = new LensFragment();
		Bundle args = new Bundle();
		args.putInt(KEY_ID_LENS, idLens);
		lensFragment.setArguments(args);
		return lensFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		idLenses = getArguments() != null ? getArguments().getInt(KEY_ID_LENS)
				: 0;
	}

	@Override
	public void onResume() {
		super.onResume();
		idLenses = getArguments() != null ? getArguments().getInt(KEY_ID_LENS)
				: 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.fragment_lens, container, false);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		context = getActivity();

		spinnerLeft = (Spinner) view.findViewById(R.id.spinnerLeft);
		spinnerRight = (Spinner) view.findViewById(R.id.spinnerRight);
		numberPickerLeft = (NumberPicker) view
				.findViewById(R.id.numberPickerLeft);
		numberPickerRight = (NumberPicker) view
				.findViewById(R.id.numberPickerRight);
		btnDateLeft = (Button) view.findViewById(R.id.btnDateLeft);
		btnDateRight = (Button) view.findViewById(R.id.btnDateRight);
		cbInUseLeft = (CheckBox) view.findViewById(R.id.cbxWearLeft);
		cbInUseRight = (CheckBox) view.findViewById(R.id.cbxWearRight);
		cbCountUnitLeft = (CheckBox) view.findViewById(R.id.cbxCountUnitLeft);
		cbCountUnitRight = (CheckBox) view.findViewById(R.id.cbxCountUnitRight);
		tvIdLens = (TextView) view.findViewById(R.id.tvIdLens);

		btnDateLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar date = Calendar.getInstance();
				try {
					date.setTime(new SimpleDateFormat("dd/MM/yyyy")
							.parse(btnDateLeft.getText().toString()));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				fragmentDate = DatePickerFragment.newInstance(/*
															 * LensFragment.this,
															 */DATE_LEFT_EYE,
						date.get(Calendar.YEAR), date.get(Calendar.MONTH),
						date.get(Calendar.DAY_OF_MONTH));
				fragmentDate.show(getFragmentManager(), "datePickerLeft");
			}
		});

		btnDateRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar date = Calendar.getInstance();
				try {
					date.setTime(new SimpleDateFormat("dd/MM/yyyy")
							.parse(btnDateRight.getText().toString()));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				fragmentDate = DatePickerFragment.newInstance(/*
															 * LensFragment.this,
															 */DATE_RIGHT_EYE,
						date.get(Calendar.YEAR), date.get(Calendar.MONTH),
						date.get(Calendar.DAY_OF_MONTH));

				fragmentDate.show(getFragmentManager(), "datePickerRight");
			}
		});

		setHasOptionsMenu(true);

		// setDate();
		setNumberPicker();
		setSpinnerDiscard();
		setLensValues();

		enableControls(menuItemEdit != null && menuItemEdit.isVisible());

		/* Google Analytics */
		// Get a Tracker (should auto-report)
		Tracker t = ((Analytics) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName("LensFragment");
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
		
		return view;
	}

	private void setSpinnerDiscard() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				context, R.array.discard_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerLeft.setAdapter(adapter);
		spinnerRight.setAdapter(adapter);

		spinnerLeft.setSelection(1);
		spinnerRight.setSelection(1);
	}

	private void setNumberPicker() {
		numberPickerLeft.setMinValue(1);
		numberPickerLeft.setMaxValue(100);
		numberPickerLeft.setWrapSelectorWheel(false);

		numberPickerRight.setMinValue(1);
		numberPickerRight.setMaxValue(100);
		numberPickerRight.setWrapSelectorWheel(false);
	}

	private void setDate() {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		String strDate = new StringBuilder(String.format("%02d", day))
				.append("/").append(String.format("%02d", month + 1))
				.append("/").append(String.valueOf(year)).toString();

		btnDateLeft.setText(strDate);
		btnDateRight.setText(strDate);
	}

	private void setLensValues() {
		// if (MainActivity.btnDateLeft == null
		// || MainActivity.btnDateRight == null) {
		LensDAO dao = LensDAO.getInstance(context);
		lensVO = dao.getById(idLenses);
		if (lensVO != null) {
			btnDateLeft.setText(lensVO.getDateLeft());
			btnDateRight.setText(lensVO.getDateRight());
			numberPickerLeft.setValue(lensVO.getExpirationLeft());
			numberPickerRight.setValue(lensVO.getExpirationRight());
			spinnerLeft.setSelection(lensVO.getTypeLeft());
			spinnerRight.setSelection(lensVO.getTypeRight());
			cbInUseLeft.setChecked(lensVO.getInUseLeft() == 1 ? true : false);
			cbInUseRight.setChecked(lensVO.getInUseRight() == 1 ? true : false);
			cbCountUnitLeft.setChecked(lensVO.getCountUnitLeft() == 1 ? true
					: false);
			cbCountUnitRight.setChecked(lensVO.getCountUnitRight() == 1 ? true
					: false);
			tvIdLens.setText(lensVO.getId().toString());
		} else {
			setDate();
			// setNumberPicker();
			// setSpinnerDiscard();
			cbInUseLeft.setChecked(true);
			cbInUseRight.setChecked(true);
			cbCountUnitLeft.setChecked(true);
			cbCountUnitRight.setChecked(true);
			setLensVO();
		}
		/*
		 * } else { btnDateLeft.setText(MainActivity.btnDateLeft.getText());
		 * btnDateRight.setText(MainActivity.btnDateRight.getText());
		 * numberPickerLeft.setValue(MainActivity.numberPickerLeft.getValue());
		 * numberPickerRight.setValue(MainActivity.numberPickerRight
		 * .getValue()); spinnerLeft.setSelection(MainActivity.spinnerLeft
		 * .getSelectedItemPosition());
		 * spinnerRight.setSelection(MainActivity.spinnerRight
		 * .getSelectedItemPosition());
		 * cbInUseLeft.setChecked(MainActivity.cbInUseLeft.isChecked());
		 * cbInUseRight.setChecked(MainActivity.cbInUseRight.isChecked()); }
		 */

	}

	private void enableControls(boolean enabled) {
		btnDateLeft.setEnabled(enabled);
		btnDateRight.setEnabled(enabled);
		numberPickerLeft.setEnabled(enabled);
		numberPickerRight.setEnabled(enabled);
		spinnerLeft.setEnabled(enabled);
		spinnerRight.setEnabled(enabled);
		cbInUseLeft.setEnabled(enabled);
		cbInUseRight.setEnabled(enabled);
		cbCountUnitLeft.setEnabled(enabled);
		cbCountUnitRight.setEnabled(enabled);
	}

	private void enableMenuEdit(boolean enabled) {
		if (menuItemEdit != null) {
			menuItemEdit.setVisible(enabled);
		}
		if (menuItemDelete != null) {
			menuItemDelete.setVisible(enabled);
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

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.time_lenses, menu);
		menuItemEdit = menu.findItem(R.id.menuEditLenses);
		menuItemSave = menu.findItem(R.id.menuSaveLenses);
		menuItemCancel = menu.findItem(R.id.menuCancelLenses);
		menuItemDelete = menu.findItem(R.id.menuDeleteLenses);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (idLenses == 0) {
			enableMenuEdit(false);
			enableMenuSaveCancel(true);
			enableControls(true);
		} else {
			enableMenuSaveCancel(false);
			enableMenuEdit(LensDAO.getInstance(context).getLastIdLens() == idLenses);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuSaveLenses:
			saveLens();
			getActivity().finish();
			return true;
		case R.id.menuEditLenses:
			enableControls(true);
			item.setVisible(false);
			enableMenuEdit(false);
			enableMenuSaveCancel(true);
			return true;
		case R.id.menuCancelLenses:
			getActivity().finish();
			return true;
		case R.id.menuDeleteLenses:
			deleteLens(idLenses);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void saveLens() {
		/*
		 * lensVO = new LensVO();
		 * 
		 * lensVO.setDateLeft(btnDateLeft.getText().toString());
		 * lensVO.setDateRight(btnDateRight.getText().toString());
		 * lensVO.setExpirationLeft(numberPickerLeft.getValue());
		 * lensVO.setExpirationRight(numberPickerRight.getValue());
		 * lensVO.setTypeLeft(spinnerLeft.getSelectedItemPosition());
		 * lensVO.setTypeRight(spinnerRight.getSelectedItemPosition());
		 * lensVO.setInUseLeft(cbInUseLeft.isChecked() ? 1 : 0);
		 * lensVO.setInUseRight(cbInUseRight.isChecked() ? 1 : 0);
		 */

		// setLensVO();

		// LensDAO lensDAO = LensDAO.getInstance(context);
		// AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
		// if (existsLens()) {
		// lensVO.setId(idLenses);
		// if (!lensVO.equals(lensDAO.getById(idLenses))) {
		// if (lensDAO.update(lensVO)) {
		// HistoryDAO.getInstance(context).insert();
		// alarmDAO.setAlarm(idLenses);
		// }
		// }
		// } else {
		// if (lensDAO.insert(lensVO)) {
		// alarmDAO.setAlarm(lensDAO.getLastIdLens());
		// HistoryDAO.getInstance(context).insert();
		// }
		// }

		LensDAO lensDAO = LensDAO.getInstance(context);
		lensDAO.save(setLensVO());

	}

	public static LensVO setLensVO() {
		lensVO = new LensVO();

		lensVO.setDateLeft(btnDateLeft.getText().toString());
		lensVO.setDateRight(btnDateRight.getText().toString());
		lensVO.setExpirationLeft(numberPickerLeft.getValue());
		lensVO.setExpirationRight(numberPickerRight.getValue());
		lensVO.setTypeLeft(spinnerLeft.getSelectedItemPosition());
		lensVO.setTypeRight(spinnerRight.getSelectedItemPosition());
		lensVO.setInUseLeft(cbInUseLeft.isChecked() ? 1 : 0);
		lensVO.setInUseRight(cbInUseRight.isChecked() ? 1 : 0);
		lensVO.setCountUnitLeft(cbCountUnitLeft.isChecked() ? 1 : 0);
		lensVO.setCountUnitRight(cbCountUnitRight.isChecked() ? 1 : 0);

		if (!"".equals(tvIdLens.getText().toString())) {
			lensVO.setId(Integer.valueOf(tvIdLens.getText().toString()));
		}
		return lensVO;
	}

	// private boolean existsLens() {
	// LensDAO dao = LensDAO.getInstance(context);
	// return dao.getById(idLenses) != null;
	// }

	private void deleteLens(final int id) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.msgDelete);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.btn_yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LensDAO lensDAO = LensDAO.getInstance(context);
						lensDAO.delete(id);

						AlarmDAO alarmDAO = AlarmDAO.getInstance(context);
						alarmDAO.setAlarm(lensDAO.getLastIdLens());

						getActivity().finish();
					}
				});
		builder.setNegativeButton(R.string.btn_no, null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser && btnDateLeft != null && btnDateLeft.isEnabled()) {
			saveLens();
			// Toast.makeText(context, R.string.msgSaved, Toast.LENGTH_SHORT)
			// .show();
			getActivity().finish();

		}
	}

}
