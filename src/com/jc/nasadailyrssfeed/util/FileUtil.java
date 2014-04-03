package com.jc.nasadailyrssfeed.util;

import java.io.File;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

public final class FileUtil {

	/** the recommended paths
	private static final String EXT_STORAGE_PATH_PREFIX = "/Android/data";
	private static final String EXT_STORAGE_FILES_PATH_SUFFIX = "/files/";
	private static final String EXT_STORAGE_CACHE_PATH_SUFFIX = "/cache/";*/
    
	public static final File fileDir=Environment.
			getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	
	// Object for intrinsic lock
	public static final Object[] DATA_LOCK = new Object[0];

	public FileUtil() {
	}

	/**
	 * use Environment to check the external storage is writable
	 */
	public static boolean isExternalStorageWritable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * use Environment to check the external storage is readable
	 */
	public static boolean isExternalStorageReadable() {
		if (isExternalStorageWritable()) {
			return true;
		}
		return Environment.getExternalStorageState().endsWith(
				Environment.MEDIA_MOUNTED_READ_ONLY);
	}

	/**
	 * check network is available
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivity.getActiveNetworkInfo();

		if (info != null && info.isConnected())
			return true;
		else
			return false;
	}
	
	/**
	 * check if is the time to update nasa daily image
	 */
	public static boolean isTimeToUpdate(long timeOfLastUpdate){
		
		long currentTime = System.currentTimeMillis();
		long deltaTime = (currentTime/1000-timeOfLastUpdate)/(60*60);
		if(deltaTime>24)
			return true;
		else
		    return false;
	}
	
	public static CharSequence toStyleText(CharSequence original,int style){
		SpannableStringBuilder sbuilder = new SpannableStringBuilder(original);
		StyleSpan span= new StyleSpan(style);
		sbuilder.setSpan(span, 0, sbuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return sbuilder;		
	}
}
