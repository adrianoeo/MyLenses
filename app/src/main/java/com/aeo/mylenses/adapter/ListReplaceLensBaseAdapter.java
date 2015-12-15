package com.aeo.mylenses.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aeo.mylenses.R;
import com.aeo.mylenses.vo.LensVO;

public class ListReplaceLensBaseAdapter extends BaseAdapter {
	private List<LensVO> list;
	private Context context;

	public ListReplaceLensBaseAdapter(Context context, List<LensVO> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;

		TextView idLens;
		TextView dateLeft;
		TextView dateRight;
		TextView timeLeft;
		TextView timeRight;
		TextView txtLensLeft;
		TextView txtLensRight;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.fragment_item_list_lens, null);

			idLens = (TextView) view.findViewById(R.id.textViewIdReplaceLens);
			dateLeft = (TextView) view
					.findViewById(R.id.textViewDateReplaceLensLeft);
			dateRight = (TextView) view
					.findViewById(R.id.textViewDateReplaceLensRight);
			timeLeft = (TextView) view
					.findViewById(R.id.textViewTimeReplaceLensLeft);
			timeRight = (TextView) view
					.findViewById(R.id.textViewTimeReplaceLensRight);
			txtLensLeft = (TextView) view
					.findViewById(R.id.textViewDescReplaceLensLeft);
			txtLensRight = (TextView) view
					.findViewById(R.id.textViewDescReplaceLensRight);

			LensVO lenses = list.get(position);
			String typeLeft = null;
			String typeRight = null;

			if (lenses.getTypeLeft() == 0) {
				typeLeft = "Day(s)";
			} else if (lenses.getTypeLeft() == 1) {
				typeLeft = "Month(s)";
			} else if (lenses.getTypeLeft() == 2) {
				typeLeft = "Year(s)";
			}
			if (lenses.getTypeRight() == 0) {
				typeRight = "Day(s)";
			} else if (lenses.getTypeRight() == 1) {
				typeRight = "Month(s)";
			} else if (lenses.getTypeRight() == 2) {
				typeRight = "Year(s)";
			}

			dateLeft.setText(lenses.getDateLeft());
			dateRight.setText(lenses.getDateRight());
			timeLeft.setText(new StringBuilder()
					.append(lenses.getExpirationLeft()).append(" ")
					.append(typeLeft));
			timeRight.setText(new StringBuilder()
					.append(lenses.getExpirationRight()).append(" ")
					.append(typeRight));

			idLens.setText(lenses.getId().toString());

			if (position > 0) {
				txtLensLeft.setTextColor(Color.GRAY);
				txtLensRight.setTextColor(Color.GRAY);
				dateLeft.setTextColor(Color.GRAY);
				dateRight.setTextColor(Color.GRAY);
				timeLeft.setTextColor(Color.GRAY);
				timeRight.setTextColor(Color.GRAY);
			} else {
				txtLensLeft.setTextColor(Color.BLACK);
				txtLensRight.setTextColor(Color.BLACK);
				dateLeft.setTextColor(Color.BLACK);
				dateRight.setTextColor(Color.BLACK);
				timeLeft.setTextColor(Color.BLACK);
				timeRight.setTextColor(Color.BLACK);
			}

		} else {
			view = convertView;

			dateLeft = (TextView) view
					.findViewById(R.id.textViewDateReplaceLensLeft);
			dateRight = (TextView) view
					.findViewById(R.id.textViewDateReplaceLensRight);
			timeLeft = (TextView) view
					.findViewById(R.id.textViewTimeReplaceLensLeft);
			timeRight = (TextView) view
					.findViewById(R.id.textViewTimeReplaceLensRight);
			txtLensLeft = (TextView) view
					.findViewById(R.id.textViewDescReplaceLensLeft);
			txtLensRight = (TextView) view
					.findViewById(R.id.textViewDescReplaceLensRight);

			if (position > 0) {
				txtLensLeft.setTextColor(Color.GRAY);
				txtLensRight.setTextColor(Color.GRAY);
				dateLeft.setTextColor(Color.GRAY);
				dateRight.setTextColor(Color.GRAY);
				timeLeft.setTextColor(Color.GRAY);
				timeRight.setTextColor(Color.GRAY);
			} else {
				txtLensLeft.setTextColor(Color.BLACK);
				txtLensRight.setTextColor(Color.BLACK);
				dateLeft.setTextColor(Color.BLACK);
				dateRight.setTextColor(Color.BLACK);
				timeLeft.setTextColor(Color.BLACK);
				timeRight.setTextColor(Color.BLACK);
			}
		}

		return view;
	}

}
