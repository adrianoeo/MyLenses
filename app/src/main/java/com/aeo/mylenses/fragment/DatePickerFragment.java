package com.aeo.mylenses.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.DatePicker;

import com.aeo.mylenses.R;
import com.aeo.mylenses.adapter.LensesCollectionPagerAdapter;
import com.aeo.mylenses.dao.LensDAO;
import com.aeo.mylenses.vo.LensVO;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	private static final String DAY = "DAY";
	private static final String MONTH = "MONTH";
	private static final String YEAR = "YEAR";
	private static final String TAG = "TAG";

	private int day;
	private int month;
	private int year;

	private Button btnDateLeft;
	private Button btnDateRight;
	private Button btnDateIniLeft;
	private Button btnDateIniRight;
	private String tag;

	public static DatePickerFragment newInstance(String tagLens, int year,
			int monht, int day) {

		DatePickerFragment datePickerFragment = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putString(TAG, tagLens);
		args.putInt(YEAR, year);
		args.putInt(MONTH, monht);
		args.putInt(DAY, day);
		datePickerFragment.setArguments(args);
		return datePickerFragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		int year = getArguments().getInt(YEAR);
		int month = getArguments().getInt(MONTH);
		int day = getArguments().getInt(DAY);
		tag = getArguments().getString(TAG);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {

		Fragment fragment = null;

		// Fragment of lenses/replacement date
		if (tag.equals(LensFragment.DATE_LEFT_EYE)
				|| tag.equals(LensFragment.DATE_RIGHT_EYE)) {
			fragment = getActivity().getSupportFragmentManager()
					.findFragmentByTag(ListReplaceLensFragment.TAG_LENS);

		} // Fragment of left lens data
		else if (tag.equals(LensLeftEyeFragment.DATE_LENS_LEFT)) {
			ViewPager mViewPager = (ViewPager) getActivity().findViewById(
					R.id.pagerLenses);
			int index = mViewPager.getCurrentItem();
			LensesCollectionPagerAdapter adapter = ((LensesCollectionPagerAdapter) mViewPager
					.getAdapter());
			fragment = (LensLeftEyeFragment) adapter.getFragment(index);

		} // Fragment of right lens data
		else if (tag.equals(LensRightEyeFragment.DATE_LENS_RIGHT)) {
			ViewPager mViewPager = (ViewPager) getActivity().findViewById(
					R.id.pagerLenses);
			int index = mViewPager.getCurrentItem();
			LensesCollectionPagerAdapter adapter = ((LensesCollectionPagerAdapter) mViewPager
					.getAdapter());
			fragment = (LensRightEyeFragment) adapter.getFragment(index);
		}

		this.day = day;
		this.month = month + 1;
		this.year = year;

		String strDate = new StringBuilder(String.format("%02d", day))
				.append("/").append(String.format("%02d", month + 1))
				.append("/").append(String.valueOf(year)).toString();

		if (tag.equals(LensFragment.DATE_LEFT_EYE)) {
			btnDateLeft = (Button) fragment.getView().findViewById(
					R.id.btnDateLeft);
			btnDateLeft.setText(strDate);
		} else if (tag.equals(LensFragment.DATE_RIGHT_EYE)) {
			btnDateRight = (Button) fragment.getView().findViewById(
					R.id.btnDateRight);
			btnDateRight.setText(strDate);
		} else if (tag.equals(LensLeftEyeFragment.DATE_LENS_LEFT)) {
			btnDateIniLeft = (Button) fragment.getView().findViewById(
					R.id.btnDateIniLeft);
			btnDateIniLeft.setText(strDate);
		} else if (tag.equals(LensRightEyeFragment.DATE_LENS_RIGHT)) {
			btnDateIniRight = (Button) fragment.getView().findViewById(
					R.id.btnDateIniRight);
			btnDateIniRight.setText(strDate);
		}
	}

	@SuppressLint("SimpleDateFormat")
	private Date getDate() {
		LensDAO dao = LensDAO.getInstance(getActivity());
		LensVO lensVO = dao.getById(LensFragment.idLenses);
		if (lensVO != null) {
			if (tag.equals(LensFragment.DATE_LEFT_EYE)) {
				try {
					return new SimpleDateFormat("dd/MM/yyyy").parse(lensVO
							.getDateLeft());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				try {
					return new SimpleDateFormat("dd/MM/yyyy").parse(lensVO
							.getDateRight());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public int getDay() {
		return day;
	}

	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

}
