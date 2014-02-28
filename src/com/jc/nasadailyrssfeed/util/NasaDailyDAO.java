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
		values.put(NasaDailyOpenHelper.NASA_TITLE, "��������ɢ���ڳ�ǧö��� ��ֵǧ����Ԫ(ͼ)");
		values.put(NasaDailyOpenHelper.NASA_DATE, "2014��02��26��13:58");
		values.put(NasaDailyOpenHelper.NASA_IMAGE, "R.drawable.coin");
		values.put(NasaDailyOpenHelper.NASA_DESCRIPTION, 
				"�������ȱ���26�ձ��������գ�һ�Լ��ݷ��޹���Ǯ��ר���ơ�����(Don Kagan)����һЩ��ҡ���Щ�������ǧö������Է��ھ���ɢ����С·���ڳ��ģ���ֵ1000����Ԫ��" +
				"�ơ�����������Ǯ��ר�ң�����һ�����������ľ����̺������̡�����˵����1981�꿪ʼ����һֱ���˴���һ��ö�������Ǯ��ר�ң���Щ��Ҷ���ֵ��ǧ��Ԫ�����ǣ����ǵ�һ�����˷����������޽�ҡ������ǰ����֮һ�Ļ��ᣬ���в�Ʊ���ʶ�С������˵��" +
				"��Է��޵ķ���λ�ڼ��ݱ����ĵٲ�����(Tibron�� CA)��ȥ�괺�죬���Ǿ��ҵ���Щ��ң����뿨�����۴��£���һֱ���������ǲ���Ը��͸¶�Լ���������" +
				"��������˵ķ��ޱ�ʾ���Լ�����û������ᷢ�������Ķ���������ֵ��ǣ�����·����������־��ú����Լ�һ�����ڵȴ���һ�̡�" +
				"��Է��޷ǳ���Ϥ���н�ҵ�С·���ܶ����������Ǽ���ÿ�춼������С·��ɢ������ɢ��ʱ����������䷢��һ���Ͼɽ������ӵı�Ե¶�����档��Ȼ��������������ȥ�����̦޺���ͷ����������޿ڣ�����Է���˵��" +
				"��������Է��޷��ֵĵ�һ��װ����ҵĽ������ӣ�����һ���ڵ�5�������Ĺ��ӡ�����˵������һ�����ִ�Լ1427öδ������ͨ��ո�½�ң�����ϵ�������1847����1894�ꡣ" +
				"25�գ�����͸¶����Է��޼ƻ������󲿷ֽ�ҡ�����������֮ǰ����Է��޽�����Щ��Ҵ��������Ǧ��Э��(American Numismatic Association)������27��������������е�Ǯ��չ����");
		db.insert(NasaDailyOpenHelper.DATABASE_TABLE_NAME, null, values);
		
		values.clear();
		values.put(NasaDailyOpenHelper.NASA_TITLE, "���⽻�����������齨�������ָ�ͬ��Ի�");
		values.put(NasaDailyOpenHelper.NASA_DATE, "2014��02��26��13:47");
		values.put(NasaDailyOpenHelper.NASA_IMAGE, "R.drawable.whatever");
		values.put(NasaDailyOpenHelper.NASA_DESCRIPTION, 
				"���������ۺϱ������ݶ���˹���۵㱨��Ԯ��������2��26����Ϣ���ڿ����⽻�����������ƣ��ڿ����������齨��ɺ������ָ�ͬ����˹�ĶԻ���" +
				"��2013��11����Ѯ�������ڿ������ڱ������ģɧ�ҡ��ڼ䣬�������뷴����ʾ�����Ŵ�չ��̸�У�ϣ��������ͻ���ָ���ƽ���������Ҳ������ע�ڿ������Ʒ�չ״����2014��2��23�գ��ڿ����������ͨ�����������ǰ��ͳ��Ŭ��ά�桰�Զ�ɥʧְȨ�������ڿ�����������鳤ͼ����ŵ���ι��Ҵ�����ͳ��" +
				"�˺��ڿ���ǰ����Ī��ƻ��ͣ��ڿ����������񲿳�Ҳ�����������񲿹�Ա����ǰ��ͳ��Ŭ��ά�汻ͨ�����ڿ����������齨���ڱ��С�" +
				"������ʱ��2��26���賿���ڿ����⽻�����������ƣ��ڿ����������齨�������ָ�ͬ����˹�ĶԻ���");
		db.insert(NasaDailyOpenHelper.DATABASE_TABLE_NAME, null, values);
		return true;
	}
}
