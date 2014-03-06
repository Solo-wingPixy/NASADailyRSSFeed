package com.jc.nasadailyrssfeed.util;


import com.jc.nasadailyrssfeed.MainActivity;
import com.jc.nasadailyrssfeed.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter{
    
	static class ViewHolder{
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
	public void bindView(View view, Context context, Cursor cursor ) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = (ViewHolder)view.getTag();
		
		String title=cursor.getString(cursor.getColumnIndex(NasaDailyOpenHelper.TITLE));
		String date =cursor.getString(cursor.getColumnIndex(NasaDailyOpenHelper.DATE));
		String imageUri=cursor.getString(cursor.getColumnIndex(NasaDailyOpenHelper.IMAGE));
		String description=cursor.getString(cursor.getColumnIndex(NasaDailyOpenHelper.DESCRIPTION));
		
		viewHolder.title.setText(title);
		viewHolder.date.setText(date);
		
		String split= imageUri.substring(imageUri.indexOf("bitmap"));
		Bitmap bitmap = MainActivity.getBitmapFromMemoryCache(split);
		if(bitmap!=null){
			viewHolder.image.setImageBitmap(bitmap);
		}else{
			bitmap = MyBitmapHelper.decodeSampleBitmap(imageUri, 100, 100);
			
			MainActivity.addBitmapToMemoryCache(split, bitmap);
			viewHolder.image.setImageBitmap(bitmap);
		}
		 		
		viewHolder.description.setText(description);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = new ViewHolder();
		LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		/**
		 * inflater.inflate(R.layout.daily_list_item,viewGroup)  ³ÌÐò»á±¨´í
		 */
		View view = inflater.inflate(R.layout.daily_list_item,viewGroup,false);
		viewHolder.title=(TextView)view.findViewById(R.id.daily_title);
		viewHolder.date=(TextView)view.findViewById(R.id.daily_date);
		viewHolder.image=(ImageView)view.findViewById(R.id.daily_image);
		viewHolder.description =(TextView)view.findViewById(R.id.daily_description);
		view.setTag(viewHolder);
		return view;
	}

}
