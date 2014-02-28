package com.jc.nasadailyrssfeed;

import java.util.ArrayList;
import com.jc.nasadailyrssfeed.util.NasaDailyDAO;
import com.jc.nasadailyrssfeed.util.NasaDailyImage;
import com.jc.nasadailyrssfeed.util.NasaDailyOpenHelper;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String MyLog="mylog";
	
	private int key_code;
	private String title;
	private String date;
	private String image;
	private String description;
	
	private int num;
	private ArrayList<NasaDailyImage> nasaArrayList;
	
	public static final String fileDir=Environment.DIRECTORY_PICTURES;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		nasaArrayList=new ArrayList<NasaDailyImage>();
		
		NasaDailyOpenHelper nasaHelper=new NasaDailyOpenHelper(this);
		
		//Get writable database first,if fails,back to get readable database
		SQLiteDatabase db = nasaHelper.getWritableDatabase();
		if(db==null){
			db=nasaHelper.getReadableDatabase();
		}
		
		/*//update DB
		NasaDailyDAO.Update(db);*/
		
		Cursor cursor=NasaDailyDAO.Query(db);
		num=cursor.getCount();
		
		/** cursor from index -1
		 * cursor.moveToFirst();*/
		
		int columnIndex;
		while(cursor.moveToNext()){
			NasaDailyImage daily=new NasaDailyImage();
		    columnIndex=cursor.getColumnIndexOrThrow(NasaDailyOpenHelper.NASA_KEYWORD);
			key_code=cursor.getInt(columnIndex);
			columnIndex=cursor.getColumnIndexOrThrow(NasaDailyOpenHelper.NASA_TITLE);
		    title=cursor.getString(columnIndex);
		    columnIndex=cursor.getColumnIndexOrThrow(NasaDailyOpenHelper.NASA_DATE);
			date=cursor.getString(columnIndex);
			columnIndex=cursor.getColumnIndexOrThrow(NasaDailyOpenHelper.NASA_IMAGE);
			image=cursor.getString(columnIndex);
			columnIndex=cursor.getColumnIndexOrThrow(NasaDailyOpenHelper.NASA_DESCRIPTION);
			description=cursor.getString(columnIndex);
			daily.setKey_code(key_code);
			daily.setTitle(title);
			daily.setDate(date);
			daily.setImage(image);
			daily.setDescription(description);
			nasaArrayList.add(daily);
		}
		
		ListView listView = (ListView)findViewById(R.id.listview);
		listView.setAdapter(new DailyImageAdapter());
		/**
		 * //inite database
		NasaDailyDAO.initDatabase(db);
		db.close();
		Toast.makeText(this, "db init complete", Toast.LENGTH_SHORT).show();*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    
	
	class DailyImageAdapter implements ListAdapter{
        
		public DailyImageAdapter(){}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return num;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return nasaArrayList.get(position);
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
			/**
			 * how to reuse convertView ?????
			 */
			
			View view=View.inflate(MainActivity.this,
					R.layout.daily_list_item, null);
			
			TextView dailyTitle=(TextView)view.findViewById(R.id.daily_title);
			dailyTitle.setText(nasaArrayList.get(position).getTitle());
			TextView dailyDate=(TextView)view.findViewById(R.id.daily_date);
			dailyDate.setText(nasaArrayList.get(position).getDate());
			ImageView dailyImage=(ImageView)view.findViewById(R.id.daily_image);
			
			Bitmap bitmap=BitmapFactory.decodeFile(nasaArrayList.get(position).getImage());
			dailyImage.setImageBitmap(bitmap); 
			     
			TextView dailyDescription=(TextView)view.findViewById(R.id.daily_description);
			dailyDescription.setText(nasaArrayList.get(position).getDescription());
			return view;
			
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
}
