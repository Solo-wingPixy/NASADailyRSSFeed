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
		newValues.put(NasaDailyOpenHelper.TITLE, "��������ɢ���ڳ�ǧö��� ��ֵǧ����Ԫ(ͼ)");
		newValues.put(NasaDailyOpenHelper.DATE, "2014��02��26��13:58");
		newValues.put(NasaDailyOpenHelper.DESCRIPTION, "�������ȱ���26�ձ��������գ�һ�Լ��ݷ��޹���Ǯ��ר���ơ�����(Don Kagan)����һЩ��ҡ���Щ�������ǧö������Է��ھ���ɢ����С·���ڳ��ģ���ֵ1000����Ԫ��" +
				"�ơ�����������Ǯ��ר�ң�����һ�����������ľ����̺������̡�����˵����1981�꿪ʼ����һֱ���˴���һ��ö�������Ǯ��ר�ң���Щ��Ҷ���ֵ��ǧ��Ԫ�����ǣ����ǵ�һ�����˷����������޽�ҡ������ǰ����֮һ�Ļ��ᣬ���в�Ʊ���ʶ�С������˵��" +
				"��Է��޵ķ���λ�ڼ��ݱ����ĵٲ�����(Tibron�� CA)��ȥ�괺�죬���Ǿ��ҵ���Щ��ң����뿨�����۴��£���һֱ���������ǲ���Ը��͸¶�Լ���������" +
				"��������˵ķ��ޱ�ʾ���Լ�����û������ᷢ�������Ķ���������ֵ��ǣ�����·����������־��ú����Լ�һ�����ڵȴ���һ�̡�" +
				"��Է��޷ǳ���Ϥ���н�ҵ�С·���ܶ����������Ǽ���ÿ�춼������С·��ɢ������ɢ��ʱ����������䷢��һ���Ͼɽ������ӵı�Ե¶�����档��Ȼ��������������ȥ�����̦޺���ͷ����������޿ڣ�����Է���˵��" +
				"��������Է��޷��ֵĵ�һ��װ����ҵĽ������ӣ�����һ���ڵ�5�������Ĺ��ӡ�����˵������һ�����ִ�Լ1427öδ������ͨ��ո�½�ң�����ϵ�������1847����1894�ꡣ" +
				"25�գ�����͸¶����Է��޼ƻ������󲿷ֽ�ҡ�����������֮ǰ����Է��޽�����Щ��Ҵ��������Ǧ��Э��(American Numismatic Association)������27��������������е�Ǯ��չ����");
		
		// Insert the row into your table
    	Uri myRowUri=cr.insert(MyContentProvider.CONTENT_URI, newValues);
    	
    	Log.w(TAG, "image uri: "+myRowUri);
    	try{
    		// Open an output stream using the new row��s URI.
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
    		// Open an output stream using the new row��s URI.
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
    		// Open an input stream using the new row��s URI.
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
