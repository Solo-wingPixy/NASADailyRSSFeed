package com.jc.nasadailyrssfeed;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import com.jc.nasadailyrssfeed.util.MyContentProvider;
import com.jc.nasadailyrssfeed.util.NasaDailyOpenHelper;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {

	// Log tag
	private static final String TAG = "my provider";
	
	/*private DailyImageAdapter dailyImageAdapter=new DailyImageAdapter();*/
	private SimpleCursorAdapter scadapter=null;
			
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
			
			String[] from={NasaDailyOpenHelper.TITLE,
					       NasaDailyOpenHelper.DATE,
					       NasaDailyOpenHelper.IMAGE,
					       NasaDailyOpenHelper.DESCRIPTION};
			
			int[] to={R.id.daily_title,
					  R.id.daily_date,
					  R.id.daily_image,
					  R.id.daily_description};
			
			scadapter=new SimpleCursorAdapter(MainActivity.this,
					R.layout.daily_list_item, cursor, from, to, 0);
			
			ListView listView = (ListView) findViewById(R.id.listview);
			if(scadapter!=null)
			   listView.setAdapter(scadapter);
			// Replace the result Cursor displayed by the Cursor Adapter with
			// the new result set.
            //** adapter.swapCursor(cursor);*//*
           
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
		
		/*initDatabase();*/
        
		//Initializing and Restarting the Cursor Loader
		LoaderManager loaderManager=getSupportLoaderManager();
		
		Bundle args=null;
		loaderManager.initLoader(0, args, myLoaderCallBacks);	
	    	
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		getSupportLoaderManager().restartLoader(0, null, myLoaderCallBacks);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public boolean initDatabase(){
		//Get the Content Resolver
		ContentResolver cr = getContentResolver();
		
		ContentValues newValues=new ContentValues();
		newValues.put(NasaDailyOpenHelper.TITLE, "美国夫妻散步挖出千枚金币 价值千万美元(图)");
		newValues.put(NasaDailyOpenHelper.DATE, "2014年02月26日13:58");
		newValues.put(NasaDailyOpenHelper.DESCRIPTION, "据美国侨报网26日报道，近日，一对加州夫妻雇来钱币专家唐·卡根(Don Kagan)鉴别一些金币。这些金币有上千枚，是这对夫妇在经常散步的小路旁挖出的，价值1000万美元。" +
				"唐·卡根不但是钱币专家，还是一名美国西部的经销商和拍卖商。卡根说，从1981年开始，就一直有人带着一两枚金币来找钱币专家，这些金币都价值几千美元。但是，这是第一次有人发现整整几罐金币。“这是百万分之一的机会，比中彩票几率都小，”他说。" +
				"这对夫妻的房产位于加州北部的蒂伯龙市(Tibron， CA)，去年春天，他们就找到这些金币，并与卡根讨论此事，但一直以来，他们并不愿意透露自己的姓名。" +
				"　这对幸运的夫妻表示，自己从来没有想过会发现这样的东西，但奇怪的是，这件事发生后，他们又觉得好像自己一生都在等待这一刻。" +
				"这对夫妻非常熟悉埋有金币的小路，很多年来，他们几乎每天都在这条小路上散步。在散步时，他们无意间发现一个老旧金属罐子的边缘露出地面。“然后，我弯下腰，刮去上面的苔藓，就发现了整个罐口，”这对夫妻说。" +
				"　这是这对夫妻发现的第一个装满金币的金属罐子，他们一共挖到5个这样的罐子。卡根说，他们一共发现大约1427枚未进入流通的崭新金币，金币上的日期是1847年至1894年。" +
				"25日，卡根透露，这对夫妻计划卖掉大部分金币。不过在卖掉之前，这对夫妻将把这些金币存放在美国铅笔协会(American Numismatic Association)，用于27日在亚特兰大举行的钱币展览。");
		
		// Insert the row into your table
    	Uri myRowUri=cr.insert(MyContentProvider.CONTENT_URI, newValues);
    	
    	Log.w(TAG, "image uri: "+myRowUri);
    	try{
    		// Open an output stream using the new row’s URI.
    		OutputStream outStream = cr.openOutputStream(myRowUri);
    		// Compress your bitmap and save it into your provider.
    		Bitmap image=BitmapFactory.decodeResource(getResources(), R.drawable.scene);
    		image.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
    	}catch(FileNotFoundException e){
    		Log.d(TAG, "No file found for this record.");
    	}
		
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
    
    //Reading and writing fi les from and to a Content Provider
    public void addNasaDailyWithImage(String title,String date,Bitmap image,String description ){
    	// Create a new row of values to insert
    	ContentValues values=new ContentValues();
    	
    	// Assign values for each row.
    	values.put(NasaDailyOpenHelper.TITLE, title);
    	values.put(NasaDailyOpenHelper.DATE, date);
    	values.put(NasaDailyOpenHelper.DESCRIPTION, description);
    	
    	// Get the Content Resolver
    	ContentResolver cr = getContentResolver();
    	
    	// Insert the row into your table
    	Uri myRowUri=cr.insert(MyContentProvider.CONTENT_URI, values);
    	
    	try{
    		// Open an output stream using the new row’s URI.
    		OutputStream outStream = cr.openOutputStream(myRowUri);
    		// Compress your bitmap and save it into your provider.
    		image.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
    	}catch(FileNotFoundException e){
    		Log.d(TAG, "No file found for this record.");
    	}
    }
    
    public Bitmap getImage(long rowID){
    	Uri myRowUri=ContentUris.withAppendedId(MyContentProvider.CONTENT_URI, rowID);
    	
    	try{
    		// Open an input stream using the new row’s URI.
    		InputStream inputStream = getContentResolver().openInputStream(myRowUri);
    		
    		// Make a copy of the Bitmap.
    		Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
    		return bitmap;
    	}catch(FileNotFoundException e){
    		Log.d(TAG, "No file found for this record.");
    	}
    	
    	return null;
    }
    
}
