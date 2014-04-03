package com.jc.nasadailyrssfeed;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.jc.nasadailyrssfeed.util.FileUtil;
import com.jc.nasadailyrssfeed.util.MyContentProvider;
import com.jc.nasadailyrssfeed.util.MyCursorAdapter;
import com.jc.nasadailyrssfeed.util.NasaDailyImage;
import com.jc.nasadailyrssfeed.util.NasaDailyOpenHelper;
import com.jc.nasadailyrssfeed.util.ParserHandler;

public class UIActivity2_0 extends FragmentActivity implements
		LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {

	private ListView nasaDailyList;

	private SharedPreferences timeOfUpdatedPreferences;
	private ProgressDialog progressDialog;

	private MyCursorAdapter cursorAdapter;

	private int countNum;

	private LruCache<String, Bitmap> mMemoryCache;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui2_0);

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
		/**------------------Lrucache------------------------*/

		nasaDailyList = (ListView) findViewById(R.id.nasadailylist);
		nasaDailyList.setOnItemClickListener(this);

		// time
		timeOfUpdatedPreferences = this.getPreferences(Context.MODE_PRIVATE);
		long timeOfLastUpdate = timeOfUpdatedPreferences.getLong(
				"timeOfLastUpdate", 0L);

		if (FileUtil.isTimeToUpdate(timeOfLastUpdate)) {
			// time to update
			if (FileUtil.isNetworkAvailable(this)) {
				Toast.makeText(this, "网络连接正常", Toast.LENGTH_SHORT).show();
				progressDialog = ProgressDialog.show(this, "请稍后", "正在加载数据。。。",
						true);
				new MyAsyncTask(this).execute();
			} else {
				//try to load local data
				startLoader(0, null, this);
			}

		} else {
			// just load local data
			Toast.makeText(this, "已更新完毕", Toast.LENGTH_SHORT).show();
			startLoader(0, null, this);
		}
	}

	public void startLoader(int flag, Bundle args,
			LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks) {

		LoaderManager loaderManager = getSupportLoaderManager();
		loaderManager.initLoader(flag, args, loaderCallbacks);
	}

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
		String sortOrder = null;

		// Query URI
		Uri queryUri = MyContentProvider.CONTENT_URI;

		// Create the new Cursor loader.
		return new CursorLoader(this, queryUri, projection, where, whereArgs,
				sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		cursorAdapter = new MyCursorAdapter(this, cursor, 0);

		/*
		 * // Replace the result Cursor displayed by the Cursor Adapter with //
		 * the new result set. cursorAdapter.swapCursor(cursor);
		 */
		if (cursorAdapter.getCount()!=0) {

			nasaDailyList.setAdapter(cursorAdapter);
			countNum = cursorAdapter.getCount();
		}else{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Sorry,无法获取数据")
			.setNeutralButton("确定", new OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					finish();
				}
				
			}).create().show();
			
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}

	class MyAsyncTask extends AsyncTask<Void, Void, Boolean> {

		private String url = "http://www.nasa.gov/rss/dyn/image_of_the_day.rss";
		private Context context;
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

				newValues.put(NasaDailyOpenHelper.TITLE, image.getTitle());
				newValues.put(NasaDailyOpenHelper.DATE, image.getDate());
				newValues.put(NasaDailyOpenHelper.IMAGE, image.getImage());
				newValues.put(NasaDailyOpenHelper.DESCRIPTION,
						image.getDescription());

				// Insert the row into your table
				cr.insert(MyContentProvider.CONTENT_URI, newValues);
			}
			// update the update time
			SharedPreferences.Editor timeEditor = timeOfUpdatedPreferences
					.edit();
			timeEditor.putLong("timeOfLastUpdate",
					System.currentTimeMillis() / 1000);
			timeEditor.commit();

			return true;
		}

		public MyAsyncTask(Context context) {
			this.context = context;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			linklist = parseRSS();
			return updateDatabase(linklist);
		}

		@Override
		protected void onPostExecute(Boolean bool) {
			if (bool) {
				
				startLoader(0, null, UIActivity2_0.this);
				
				if (progressDialog != null)
					progressDialog.dismiss();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, DetailFragmentActvity.class);
		intent.putExtra("countNum", countNum);
		startActivity(intent);
	}
    
	public  void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null)
			mMemoryCache.put(key, bitmap);
	}

	public  Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}
}
