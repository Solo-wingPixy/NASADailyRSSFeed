package com.jc.nasadailyrssfeed.util;

import java.io.File;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

public class NasaDailyDAO {
		
	private static final String[] result_columns=new String[]{
		NasaDailyOpenHelper.NASA_KEYWORD,NasaDailyOpenHelper.NASA_TITLE,
		NasaDailyOpenHelper.NASA_DATE,NasaDailyOpenHelper.NASA_IMAGE,
		NasaDailyOpenHelper.NASA_DESCRIPTION};
	
	private static final String where=null;
	private static final String whereArgs[]=null;
	private static final String groupBy=null;
	private static final String having=null;
	private static final String orderBy=null;
	
	public static void Add(){}
	public static void Update(SQLiteDatabase db){
		ContentValues values =new ContentValues();
		
		File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"scene.jpg");
		values.put(NasaDailyOpenHelper.NASA_IMAGE,
				file.getAbsolutePath());
		
		db.update(NasaDailyOpenHelper.DATABASE_TABLE_NAME,
				values, where, whereArgs);
	}
	public static void Delete(){}
	
	@SuppressLint("NewApi")
	public static Cursor Query(SQLiteDatabase db){
		return db.query(true, NasaDailyOpenHelper.DATABASE_TABLE_NAME, 
				result_columns, where, whereArgs, groupBy, having, orderBy, null, null);
	}
	
	public static boolean initDatabase(SQLiteDatabase db){
		ContentValues values=new ContentValues();
		values.put(NasaDailyOpenHelper.NASA_TITLE, "美国夫妻散步挖出千枚金币 价值千万美元(图)");
		values.put(NasaDailyOpenHelper.NASA_DATE, "2014年02月26日13:58");
		values.put(NasaDailyOpenHelper.NASA_IMAGE, "R.drawable.coin");
		values.put(NasaDailyOpenHelper.NASA_DESCRIPTION, 
				"据美国侨报网26日报道，近日，一对加州夫妻雇来钱币专家唐·卡根(Don Kagan)鉴别一些金币。这些金币有上千枚，是这对夫妇在经常散步的小路旁挖出的，价值1000万美元。" +
				"唐·卡根不但是钱币专家，还是一名美国西部的经销商和拍卖商。卡根说，从1981年开始，就一直有人带着一两枚金币来找钱币专家，这些金币都价值几千美元。但是，这是第一次有人发现整整几罐金币。“这是百万分之一的机会，比中彩票几率都小，”他说。" +
				"这对夫妻的房产位于加州北部的蒂伯龙市(Tibron， CA)，去年春天，他们就找到这些金币，并与卡根讨论此事，但一直以来，他们并不愿意透露自己的姓名。" +
				"　这对幸运的夫妻表示，自己从来没有想过会发现这样的东西，但奇怪的是，这件事发生后，他们又觉得好像自己一生都在等待这一刻。" +
				"这对夫妻非常熟悉埋有金币的小路，很多年来，他们几乎每天都在这条小路上散步。在散步时，他们无意间发现一个老旧金属罐子的边缘露出地面。“然后，我弯下腰，刮去上面的苔藓，就发现了整个罐口，”这对夫妻说。" +
				"　这是这对夫妻发现的第一个装满金币的金属罐子，他们一共挖到5个这样的罐子。卡根说，他们一共发现大约1427枚未进入流通的崭新金币，金币上的日期是1847年至1894年。" +
				"25日，卡根透露，这对夫妻计划卖掉大部分金币。不过在卖掉之前，这对夫妻将把这些金币存放在美国铅笔协会(American Numismatic Association)，用于27日在亚特兰大举行的钱币展览。");
		db.insert(NasaDailyOpenHelper.DATABASE_TABLE_NAME, null, values);
		
		values.clear();
		values.put(NasaDailyOpenHelper.NASA_TITLE, "乌外交部：新政府组建后将立即恢复同俄对话");
		values.put(NasaDailyOpenHelper.NASA_DATE, "2014年02月26日13:47");
		values.put(NasaDailyOpenHelper.NASA_IMAGE, "R.drawable.whatever");
		values.put(NasaDailyOpenHelper.NASA_DESCRIPTION, 
				"【环球网综合报道】据俄罗斯《观点报》援引俄新社2月26日消息，乌克兰外交部发表声明称，乌克兰新政府组建完成后将立即恢复同俄罗斯的对话。" +
				"自2013年11月下旬以来，乌克兰境内爆发大规模骚乱。期间，乌政府与反政府示威者屡次展开谈判，希望结束冲突，恢复和平。国际社会也持续关注乌克兰局势发展状况。2014年2月23日，乌克兰最高拉达通过表决宣布，前总统亚努科维奇“自动丧失职权”，由乌克兰最高拉达议长图尔奇诺夫担任国家代理总统。" +
				"此后，乌克兰前总理季莫申科获释，乌克兰代理内务部长也撤换多名内务部官员。乌前总统亚努科维奇被通缉。乌克兰新政府组建势在必行。" +
				"　当地时间2月26日凌晨，乌克兰外交部发表声明称，乌克兰新政府组建后将立即恢复同俄罗斯的对话。");
		db.insert(NasaDailyOpenHelper.DATABASE_TABLE_NAME, null, values);
		return true;
	}
}
