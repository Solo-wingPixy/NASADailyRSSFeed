package com.jc.nasadailyrssfeed.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

   public class NasaDailyOpenHelper extends SQLiteOpenHelper{
    
	 //Log tag
	private static final String TAG="my provider"; 
	
	public static final int DATABASE_VERSION=1;
	public static final String DATABASE_NAME="NASARSSFeed";
	public static final String TABLE_NAME_1="Nasa_daily_Image";
	
	public static final String KEYWORD="_id";
	public static final String DATE="date";
	public static final String TITLE="title";
	public static final String IMAGE="_data";
	public static final String DESCRIPTION="description";
	public static final String IS_READ="is_read";
	
	private static final String TABLE_CREATE_1="create table "+TABLE_NAME_1
			+"( "+KEYWORD+" integer primary key autoincrement, "
			     +TITLE+" varchar(255) not null unique, "
			     +DATE +" datatime not null, "
			     +IMAGE+" string, "
			     +DESCRIPTION+" text not null, "
			     +IS_READ+" integer not null default 0) ";
	
	public NasaDailyOpenHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public NasaDailyOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
    
	//called when no database exits in disk and 
	//the helper class need to create a new one.
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(TABLE_CREATE_1);
		Log.w(TAG, "create db sucessful");
	}
    
	// Called when there is a database version mismatch meaning that
	// the version of the database on disk needs to be upgraded to
	// the current version.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(TAG, "Upgrading from version "+oldVersion+" to "+newVersion
				+" ,which will destroy all old data");
		
		// Upgrade the existing database to conform to the new
		// version. Multiple previous versions can be handled by
		// comparing oldVersion and newVersion values.
		// The simplest case is to drop the old table and create a new one.
		db.execSQL("drop table if it exists "+TABLE_NAME_1);
		// Create a new one.
		onCreate(db);
	}
	
}