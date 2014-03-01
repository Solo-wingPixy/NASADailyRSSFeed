package com.jc.nasadailyrssfeed;

import java.util.ArrayList;

import com.jc.nasadailyrssfeed.util.MyContentProvider;
import com.jc.nasadailyrssfeed.util.NasaDailyDAO;
import com.jc.nasadailyrssfeed.util.NasaDailyImage;
import com.jc.nasadailyrssfeed.util.NasaDailyOpenHelper;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	// Log tag
	private static final String TAG = "my provider";

	private int key_code;
	private String title;
	private String date;
	private String image;
	private String description;

	private int num;
	private ArrayList<NasaDailyImage> nasaArrayList;

	public static final String fileDir = Environment.DIRECTORY_PICTURES;

	// Querying for Content Asynchronously Using the Cursor Loader
	private LoaderManager.LoaderCallbacks<Cursor> myLoaderCallBacks = new LoaderManager.LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			// Construct the new query in the form of a Cursor Loader. Use the
			// id
			// parameter to construct and return different loaders.
			String[] projection = {NasaDailyOpenHelper.KEYWORD,
					               NasaDailyOpenHelper.TITLE,
					               NasaDailyOpenHelper.DATE,
					               NasaDailyOpenHelper.IMAGE,
					               NasaDailyOpenHelper.DESCRIPTION};
			String where = null;
			String[] whereArgs = null;
			String sortOrder = null;

			// Query URI
			Uri queryUri = MyContentProvider.CONTENT_URI;

			// Create the new Cursor loader.
			return new CursorLoader(MainActivity.this, queryUri, projection,
					where, whereArgs, sortOrder);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			// Replace the result Cursor displayed by the Cursor Adapter with
			// the new result set.
           /** adapter.swapCursor(cursor);*/
            
         // This handler is not synchronized with the UI thread, so you
         // will need to synchronize it before modifying any UI elements
         // directly.
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			// Remove the existing result Cursor from the List Adapter.
           /** adapter.swapCursor(null);*/
            
         // This handler is not synchronized with the UI thread, so you
         // will need to synchronize it before modifying any UI elements
         // directly.
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        
		//Initializing and Restarting the Cursor Loader
		LoaderManager loaderManager=getSupportLoaderManager();
		
		Bundle args=null;
		loaderManager.initLoader(1, args, myLoaderCallBacks);	
		
		nasaArrayList = new ArrayList<NasaDailyImage>();

		NasaDailyOpenHelper nasaHelper = new NasaDailyOpenHelper(this);

		// Get writable database first,if fails,back to get readable database
		SQLiteDatabase db = nasaHelper.getWritableDatabase();
		if (db == null) {
			db = nasaHelper.getReadableDatabase();
		}

		/*
		 * //update DB NasaDailyDAO.Update(db);
		 */

		Cursor cursor = NasaDailyDAO.Query(db);
		num = cursor.getCount();

		/**
		 * cursor from index -1 cursor.moveToFirst();
		 */

		int columnIndex;
		while (cursor.moveToNext()) {
			NasaDailyImage daily = new NasaDailyImage();
			columnIndex = cursor
					.getColumnIndexOrThrow(NasaDailyOpenHelper.KEYWORD);
			key_code = cursor.getInt(columnIndex);
			columnIndex = cursor
					.getColumnIndexOrThrow(NasaDailyOpenHelper.TITLE);
			title = cursor.getString(columnIndex);
			columnIndex = cursor
					.getColumnIndexOrThrow(NasaDailyOpenHelper.DATE);
			date = cursor.getString(columnIndex);
			columnIndex = cursor
					.getColumnIndexOrThrow(NasaDailyOpenHelper.IMAGE);
			image = cursor.getString(columnIndex);
			columnIndex = cursor
					.getColumnIndexOrThrow(NasaDailyOpenHelper.DESCRIPTION);
			description = cursor.getString(columnIndex);
			daily.setKey_code(key_code);
			daily.setTitle(title);
			daily.setDate(date);
			daily.setImage(image);
			daily.setDescription(description);
			nasaArrayList.add(daily);
		}

		ListView listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(new DailyImageAdapter());
		/**
		 * inite database NasaDailyDAO.initDatabase(db); db.close();
		 * Toast.makeText(this, "db init complete", Toast.LENGTH_SHORT).show();
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public Cursor queryAllRows(){
		// Get the Content Resolver.
		ContentResolver cr = getContentResolver();
		
		// Specify the result column projection. Return the minimum set
		// of columns required to satisfy your requirements.
		String[] result_columns=new String[]{
				NasaDailyOpenHelper.KEYWORD,NasaDailyOpenHelper.TITLE,
				NasaDailyOpenHelper.DATE,NasaDailyOpenHelper.IMAGE,
				NasaDailyOpenHelper.DESCRIPTION};
		
		// Specify the where clause that will limit your results.
		String where = null;
		
		// Replace these with valid SQL statements as necessary.
		String[] whereArgs=null;
		String order = null;
		
		// Return the specified rows.
		return cr.query(MyContentProvider.CONTENT_URI, result_columns,
				where, whereArgs, order);
	}
	
	public Cursor query(int rowID){
		// Get the Content Resolver.
		ContentResolver cr=getContentResolver();
		
		// Specify the result column projection. Return the minimum set
		// of columns required to satisfy your requirements.
		String[] result_columns=new String[]{
				NasaDailyOpenHelper.KEYWORD,NasaDailyOpenHelper.TITLE,
				NasaDailyOpenHelper.DATE,NasaDailyOpenHelper.IMAGE,
				NasaDailyOpenHelper.DESCRIPTION};
		
		// Append a row ID to the URI to address a specific row.
		Uri rowAddress=ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, rowID);
		
		// These are null as we are requesting a single row.
		String where = null;
		String[] whereArgs=null;
		String order=null;
		
		// Return the specified rows.
		return cr.query(rowAddress, result_columns, where, whereArgs, order);
	}
	
    public Uri insert(String title,String date,String image,String description){
    	// Create a new row of values to insert.
    	/**
    	 * bulkInsert  takes an array
    	 */
    	ContentValues values = new ContentValues();
    	
    	// Assign values for each row.
    	values.put(NasaDailyOpenHelper.TITLE, title);
    	values.put(NasaDailyOpenHelper.DATE, date);
    	/** image can be null*/
    	values.put(NasaDailyOpenHelper.IMAGE, image);
    	values.put(NasaDailyOpenHelper.DESCRIPTION, description);
    	
    	// Get the Content Resolver
    	ContentResolver cr = getContentResolver();
    	
    	// Insert the row into your table
    	return cr.insert(MyContentProvider.CONTENT_URI, values);
    }
    
    //return delete row numbers
    public int delete(int rowID){
    	// Specify a where clause that determines which row(s) to delete.
    	// Specify where arguments as necessary.
    	String where=NasaDailyOpenHelper.KEYWORD+"="+rowID;
    	
    	String[] whereArgs=null;
    	
    	// Get the Content Resolver.
    	ContentResolver cr = getContentResolver();
    	
    	// Delete the matching rows
    	return cr.delete(MyContentProvider.CONTENT_URI, where, whereArgs);
    }
    
    //return update rows number
    public int update(int rowID,String title,String date,String image,String description){
    	// Create the updated row content, assigning values for each row.
    	ContentValues updateValues=new ContentValues();
    	if(title!=null&&title!="")
    	  updateValues.put(NasaDailyOpenHelper.TITLE, title);
    	if(date!=null&&title!="")
    	  updateValues.put(NasaDailyOpenHelper.DATE, date);
    	if(image!=null&&image!="")
    	  updateValues.put(NasaDailyOpenHelper.IMAGE, image);
    	if(description!=null&&description!="")
    	  updateValues.put(NasaDailyOpenHelper.DESCRIPTION, description);
    	
    	// Create a URI addressing a specific row.
    	Uri rowURI=ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, rowID);
    	
    	// Specify a specific row so no selection clause is required.
    	String where=null;
    	String[] whereArgs=null;
    	
    	// Get the Content Resolver.
    	ContentResolver cr = getContentResolver();
    	
    	// Update the specified row.
    	return cr.update(rowURI, updateValues, where, whereArgs);
    }
    
	class DailyImageAdapter implements ListAdapter {

		public DailyImageAdapter() {
		}

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
			View view = View.inflate(MainActivity.this,
					R.layout.daily_list_item, null);

			TextView dailyTitle = (TextView) view
					.findViewById(R.id.daily_title);
			dailyTitle.setText(nasaArrayList.get(position).getTitle());
			TextView dailyDate = (TextView) view.findViewById(R.id.daily_date);
			dailyDate.setText(nasaArrayList.get(position).getDate());
			ImageView dailyImage = (ImageView) view
					.findViewById(R.id.daily_image);

			Bitmap bitmap = BitmapFactory.decodeFile(nasaArrayList
					.get(position).getImage());
			dailyImage.setImageBitmap(bitmap);

			TextView dailyDescription = (TextView) view
					.findViewById(R.id.daily_description);
			dailyDescription.setText(nasaArrayList.get(position)
					.getDescription());
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
