package com.aeo.mylenses.fragment;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aeo.mylenses.R;
import com.aeo.mylenses.activity.TimeLensActivity;
import com.aeo.mylenses.adapter.ListReplaceLensBaseAdapter;
import com.aeo.mylenses.analytics.Analytics;
import com.aeo.mylenses.analytics.Analytics.TrackerName;
import com.aeo.mylenses.dao.LensDAO;
import com.aeo.mylenses.vo.LensVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ListReplaceLensFragment extends ListFragment {

	public static List<LensVO> listLenses;
	ListReplaceLensBaseAdapter mListAdapter;
	public static final String TAG_LENS = "TAG_LENS";

	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		List<LensVO> listLens = LensDAO.getInstance(context).getListLens();

		if (listLens != null && listLens.size() == 0) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					inflater.getContext(),
					android.R.layout.simple_list_item_1,
					new String[] { getString(R.string.msg_insert_time_replace) });
			setListAdapter(adapter);
		}

		/* Google Analytics */
		// Get a Tracker (should auto-report)
		Tracker t = ((Analytics) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName("ListReplaceLensFragment");
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);

		context = getActivity();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		TextView idLens = (TextView) v.findViewById(R.id.textViewIdReplaceLens);

		if (idLens != null) {
			startActivity(Integer.valueOf(idLens.getText().toString()));
		}
	}

	private void startActivity(int idLens) {
		Intent intent = new Intent(context, TimeLensActivity.class);
		intent.putExtra("ID_LENS", idLens);
		startActivity(intent);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem menuItemInsert = menu.findItem(R.id.menuInsertLens);
		menuItemInsert.setVisible(true);

		MenuItem menuItemHelp = menu.findItem(R.id.menuHelp);
		menuItemHelp.setVisible(false);
		
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuInsertLens:
			startActivity(new Intent(context, TimeLensActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		List<LensVO> listLens = LensDAO.getInstance(context).getListLens();

		if (listLens != null && listLens.size() > 0) {
			mListAdapter = new ListReplaceLensBaseAdapter(context, listLens);
			setListAdapter(mListAdapter);
		} else {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
					inflater.getContext(),
					android.R.layout.simple_list_item_1,
					new String[] { getString(R.string.msg_insert_time_replace) });
			setListAdapter(adapter);
		}
	}
}
