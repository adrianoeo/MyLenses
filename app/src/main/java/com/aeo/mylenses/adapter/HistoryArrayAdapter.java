package com.aeo.mylenses.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aeo.mylenses.R;
import com.aeo.mylenses.vo.HistoryVO;

public class HistoryArrayAdapter extends ArrayAdapter<HistoryVO> {

	private List<HistoryVO> listHistory;
	private Context context;

	public HistoryArrayAdapter(Context context, int resource,
			List<HistoryVO> objects) {
		super(context, resource, objects);
		this.context = context;
		listHistory = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_history, null);
		}

		HistoryVO vo = listHistory.get(position);

		if (vo != null) {

			TextView textViewIdHistory = (TextView) view
					.findViewById(R.id.textViewIdHistory);
			TextView textViewDateHistory = (TextView) view
					.findViewById(R.id.textViewDateHistory);
			TextView textViewLeft = (TextView) view
					.findViewById(R.id.textViewDescLeft);
			TextView textViewRight = (TextView) view
					.findViewById(R.id.textViewDescRight);

			if (textViewIdHistory != null) {
				textViewIdHistory.setText(vo.getId().toString());
			}

			if (textViewDateHistory != null) {
				textViewDateHistory.setText(vo.getDate_hist());
			}

			String typeLeft = null;
			String typeRight = null;

			if (vo.getType_time_left() == 0) {
				typeLeft = "Day(s)";
			} else if (vo.getType_time_left() == 1) {
				typeLeft = "Month(s)";
			} else if (vo.getType_time_left() == 2) {
				typeLeft = "Year(s)";
			}
			if (vo.getType_time_right() == 0) {
				typeRight = "Day(s)";
			} else if (vo.getType_time_right() == 1) {
				typeRight = "Month(s)";
			} else if (vo.getType_time_right() == 2) {
				typeRight = "Year(s)";
			}

			if (textViewLeft != null) {
				textViewLeft.setText(new StringBuilder()
						.append(context.getString(R.string.str_left))
						.append(": ").append(vo.getDate_left()).append(" - ")
						.append(vo.getExpiration_left()).append(" ")
						.append(typeLeft));
				textViewLeft
						.setVisibility(vo.getIn_use_left() == 1 ? View.VISIBLE
								: View.INVISIBLE);
			}

			if (textViewRight != null) {
				textViewRight.setText(new StringBuilder()
						.append(context.getString(R.string.str_right))
						.append(": ").append(vo.getDate_right()).append(" - ")
						.append(vo.getExpiration_right()).append(" ")
						.append(typeRight));
				textViewRight
						.setVisibility(vo.getIn_use_right() == 1 ? View.VISIBLE
								: View.INVISIBLE);
			}
		}

		return view;
	}
}
