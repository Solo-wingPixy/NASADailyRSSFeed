package com.jc.nasadailyrssfeed.util;

import com.jc.nasadailyrssfeed.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class MyCursorAdapter extends CursorAdapter {

	static class ViewHolder {
		TextView title;
		TextView date;
		ImageView image;
		TextView description;
	}

	public MyCursorAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		ViewHolder viewHolder = (ViewHolder) view.getTag();

		String title = cursor.getString(cursor
				.getColumnIndex(NasaDailyOpenHelper.TITLE));
		String date = cursor.getString(cursor
				.getColumnIndex(NasaDailyOpenHelper.DATE));
		String description = "  "
				+ cursor.getString(
						cursor.getColumnIndex(NasaDailyOpenHelper.DESCRIPTION))
						.substring(0, 100) + "...";
		
		viewHolder.title.setText(FileUtil.toStyleText(title, Typeface.BOLD),
				BufferType.SPANNABLE);
		viewHolder.date.setText(FileUtil.toStyleText(date, Typeface.ITALIC),
				BufferType.SPANNABLE);
		viewHolder.description.setText(description);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		/**
		 * inflater.inflate(R.layout.daily_list_item,viewGroup) ³ÌÐò»á±¨´í
		 */
		View view = inflater.inflate(R.layout.list_element, viewGroup, false);
		viewHolder.title = (TextView) view.findViewById(R.id.list_title);
		viewHolder.date = (TextView) view.findViewById(R.id.list_date);
		viewHolder.description = (TextView) view
				.findViewById(R.id.list_description);
		view.setTag(viewHolder);
		return view;
	}

}
