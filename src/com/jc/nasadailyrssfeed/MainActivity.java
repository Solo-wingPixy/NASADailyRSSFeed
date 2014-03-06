package com.jc.nasadailyrssfeed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.jc.nasadailyrssfeed.util.MyContentProvider;
import com.jc.nasadailyrssfeed.util.MyCursorAdapter;
import com.jc.nasadailyrssfeed.util.NasaDailyImage;
import com.jc.nasadailyrssfeed.util.NasaDailyOpenHelper;
import com.jc.nasadailyrssfeed.util.ParserHandler;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity{

	// Log tag
	private static final String TAG = "mytag";

	private File fileDir = null;

	private static LruCache<String, Bitmap> mMemoryCache;
    
	private ListView listView;
	
	private ProgressDialog progressDialog;
	
	private MyCursorAdapter cursorAdapter;
	
	// Querying for Content Asynchronously Using the Cursor Loader
	LoaderManager.LoaderCallbacks<Cursor> myLoaderCallBacks = new LoaderManager.LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {

			// Construct the new query in the form of a Cursor Loader. Use the
			// id
			// parameter to construct and return different loaders.
			String[] projection = { NasaDailyOpenHelper.KEYWORD,
					NasaDailyOpenHelper.TITLE, NasaDailyOpenHelper.DATE,
					NasaDailyOpenHelper.IMAGE, NasaDailyOpenHelper.DESCRIPTION };
			String where = null;
			String[] whereArgs = null;
			String sortOrder = NasaDailyOpenHelper.KEYWORD + " desc";

			// Query URI
			Uri queryUri = MyContentProvider.CONTENT_URI;

			// Create the new Cursor loader.
			return new CursorLoader(MainActivity.this, queryUri, projection,
					where, whereArgs, sortOrder);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

			 cursorAdapter = new MyCursorAdapter(
					MainActivity.this, cursor, 0);

			/*// Replace the result Cursor displayed by the Cursor Adapter with
			// the new result set.
			cursorAdapter.swapCursor(cursor);*/

			if (cursorAdapter != null){
				listView.setAdapter(cursorAdapter);
			}     
            
			if(progressDialog.isShowing())
				progressDialog.dismiss();
			// This handler is not synchronized with the UI thread, so you
			// will need to synchronize it before modifying any UI elements
			// directly.

		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			// Remove the existing result Cursor from the List Adapter.
			/** adapter.swapCursor(null); */

			// This handler is not synchronized with the UI thread, so you
			// will need to synchronize it before modifying any UI elements
			// directly.
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fileDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

		// Use 1/8th of the available memory for this memory cache.
		final int cacheSize = maxMemory / 8;

		RetainFragment retainFragment = RetainFragment
				.findOrCreateRetainFragment(getSupportFragmentManager());

		mMemoryCache = retainFragment.mRetainedCache;
		if (mMemoryCache == null) {
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

				@SuppressLint("NewApi")
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					// The cache size will be measured in kilobytes rather than
					// number of items.
					return bitmap.getByteCount() / 1024;
				}
			};

			retainFragment.mRetainedCache = mMemoryCache;
		}
     
		// review the network state
		if (networkState()) {
			// if there are new contents appear
			new MyAsyncTask(this,fileDir).execute();
		} else {
			// network is unavailable,just get contents from the database;
			startLoader(0, null, myLoaderCallBacks);
		}
        
		listView = (ListView) findViewById(R.id.listview);
	}

	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null)
			mMemoryCache.put(key, bitmap);
	}

	public static Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}

	public void startLoader(int flag, Bundle args,
			LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks) {

		LoaderManager loaderManager = getSupportLoaderManager();
		loaderManager.initLoader(flag, args, loaderCallbacks);
	}

	public boolean networkState() {
		ConnectivityManager connectivity = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();

		if (info != null && info.isConnected()) {
			Toast.makeText(this, "网络连接正常", Toast.LENGTH_SHORT).show();
			progressDialog = ProgressDialog.show(this, "请稍后", 
					"正在加载数据。。。", true);
			return true;
		} else {
			Toast.makeText(this, "网络不可用", Toast.LENGTH_LONG).show();
			progressDialog = ProgressDialog.show(this, "请稍后", 
					"正在加载本地数据。。。", true);
			return false;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getSupportLoaderManager().restartLoader(0, null, myLoaderCallBacks);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class MyAsyncTask extends AsyncTask<Void,Void,Boolean>{
	    
		private String url = "http://www.nasa.gov/rss/dyn/image_of_the_day.rss";
		private Context context;
		private File fileDir;
		private LinkedList<NasaDailyImage> linklist;
		
		public LinkedList<NasaDailyImage> parseRSS() {

			ParserHandler parserHandler = new ParserHandler();

			try { // configure reader and parser
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				XMLReader reader = parser.getXMLReader();
				reader.setContentHandler(parserHandler);

				// make an input stream from the feed URL
				InputStream inputStream = new URL(url).openStream();

				// start the parsing
				reader.parse(new InputSource(inputStream));
				inputStream.close();
			} catch (IOException e) {
				Toast.makeText(context, "IO解析错误", Toast.LENGTH_SHORT).show();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return parserHandler.getLinkedList();
		}
		
		public boolean updateDatabase(LinkedList<NasaDailyImage> list) {

			if (list == null)
				return false;

			// Get the Content Resolver
			ContentResolver cr = context.getContentResolver();

			int num = list.size();
			for (int i = 0; i < num; i++) {
				NasaDailyImage image = list.get(i);
				ContentValues newValues = new ContentValues();

				File file = new File(fileDir, "bitmap" + i + ".jpg");
				String imageUri = file.getAbsolutePath();
				try {
					InputStream input = new URL(image.getImage()).openStream();
					Bitmap bitmap = BitmapFactory.decodeStream(input);

					FileOutputStream output = new FileOutputStream(file);
					bitmap.compress(CompressFormat.JPEG, 100, output);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				newValues.put(NasaDailyOpenHelper.TITLE, image.getTitle());
				newValues.put(NasaDailyOpenHelper.DATE, image.getDate());
				newValues.put(NasaDailyOpenHelper.IMAGE, imageUri);
				newValues.put(NasaDailyOpenHelper.DESCRIPTION,
						image.getDescription());

				// Insert the row into your table
				cr.insert(MyContentProvider.CONTENT_URI, newValues);
			}

			return true;
		}
		
		public MyAsyncTask(Context context,File fileDir){
			this.context=context;
			this.fileDir=fileDir;
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			linklist = parseRSS();
			return updateDatabase(linklist);
		}

	    @Override
	    protected void onPostExecute(Boolean bool){
	    	if(bool)
	    	   startLoader(0, null, myLoaderCallBacks);
	    }
	}

}
