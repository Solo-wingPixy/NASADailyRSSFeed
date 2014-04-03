package com.jc.nasadailyrssfeed.util;

import java.util.LinkedList;
import com.jc.nasadailyrssfeed.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class MyAdapter extends BaseAdapter {

	private LinkedList<NasaDailyImage> linklist;
	private LayoutInflater inflater;

	static class ViewHolder {
		TextView title;
		TextView date;
		TextView description;
	}

	public MyAdapter(LinkedList<NasaDailyImage> linklist, Context context) {
		this.linklist = linklist;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return linklist.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return linklist.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.list_element, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.list_title);
			viewHolder.date = (TextView) convertView
					.findViewById(R.id.list_date);
			viewHolder.description = (TextView) convertView
					.findViewById(R.id.list_description);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.title.setText(FileUtil.toStyleText(linklist.get(position)
				.getTitle(), Typeface.BOLD), BufferType.SPANNABLE);
		viewHolder.date.setText(FileUtil.toStyleText(linklist.get(position)
				.getDate(), Typeface.ITALIC), BufferType.SPANNABLE);

		viewHolder.description.setText("  "
				+ linklist.get(position).getDescription().substring(0, 100)
				+ "...");

		return convertView;
	}

}
