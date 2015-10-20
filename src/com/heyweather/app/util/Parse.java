package com.heyweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.heyweather.app.db.HeyWeatherDB;
import com.heyweather.app.model.City;
import com.heyweather.app.model.Country;
import com.heyweather.app.model.Province;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Parse {
	
	/**
	 * 解析和处理服务器返回的省级数据
	 * */
	public synchronized static boolean handleProvinceResponse(HeyWeatherDB 
			heyWeatherDB,String response){
		if (!TextUtils.isEmpty(response)) {
			Log.i("TAG1", response+"");
			String[] allProvinces = response.split(",");
			Log.i("TAG2", allProvinces.toString()+"");
			if (allProvinces!=null && allProvinces.length>0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出来的数据存储到province表
//					Log.i("TAG2", province.toString()+"");
					heyWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析和处理服务器返回的市级数据
	 * */
	public  static boolean handleCityResponse(HeyWeatherDB 
			heyWeatherDB,String response,int provinceId){
		if (!TextUtils.isEmpty(response)) {
			Log.i("TAG3", response+"");
			String[] allCities = response.split(",");
			if (allCities!=null && allCities.length>0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//将解析出来的数据存储到city表
					heyWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析和处理服务器返回的县级数据
	 * */
	public  static boolean handleCountryResponse(HeyWeatherDB 
			heyWeatherDB,String response,int cityId){
		if (!TextUtils.isEmpty(response)) {
			Log.i("TAG4", response+"");
			String[] allCountries = response.split(",");
			if (allCountries!=null && allCountries.length>0) {
				for (String c : allCountries) {
					String[] array = c.split("\\|");
					Country country = new Country();
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setCityId(cityId);
					//将解析出来的数据存储到country表
					heyWeatherDB.saveCountry(country);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * 解析服务器返回的JSON数据,将解析出的数据存储到本地
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String cityId = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weather = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			saveWeatherInfo(context, cityName, cityId, temp1, temp2, weather, publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 将解析出来的天气数据存储到sharedPreferences文件中
	 */
	public static void saveWeatherInfo(Context context,String cityName,
			String cityId,String temp1,String temp2,String weather,String publishTime){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		SharedPreferences.Editor editor = 
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("city_id", cityId);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather", weather);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", format.format(new Date()));
		editor.commit();
	}
}
