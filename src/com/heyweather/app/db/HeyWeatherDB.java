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
	 * ���ݿ�����
	 * */
	public static final String DB_NAME = "hey_weather";
	/**
	 * ���ݿ�汾
	 * */
	public static final int VERSION = 1;
	
	private static HeyWeatherDB heyWeatherDB;
	
	private SQLiteDatabase db;

	/**
	 * ����ģʽ,���췽��˽�л�,�������������д����������
	 * */
	private HeyWeatherDB(Context context){
		Log.i("Rid", "333333333333333333");
		HeyWeatherOpenHelper dbHelper = 
				new HeyWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * ���HeyWeatherDBʵ��
	 * */
	public synchronized static HeyWeatherDB getInstance(Context context){
		Log.i("Rid", "222222222222222222");
		if (heyWeatherDB == null) {
			heyWeatherDB = new HeyWeatherDB(context);
		}
		
		return heyWeatherDB;
	}
	/**
	 * ��Provinceʵ���洢�����ݿ�
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
	 * �����ݿ��ȡȫ�����е�ʡ����Ϣ(��ѯ)
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
	 * ��Cityʵ���洢�����ݿ�
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
	 * �����ݿ��ȡĳʡ�����г�����Ϣ(��ѯ)
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
	 * ��Countryʵ���洢�����ݿ�
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
	 * �����ݿ��ȡĳ�������е�����Ϣ
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
