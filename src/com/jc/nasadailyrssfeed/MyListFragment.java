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

import com.jc.nasadailyrssfeed.util.FileUtil;
import com.jc.nasadailyrssfeed.util.MyAdapter;
import com.jc.nasadailyrssfeed.util.MyContentProvider;
import com.jc.nasadailyrssfeed.util.MyCursorAdapter;
import com.jc.nasadailyrssfeed.util.NasaDailyImage;
import com.jc.nasadailyrssfeed.util.NasaDailyOpenHelper;
import com.jc.nasadailyrssfeed.util.ParserHandler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MyListFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor>,OnItemClickListener{

	private ProgressDialog progressDialog;
	private Context context;

	private ListView listView;
	private MyAdapter myAdapter;
	private MyCursorAdapter cursorAdapter;

	SharedPreferences timeOfUpdatedPreferences;
	
    private OnItemSelectedListener itemListener;
	
	public interface OnItemSelectedListener{
		public void onItemSelected(int dataId);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		itemListener = (OnItemSelectedListener)activity;
		this.context = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		timeOfUpdatedPreferences = ((Activity) context)
				.getPreferences(Context.MODE_PRIVATE);
		long timeOfLastUpdate = timeOfUpdatedPreferences.getLong(
				"timeOfLastUpdate", 0L);

		if (FileUtil.isTimeToUpdate(timeOfLastUpdate)) {
			// time to update
			if (FileUtil.isNetworkAvailable(context)) {
				Toast.makeText(context, "网络连接正常", Toast.LENGTH_SHORT).show();
				progressDialog = ProgressDialog.show(context, "请稍后",
						"正在加载数据。。。", true);
				new MyAsyncTask(context).execute();
			} else {
				Toast.makeText(context, "网络不可用", Toast.LENGTH_SHORT).show();
				progressDialog = ProgressDialog.show(context, "请稍后",
						"正在加载本地数据。。", true);
				startLoader(0, null, this);
			}

		} else {
			// just load local data
			Toast.makeText(context, "已更新完毕", Toast.LENGTH_SHORT).show();
			progressDialog = ProgressDialog.show(context, "请稍后", "正在加载数据。。。",
					true);
			startLoader(0, null, this);
		}

	}

	// Called once the Fragment has been created in order for it to
	// create its user interface.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_fragment, container, false);
		listView = (ListView) view.findViewById(R.id.listview);
		listView.setOnItemClickListener(this);
		return view;
	}

	// Called once the parent Activity and the Fragment’s UI have
	// been created.
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Complete the Fragment initialization – particularly anything
		// that requires the parent Activity to be initialized or the
		// Fragment’s view to be fully inflated.

	}

	// Called at the start of the visible lifetime.
	@Override
	public void onStart() {
		super.onStart();
		// Apply any required UI change now that the Fragment is visible.
	}

	// Called at the start of the active lifetime.
	@Override
	public void onResume() {
		super.onResume();
		// Resume any paused UI updates, threads, or processes required
		// by the Fragment but suspended when it became inactive.
	}

	public void startLoader(int flag, Bundle args,
			LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks) {

		LoaderManager loaderManager = getLoaderManager();
		loaderManager.initLoader(flag, args, loaderCallbacks);
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
				myAdapter = new MyAdapter(linklist, context);
				listView.setAdapter((ListAdapter) myAdapter);

				if (progressDialog != null)
					progressDialog.dismiss();
			}
		}
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
		return new CursorLoader(context, queryUri, projection, where,
				whereArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		cursorAdapter = new MyCursorAdapter(context, cursor, 0);

		/*
		 * // Replace the result Cursor displayed by the Cursor Adapter with //
		 * the new result set. cursorAdapter.swapCursor(cursor);
		 */

		if (cursorAdapter != null) {
			listView.setAdapter(cursorAdapter);
		}

		if (progressDialog.isShowing())
			progressDialog.dismiss();
		// This handler is not synchronized with the UI thread, so you
		// will need to synchronize it before modifying any UI elements
		// directly.
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		itemListener.onItemSelected(position);
	}

}
