package com.heyweather.app.util;

import com.heyweather.app.db.HeyWeatherDB;
import com.heyweather.app.model.City;
import com.heyweather.app.model.Country;
import com.heyweather.app.model.Province;

import android.text.TextUtils;
import android.util.Log;

public class Handle {
	
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
}
