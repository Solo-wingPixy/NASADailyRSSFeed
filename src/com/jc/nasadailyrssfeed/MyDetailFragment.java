package com.jc.nasadailyrssfeed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import com.jc.nasadailyrssfeed.util.FileUtil;
import com.jc.nasadailyrssfeed.util.MyContentProvider;
import com.jc.nasadailyrssfeed.util.NasaDailyOpenHelper;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class MyDetailFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private int dataId;
    private Context context;
    
    private File fileDir = null;
    
    private TextView dailyTitle;
    private TextView dailyDate;
    private ImageView dailyImage;
    private TextView dailyDescription;
    
    private ContentResolver resolver;
   
    private ProgressBar progressBar;
	// Called when the Fragment is attached to its parent Activity.
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Get a reference to the parent Activity.
		this.context = activity;
	}

	// Called to do the initial creation of the Fragment.
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Initialize the Fragment.
		dataId = this.getArguments().getInt("dataId");
		
		fileDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		
		resolver = context.getContentResolver();
		
		startLoader(1, null, this);
	}

	// Called once the Fragment has been created in order for it to
	// create its user interface.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Create, or inflate the Fragment¡¯s UI, and return it.
		// If this Fragment has no UI then return null.
		View populateView = inflater.inflate(R.layout.detail_fragment, container, false);
		dailyTitle = (TextView)populateView.findViewById(R.id.daily_title);
		dailyDate = (TextView)populateView.findViewById(R.id.daily_date);
		dailyImage = (ImageView)populateView.findViewById(R.id.daily_image);
		dailyDescription = (TextView)populateView.findViewById(R.id.daily_description);
		progressBar = (ProgressBar)populateView.findViewById(R.id.image_progressbar);
		return populateView;
	}

	// Called once the parent Activity and the Fragment¡¯s UI have
	// been created.
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Complete the Fragment initialization ¨C particularly anything
		// that requires the parent Activity to be initialized or the
		// Fragment¡¯s view to be fully inflated.
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

	// Called at the end of the active lifetime.
	@Override
	public void onPause() {
		// Suspend UI updates, threads, or CPU intensive processes
		// that don¡¯t need to be updated when the Activity isn¡¯t
		// the active foreground activity.
		// Persist all edits or state changes
		// as after this call the process is likely to be killed.
		super.onPause();
	}

	// Called to save UI state changes at the
	// end of the active lifecycle.
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate, onCreateView, and
		// onActivityCreate if the parent Activity is killed and restarted.
		super.onSaveInstanceState(savedInstanceState);
	}

	// Called at the end of the visible lifetime.
	@Override
	public void onStop() {
		// Suspend remaining UI updates, threads, or processing
		// that aren¡¯t required when the Fragment isn¡¯t visible.
		super.onStop();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		// Construct the new query in the form of a Cursor Loader. Use the
		// id
		// parameter to construct and return different loaders.
		String[] projection = { NasaDailyOpenHelper.KEYWORD,
				NasaDailyOpenHelper.TITLE, NasaDailyOpenHelper.DATE,
				NasaDailyOpenHelper.IMAGE, NasaDailyOpenHelper.DESCRIPTION };
		
		String where = NasaDailyOpenHelper.KEYWORD +"="+ dataId;
		String[] whereArgs = null;
        String sortOrder = null;
		// Query URI
		Uri queryUri = MyContentProvider.CONTENT_URI;

		// Create the new Cursor loader.
		return new CursorLoader(context, queryUri, projection, where,
				whereArgs, sortOrder);
	}
	
	public void startLoader(int flag, Bundle args,
			LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks) {

		LoaderManager loaderManager = getLoaderManager();
		loaderManager.initLoader(flag, args, loaderCallbacks);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		cursor.moveToFirst();
		
		String title = cursor.getString(cursor.getColumnIndex(NasaDailyOpenHelper.TITLE));
		String date = cursor.getString(cursor.getColumnIndex(NasaDailyOpenHelper.DATE));
		String description = cursor.getString(cursor.getColumnIndex(NasaDailyOpenHelper.DESCRIPTION));
		String imageUri = cursor.getString(cursor.getColumnIndex(NasaDailyOpenHelper.IMAGE));
			
		dailyImage.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		new MyImageAsyncTask().execute(imageUri);
		
		dailyTitle.setText(FileUtil.toStyleText(title, Typeface.BOLD), BufferType.SPANNABLE);
		dailyDate.setText(FileUtil.toStyleText(date, Typeface.ITALIC), BufferType.SPANNABLE);
		dailyDescription.setText(description);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}
	
	class MyImageAsyncTask extends AsyncTask<String,Void,Bitmap>{

		@Override
		protected Bitmap doInBackground(String... params) {
			
			File file = new File(fileDir, "bitmap" + dataId + ".jpg");
			
			if(!file.exists()){
				try {
					InputStream input = new URL(params[0]).openStream();
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
				//update database;
				ContentValues values = new ContentValues();
				values.put(NasaDailyOpenHelper.IMAGE, file.getAbsolutePath());
				resolver.update(MyContentProvider.CONTENT_URI, values,
						NasaDailyOpenHelper.KEYWORD+"="+dataId, null);
				
				return BitmapFactory.decodeFile(file.getAbsolutePath());
			}else{
				return BitmapFactory.decodeFile(file.getAbsolutePath());
			}
			
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap){
			
			progressBar.setVisibility(View.INVISIBLE);
			dailyImage.setVisibility(View.VISIBLE);
			dailyImage.setImageBitmap(bitmap);		
		}
	}
}
