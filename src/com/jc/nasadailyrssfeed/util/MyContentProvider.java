package com.jc.nasadailyrssfeed.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

public class MyContentProvider extends ContentProvider{
    
	//Log tag
	private static final String TAG="my provider"; 
	
	//Publish Content Uri   ??????? 最后的elements 是什么
	public static final Uri CONTENT_URI=Uri.
				parse("content://com.jc.provider.nasadailyrssfeed/"
	                     +NasaDailyOpenHelper.TABLE_NAME_1);
	
	// Create the constants used to differentiate between
	// the different URI requests.
	private static final int ALLROWS=1;
	private static final int SINGLE_ROW=2;
	
	private static final UriMatcher uriMatcher;
	
	// Populate the UriMatcher object, where a URI ending
	// in ‘elements’ will correspond to a request for all
	// items, and ‘elements/[rowID]’ represents a single row.
	static {
		uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.jc.provider.nasadailyrssfeed",
				NasaDailyOpenHelper.TABLE_NAME_1, ALLROWS);
		uriMatcher.addURI("com.jc.provider.nasadailyrssfeed",
				NasaDailyOpenHelper.TABLE_NAME_1+"/#", SINGLE_ROW);
	}
	
	//SQLite open helper variable
	private NasaDailyOpenHelper openHelper;
	
	//The index(key) column name for use in where clauses.
	public static final String KEY_ID="_id";
	
	// The name and column index of each column in your database.
	// These should be descriptive.
	public static final String KEY_COLUMN_1_NAME="KEY_COLUMN_1_NAME";
	
	@Override
	public boolean onCreate() {
		// Construct the underlying database.
		// Defer opening the database until you need to perform
		// a query or transaction.
		openHelper=new NasaDailyOpenHelper(getContext(),
					NasaDailyOpenHelper.DATABASE_NAME,null,
					NasaDailyOpenHelper.DATABASE_VERSION);
		return true;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// open database
		SQLiteDatabase db;
		try{
			db=openHelper.getWritableDatabase();
		}catch(Exception ex){
			db=openHelper.getReadableDatabase();
		}
		
		// Replace these with valid SQL statements if necessary.
		String groupBy = null;
		String having = null;
		
		//Use an SQLite Query Builder to simplify constructing 
		//the database query
		SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
		
		// Specify the table on which to perform the query. This can
	    // be a specific table or a join as required.
		queryBuilder.setTables(NasaDailyOpenHelper.TABLE_NAME_1);
		
		//If this is a row query,limit the result set to the passed in row
		switch(uriMatcher.match(uri)){
		    case SINGLE_ROW:
		    	String rowID=uri.getPathSegments().get(1);
		    	queryBuilder.appendWhere(NasaDailyOpenHelper.KEYWORD+"="+rowID);
		    default: break;
		}
				
		// Execute the query.
		Cursor cursor=queryBuilder.query(db, projection, selection, 
		           selectionArgs, groupBy, having, sortOrder);
		
		//return the result cursor
		return cursor;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Open a read / write database to support the transaction.
		SQLiteDatabase db = openHelper.getWritableDatabase();
		
		// If this is a row URI, limit the deletion to the specified row.
		switch(uriMatcher.match(uri)){
		   case SINGLE_ROW:
			   String rowId=uri.getPathSegments().get(1);
			   selection=NasaDailyOpenHelper.KEYWORD+"="+rowId
					   +(!TextUtils.isEmpty(selection)?" AND ("+selection+')' :"");
		   default: break;
		}
		
		// To return the number of deleted items you must specify a where
		// clause. To delete all rows and return a value pass in “1”.
		if(selection==null)
			selection="1";
		
		//Perform the deletion。
		int deleteCount=db.delete(NasaDailyOpenHelper.TABLE_NAME_1,
				selection, selectionArgs);
		
		// Notify any observers of the change in the data set.
		getContext().getContentResolver().notifyChange(uri, null);
		
		// Return the number of deleted items.
		return deleteCount;
	}

	@Override
	public String getType(Uri uri) {
		// Return a string that identifies the MIME type
		// for a Content Provider URI
		switch(uriMatcher.match(uri)){
		    case ALLROWS:
		    	return "vnd.android.cursor.dir/vnd.com.jc.provider.nasadailyrssfeed."
		    			+NasaDailyOpenHelper.TABLE_NAME_1;
		    case SINGLE_ROW:
		    	return "vnd.android.cursor.item/vnd.com.jc.provider.nasadailyrssfeed."
		    			+NasaDailyOpenHelper.TABLE_NAME_1;
		    default:
		    	throw new IllegalArgumentException("Unsupported URI: "+uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Open a read / write database to support the transaction.
		SQLiteDatabase db = openHelper.getWritableDatabase();
		
		// To add empty rows to your database by passing in an empty
		// Content Values object you must use the null column hack
		// parameter to specify the name of the column that can be
		// set to null.
		String nullColumnHack=null;
		
		// Insert the values into the table
		long id=db.insert(NasaDailyOpenHelper.TABLE_NAME_1, 
				nullColumnHack, values);
		
		//Construct and return the URI of the newly inserted row.
		if(id>-1){
			// Construct and return the URI of the newly inserted row.
			Uri insertedId=ContentUris.withAppendedId(CONTENT_URI, id);
			
			// Notify any observers of the change in the data set.
			getContext().getContentResolver().notifyChange(insertedId, null);
			
			return insertedId;
		}else
		    return null;
	}
    
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// Open a read / write database to support the transaction.
		SQLiteDatabase db=openHelper.getWritableDatabase();
		
		// If this is a row URI, limit the deletion to the specified row.
		switch(uriMatcher.match(uri)){
		     case SINGLE_ROW:
		    	 String rowID=uri.getPathSegments().get(1);
		    	 selection=NasaDailyOpenHelper.KEYWORD+"="+rowID+
		    			 (!TextUtils.isEmpty(selection)?" AND ("+selection+')':"");
		     default:break;
		}
		
		// Perform the update.
		int updateCount=db.update(NasaDailyOpenHelper.TABLE_NAME_1, 
				values, selection, selectionArgs);
		
		// Notify any observers of the change in the data set.
		getContext().getContentResolver().notifyChange(uri, null);
		
		return updateCount;
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
	         throws FileNotFoundException{
				
		// Find the row ID and use it as a filename.
		String rowID=uri.getPathSegments().get(1);
		
		// Create a file object in the application’s external
		// files directory.
		String picsDir=Environment.DIRECTORY_PICTURES;
		File file=
				new File(getContext().getExternalFilesDir(picsDir),rowID);
		
		// If the file doesn’t exist, create it now.
		if(!file.exists()){
			try{
				file.createNewFile();
			}catch(IOException e){
				Log.d(TAG, "File creation failed: " + e.getMessage());
			}
		}
		
		// Translate the mode parameter to the corresponding Parcel File
		// Descriptor open mode.
		int fileMode=0;
		if(mode.contains("w"))
			fileMode |=ParcelFileDescriptor.MODE_WRITE_ONLY;
		if(mode.contains("r"))
			fileMode |=ParcelFileDescriptor.MODE_READ_ONLY;
		if(mode.contains("+"))
			fileMode |=ParcelFileDescriptor.MODE_APPEND;
		
		// Return a Parcel File Descriptor that represents the file.
		return ParcelFileDescriptor.open(file, fileMode);
	}
    
	
}
