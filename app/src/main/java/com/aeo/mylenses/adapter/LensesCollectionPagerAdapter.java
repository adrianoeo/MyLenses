package com.aeo.mylenses.adapter;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.aeo.mylenses.fragment.LensLeftEyeFragment;
import com.aeo.mylenses.fragment.LensRightEyeFragment;

public class LensesCollectionPagerAdapter extends FragmentPagerAdapter {

	private static final int NUMBER_FRAGMENTS = 2;
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();

	public LensesCollectionPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		if (position == 0) {
			fragment = new LensLeftEyeFragment();
			mPageReferenceMap.put(position, fragment);
			// FragmentTransaction trans =
			// fragment.getFragmentManager().beginTransaction();
			// trans.add(fragment, LensLeftEyeFragment.DATE_LENS_LEFT);
			// trans.replace(R.id.root_frame, lensFragment, TAG_LENS);
			// trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			// trans.commit();
		} else {
			fragment = new LensRightEyeFragment();
			mPageReferenceMap.put(position, fragment);
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return NUMBER_FRAGMENTS;
	}

	public Fragment getFragment(int key) {
		return mPageReferenceMap.get(key);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
		mPageReferenceMap.remove(position);
	}
}
