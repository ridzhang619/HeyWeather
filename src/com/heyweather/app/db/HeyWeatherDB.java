package com.heyweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.heyweather.app.model.City;
import com.heyweather.app.model.Country;
import com.heyweather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HeyWeatherDB {
	/**
	 * 数据库名称
	 * */
	public static final String DB_NAME = "hey_weather";
	/**
	 * 数据库版本
	 * */
	public static final int VERSION = 1;
	
	private static HeyWeatherDB heyWeatherDB;
	
	private SQLiteDatabase db;

	/**
	 * 单例模式,构造方法私有化,不能在其他类中创建此类对象
	 * */
	private HeyWeatherDB(Context context){
		Log.i("Rid", "333333333333333333");
		HeyWeatherOpenHelper dbHelper = 
				new HeyWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * 获得HeyWeatherDB实例
	 * */
	public synchronized static HeyWeatherDB getInstance(Context context){
		Log.i("Rid", "222222222222222222");
		if (heyWeatherDB == null) {
			heyWeatherDB = new HeyWeatherDB(context);
		}
		
		return heyWeatherDB;
	}
	/**
	 * 将Province实例存储到数据库
	 * */
	public void saveProvince(Province province){
		if (province!=null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	/**
	 * 从数据库读取全国所有的省份信息(查询)
	 * */
	public List<Province> loadProvince(){
		List<Province> lists = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Province province = new Province();
			province.setId(cursor.getInt(cursor.getColumnIndex("id")));
			province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
			province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
			lists.add(province);
		}
		return lists;
	}
	/**
	 * 将City实例存储到数据库
	 * */
	public void saveCity(City city){
		if (city!=null) {
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	/**
	 * 从数据库读取某省的所有城市信息(查询)
	 * */
	public List<City> loadCities(int provinceId){
		List<City> lists = new ArrayList<City>();
		Cursor cursor = db.query("City", null, 
				"province_Id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
		while (cursor.moveToNext()) {
			City city = new City();
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
			city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
			city.setProvinceId(provinceId);
			lists.add(city);
		}
		return lists;
	}
	
	/**
	 * 将Country实例存储到数据库
	 * */
	public void saveCountry(Country country){
		if (country!=null) {
			ContentValues values = new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountryCode());
			values.put("city_id", country.getCityId());
			db.insert("Country", null, values);
		}
	}
	/**
	 * 从数据库读取某城市所有的县信息
	 * */
	public List<Country> loadCountries(int cityId){
		List<Country> lists = new ArrayList<Country>();
		Cursor cursor = db.query("Country", null, 
				"city_Id=?", new String[]{String.valueOf(cityId)}, null, null, null);
		while (cursor.moveToNext()) {
			Country country = new Country();
			country.setId(cursor.getInt(cursor.getColumnIndex("id")));
			country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
			country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
			country.setCityId(cityId);
			lists.add(country);
		}
		return lists;
	}
}
