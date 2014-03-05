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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	// Log tag
	private static final String TAG = "mytag";

	private String url = "http://www.nasa.gov/rss/dyn/image_of_the_day.rss";
	private ProgressDialog progressDialog = null;
	
    private  File fileDir = null;
	
	// Querying for Content Asynchronously Using the Cursor Loader
	private LoaderManager.LoaderCallbacks<Cursor> myLoaderCallBacks = new LoaderManager.LoaderCallbacks<Cursor>() {

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

			MyCursorAdapter cursorAdapter = new MyCursorAdapter(
					MainActivity.this, cursor, 0);

			ListView listView = (ListView) findViewById(R.id.listview);
			if (cursorAdapter != null)
				listView.setAdapter(cursorAdapter);

			if (progressDialog != null)
				progressDialog.dismiss();
			// Replace the result Cursor displayed by the Cursor Adapter with
			// the new result set.
			// ** adapter.swapCursor(cursor);*//*

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

	private  Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// Initializing and Restarting the Cursor Loader
			LoaderManager loaderManager = getSupportLoaderManager();

			Bundle args = null;
			loaderManager.initLoader(0, args, myLoaderCallBacks);
		}
	};

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			updateDatabase(parseRSS());

			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("value", "初始化数据库");
			msg.setData(data);
			handler.sendMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
         
		fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		// review the network state
		if (networkState()) {
			
			//if there are new contents appear
			
			new Thread(runnable).start();
		} else {
			// network is unavailable,just get contents from the database;

			// Initializing and Restarting the Cursor Loader
			LoaderManager loaderManager = getSupportLoaderManager();

			Bundle args = null;
			loaderManager.initLoader(0, args, myLoaderCallBacks);
		}

	}

	public boolean networkState() {
		ConnectivityManager connectivity = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();

		if (info != null && info.isConnected()) {
			Toast.makeText(this, "网络连接正常", Toast.LENGTH_SHORT).show();
			progressDialog = ProgressDialog
					.show(this, "请稍后", "正在获取数据...", true);
			return true;
		} else {
			Toast.makeText(this, "网络不可用", Toast.LENGTH_LONG).show();
			progressDialog = ProgressDialog
					.show(this, "请稍后", "正在获取本地数据...", true);
			return false;
		}
	}

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
			Log.w("saxtag", "是否阻塞");
			inputStream.close();
		} catch (IOException e) {
			Toast.makeText(this, "IO错误，读取Rss失败", Toast.LENGTH_SHORT).show();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parserHandler.getLinkedList();
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

	public boolean updateDatabase(LinkedList<NasaDailyImage> list) {

		if (list == null)
			return false;

		// Get the Content Resolver
		ContentResolver cr = getContentResolver();

		int num = list.size();
		for (int i = 0; i < num; i++) {
			NasaDailyImage image = list.get(i);
			ContentValues newValues = new ContentValues();
			
			File file=new File(fileDir, "bitmap"+i+".jpg");
			String imageUri=file.getAbsolutePath();
			try {
				InputStream input=new URL(image.getImage()).openStream();
				Bitmap bitmap = BitmapFactory.decodeStream(input);
				
				FileOutputStream output=new FileOutputStream(file);
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
	
	

}
