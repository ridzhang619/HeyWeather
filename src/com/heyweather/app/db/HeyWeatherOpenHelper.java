package com.heyweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class HeyWeatherOpenHelper extends SQLiteOpenHelper{
	
	/**
	 * Province表建表语句
	 * */
	public static final String CREATE_PROVINCE = "create table province("
			+ "id integer primary key autocrement,"
			+ "province_name text,"
			+ "province_code text)";
	/**
	 * City表建表语句
	 * */
	public static final String CREATE_CITY = "create table city("
			+ "id integer primary key autocrement,"
			+ "city_name text,"
			+ "city_code text,"
			+ "province_id interger)";
	/**
	 * Country表建表语句
	 * */
	public static final String CREATE_COUNTRY = "create table country("
			+ "id integer primary key autocrement,"
			+ "country_name text,"
			+ "country_code text,"
			+ "city_id interger)";

	public HeyWeatherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	//此方法中分别建三张表为省,市,县
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTRY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

}
