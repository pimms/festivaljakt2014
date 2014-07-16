package com.stien.festivaljakt.slottsfjell;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TagAdapter extends BaseAdapter {
	List<String> _names;


	public TagAdapter(List<String> names) {
		_names = names;
	}

	@Override
	public int getCount() {
		return _names.size();
	}

	@Override
	public Object getItem(int i) {
		return _names.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		if (view == null) {
			view = LayoutInflater.from(ScanApplication.sharedApplicationContext()).inflate(R.layout.row_tagname, null);
		}

		TextView tv = (TextView)view.findViewById(R.id.textView);
		tv.setText(_names.get(_names.size() - 1 - i));

		return view;
	}
}
