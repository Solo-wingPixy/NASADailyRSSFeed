package com.jc.nasadailyrssfeed.util;

import android.os.Environment;

public final class FileUtil {
	
    //the recommended paths
	private static final String EXT_STORAGE_PATH_PREFIX="/Android/data";
	private static final String EXT_STORAGE_FILES_PATH_SUFFIX="/files/";
	private static final String EXT_STORAGE_CACHE_PATH_SUFFIX="/cache/";
	
	//Object for intrinsic lock
	public static final Object[] DATA_LOCK = new Object[0];
	
	public FileUtil(){}
	
	/**
	 * use Environment to check the external storage is writable
	 */
    public static boolean isExternalStorageWritable(){
    	return Environment.getExternalStorageState().
    			equals(Environment.MEDIA_MOUNTED);
    }
    
    /**
     * use Environment to check the external storage is readable
     */
    public static boolean isExternalStorageReadable(){
    	if(isExternalStorageWritable()){
    		return true;
    	}
    	return Environment.getExternalStorageState().
    			endsWith(Environment.MEDIA_MOUNTED_READ_ONLY);
    }
    
    
}
