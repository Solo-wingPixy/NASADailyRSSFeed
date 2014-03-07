package com.jc.nasadailyrssfeed.util;

import java.util.LinkedList;
import com.jc.nasadailyrssfeed.R;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class MyAdapter implements ListAdapter{
    
	private LinkedList<NasaDailyImage> linklist;
	private LayoutInflater inflater;
	
	static class ViewHolder{
		TextView title;
		TextView date;
		TextView description;
	}
	
	public MyAdapter(LinkedList<NasaDailyImage> linklist,Context context){
		this.linklist=linklist;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return linklist.size();
	}

	@Override
	public Object getItem(int id) {
		// TODO Auto-generated method stub
		return linklist.get(id);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder=null;
		if(convertView==null){
			convertView = inflater.inflate(R.layout.list_element, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.title=(TextView)convertView.findViewById(R.id.list_title);
			viewHolder.date=(TextView)convertView.findViewById(R.id.list_date);
			viewHolder.description=(TextView)convertView.findViewById(R.id.list_description);
			convertView.setTag(viewHolder);
		}else{
		    viewHolder=(ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(linklist.get(position).getTitle());
		viewHolder.date.setText(linklist.get(position).getDate());
		viewHolder.description.setText(linklist.get(position)
				.getDescription().substring(0, 100));
		
		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean areAllItemsEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return false;
	}

	
}
