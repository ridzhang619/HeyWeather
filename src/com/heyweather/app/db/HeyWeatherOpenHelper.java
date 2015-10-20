package com.heyweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HeyWeatherOpenHelper extends SQLiteOpenHelper{
	
	/**
	 * Province表建表语句
	 * */
	public static final String CREATE_PROVINCE = "create table Province("
			+ "id integer primary key autoincrement,"
			+ "province_name text,"
			+ "province_code text)";
	/**
	 * City表建表语句
	 * */
	public static final String CREATE_CITY = "create table City("
			+ "id integer primary key autoincrement,"
			+ "city_name text,"
			+ "city_code text,"
			+ "province_id integer)";
	/**
	 * Country表建表语句
	 * */
	public static final String CREATE_COUNTRY = "create table Country("
			+ "id integer primary key autoincrement,"
			+ "country_name text,"
			+ "country_code text,"
			+ "city_id integer)";
	/**
	 * 天气队列添加表
	 */
	public static final String CREATE_ADD_CITY = "create table AddCity("
			+ "id integer primary key autoincrement,"
			+ "addcity_name text,"
			+ "addcity_code text)";

	public HeyWeatherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		Log.i("Rid", "44444444444444444");
	}
	//此方法中分别建三张表为省,市,县
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i("Rid", "55555555555555555555555555");
		db.execSQL(CREATE_PROVINCE);
		Log.i("Rid", "666666666666666666666666666");
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTRY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
