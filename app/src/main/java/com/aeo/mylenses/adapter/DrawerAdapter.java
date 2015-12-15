package com.aeo.mylenses.adapter;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aeo.mylenses.R;

public class DrawerAdapter extends ArrayAdapter<String> {

	private List<String> list;
	private Context context;

	public DrawerAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		list = objects;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.drawer_item, null);
		}

		TextView text = (TextView) view.findViewById(R.id.textDrawerItem);

		TypedArray images = context.getResources().obtainTypedArray(
				R.array.array_drawable_icon);
		int id = images.getResourceId(position, -1);
		images.recycle();

		text.setText(list.get(position));
		text.setCompoundDrawablesWithIntrinsicBounds(context.getResources()
				.getDrawable(id), null, null, null);

		return view;
	}

}
