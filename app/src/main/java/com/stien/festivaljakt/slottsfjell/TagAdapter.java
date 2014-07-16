package com.stien.festivaljakt.slottsfjell;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TagAdapter extends BaseAdapter {
	List<Tag> _tags;


	public TagAdapter(List<Tag> tags) {
		_tags = tags;
	}

	@Override
	public int getCount() {
		return _tags.size();
	}

	@Override
	public Object getItem(int i) {
		return _tags.get(i);
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

		TextView name = (TextView)view.findViewById(R.id.textView1);
		TextView time = (TextView)view.findViewById(R.id.textView2);

		Tag tag = _tags.get(_tags.size() - 1 - i);
		name.setText(tag.getName());
		time.setText(tag.getPrettyTime());

		return view;
	}
}
