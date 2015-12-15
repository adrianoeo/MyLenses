package com.aeo.mylenses.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.aeo.mylenses.R;
import com.aeo.mylenses.adapter.HistoryArrayAdapter;
import com.aeo.mylenses.analytics.Analytics;
import com.aeo.mylenses.analytics.Analytics.TrackerName;
import com.aeo.mylenses.dao.AlarmDAO;
import com.aeo.mylenses.dao.HistoryDAO;
import com.aeo.mylenses.dao.LensDAO;
import com.aeo.mylenses.dao.LensesDataDAO;
import com.aeo.mylenses.vo.HistoryVO;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class HistoryFragment extends ListFragment {
	private HistoryArrayAdapter adapter;
	private List<HistoryVO> listHistory = new ArrayList<HistoryVO>();
	private Runnable runnable;
	private Context context;
	private ShareActionProvider mShareActionProvider;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_history, container,
				false);

		setHasOptionsMenu(true);

		context = getActivity();

		runnable = new Runnable() {
			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		};

		startThread();

		/* Google Analytics */
		// Get a Tracker (should auto-report)
		Tracker t = ((Analytics) getActivity().getApplication()).getTracker(TrackerName.APP_TRACKER);
		// Set screen name.
		t.setScreenName("HistoryFragment");
		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ListView listView = getListView();
		listView.setLongClickable(true);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int arg2, long arg3) {

				deletById(((TextView) view.findViewById(R.id.textViewIdHistory))
						.getText().toString());
				return false;
			}
		});

	}

	private void startThread() {
		Thread thread = new Thread(null, runnable, "HistoryBackground");
		thread.start();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			listHistory = HistoryDAO.getInstance(context).getListHistory();

			adapter = new HistoryArrayAdapter(context, R.layout.item_history,
					listHistory);
			adapter.notifyDataSetChanged();
			setListAdapter(adapter);
		}
	};

	public void onListItemClick(android.widget.ListView l, android.view.View v,
			int position, long id) {

		TextView tvIdHistory = (TextView) v
				.findViewById(R.id.textViewIdHistory);
		TextView tvDateHistory = (TextView) v
				.findViewById(R.id.textViewDateHistory);

		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.resume_history);
		dialog.setTitle(tvDateHistory.getText());

		HistoryVO vo = new HistoryVO();
		vo = HistoryDAO.getInstance(context).getById(
				Integer.valueOf(tvIdHistory.getText().toString()));

		/* Left */
		if (vo != null && vo.getIn_use_left() == 1) {
			TextView tvLeftEye = (TextView) dialog
					.findViewById(R.id.tVLensLeft);
			TextView tvResumeLeft = (TextView) dialog
					.findViewById(R.id.tVResumeLeft);

			tvLeftEye.setText(getString(R.string.tabLeftLens));
			tvResumeLeft.setText(getLeftText(vo));

		}

		/* Right */
		if (vo != null && vo.getIn_use_right() == 1) {
			TextView tvRightEye = (TextView) dialog
					.findViewById(R.id.tVLensRight);
			TextView tvResumeRight = (TextView) dialog
					.findViewById(R.id.tVResumeRight);

			tvRightEye.setText(getString(R.string.tabRightLens));
			tvResumeRight.setText(getRightText(vo));
		}
		dialog.show();
	};

	private String getLeftText(HistoryVO vo) {
		String typeTimeLeft = getResources().getStringArray(
				R.array.discard_array)[vo.getType_time_left()];

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
		String numUnitsLeft = getResources().getStringArray(
				R.array.array_num_units)[vo.getNumber_units_left()];

		StringBuilder sbLeft = new StringBuilder();
		sbLeft.append(getString(R.string.dateLeftEye)).append(" ")
				.append(vo.getDate_left()).append("\n");
		sbLeft.append(getString(R.string.lblMonths)).append(" ")
				.append(vo.getExpiration_left()).append(" ")
				.append(typeTimeLeft).append("(s)").append("\n");

		if (vo.getDate_ini_left() != null) {
			sbLeft.append("\n");
			sbLeft.append(getString(R.string.lbl_brand))
					.append(": ")
					.append(vo.getBrand_left() == null ? "" : vo
							.getBrand_left()).append("\n");
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
					sbLeft.append(getString(R.string.lbl_cylinder))
							.append(": ").append(cylinderLeft).append("\n");
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
			sbLeft.append("\n");
			sbLeft.append(getString(R.string.lbl_date_ini_wear2)).append(": ")
					.append(vo.getDate_ini_left()).append("\n");
			sbLeft.append(getString(R.string.lbl_number_units)).append(": ")
					.append(numUnitsLeft).append("\n");
			sbLeft.append(getString(R.string.lbl_buy_site))
					.append(": ")
					.append(vo.getBuy_site_left() == null ? "" : vo
							.getBuy_site_left()).append("\n");
		}
		return sbLeft.toString();

	}

	private String getRightText(HistoryVO vo) {
		String typeTimeRight = getResources().getStringArray(
				R.array.discard_array)[vo.getType_time_right()];
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
		String numUnitsRight = getResources().getStringArray(
				R.array.array_num_units)[vo.getNumber_units_right()];

		StringBuilder sbRight = new StringBuilder();
		sbRight.append(getString(R.string.dateRightEye)).append(" ")
				.append(vo.getDate_right()).append("\n");
		sbRight.append(getString(R.string.lblMonths)).append(" ")
				.append(vo.getExpiration_right()).append(" ")
				.append(typeTimeRight).append("(s)").append("\n");

		if (vo.getDate_ini_right() != null) {
			sbRight.append("\n");
			sbRight.append(getString(R.string.lbl_brand))
					.append(": ")
					.append(vo.getBrand_right() == null ? "" : vo
							.getBrand_right()).append("\n");
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
					sbRight.append(getString(R.string.lbl_cylinder))
							.append(": ").append(cylinderRight).append("\n");
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
			sbRight.append("\n");
			sbRight.append(getString(R.string.lbl_date_ini_wear2)).append(": ")
					.append(vo.getDate_ini_right()).append("\n");
			sbRight.append(getString(R.string.lbl_number_units)).append(": ")
					.append(numUnitsRight).append("\n");
			sbRight.append(getString(R.string.lbl_buy_site))
					.append(": ")
					.append(vo.getBuy_site_right() == null ? "" : vo
							.getBuy_site_right()).append("\n");
		}
		return sbRight.toString();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.history, menu);

		((MenuItem) menu.findItem(R.id.menuInsertLens)).setVisible(false);

		MenuItem menuItemHelp = menu.findItem(R.id.menuHelp);
		menuItemHelp.setVisible(false);

		MenuItem menuItemShare = menu.findItem(R.id.menuShareHistory);
		// Fetch and store ShareActionProvider
		mShareActionProvider = (ShareActionProvider) menuItemShare
				.getActionProvider();
		setShareIntent(getDefaultIntent());

		super.onCreateOptionsMenu(menu, inflater);
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
				getString(R.string.str_email_subject));
		intent.putExtra(Intent.EXTRA_TEXT, getHistory());
		return intent;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuDeleteHistory:
			delete();
			return true;
		case R.id.menuShareHistory:
			Intent intent = getDefaultIntent();
			intent.putExtra(Intent.EXTRA_SUBJECT,
					getString(R.string.str_email_subject));
			intent.putExtra(Intent.EXTRA_TEXT, getHistory());
			startActivity(Intent.createChooser(intent, "Share via"));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void delete() {
		Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.msgDeleteAllHistory);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.btn_yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						HistoryDAO.getInstance(context).delete();
						checkHistory();
						startThread();
					}
				});
		builder.setNegativeButton(R.string.btn_no, null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void deletById(final String id) {
		Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.msgDelete);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.btn_yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						HistoryDAO.getInstance(context).delete(id);
						checkHistory();
						startThread();
					}
				});
		builder.setNegativeButton(R.string.btn_no, null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	// If history is empty, check is there is lens and creates a history for it.
	private void checkHistory() {
		List<HistoryVO> listHistory = HistoryDAO.getInstance(context)
				.getListHistory();

		int idLens = LensDAO.getInstance(context).getLastIdLens();

		if (listHistory == null || listHistory.size() == 0) {
			if (idLens != 0
					&& LensesDataDAO.getInstance(context).getLastIdLens() != 0) {
				if (HistoryDAO.getInstance(context).insert()) {
					Toast.makeText(context, R.string.msg_must_have_history,
							Toast.LENGTH_LONG).show();
					AlarmDAO.getInstance(context).setAlarm(idLens);
				}
			}
		}
	}

	private String getHistory() {
		StringBuilder text = new StringBuilder();

		List<HistoryVO> list = HistoryDAO.getInstance(context).getListHistory();

		if (list != null) {
			for (HistoryVO history : list) {
				if (history != null) {
					text.append("\n");
					text.append(getString(R.string.str_date_history))
							.append(" ").append(history.getDate_hist())
							.append("\n\n");
					if (history.getIn_use_left() == 1) {
						text.append("- ")
								.append(getString(R.string.tabLeftLens))
								.append("\n\n");
						text.append(getLeftText(history)).append("\n\n");
					}
					if (history.getIn_use_right() == 1) {
						text.append("- ")
								.append(getString(R.string.tabRightLens))
								.append("\n\n");
						text.append(getRightText(history)).append("\n");
					}
					text.append("___________________");
					text.append("\n");
				}
			}
		}
		return text.toString();
	}

}
