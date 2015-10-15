package com.heyweather.app.util;

import com.heyweather.app.db.HeyWeatherDB;
import com.heyweather.app.model.City;
import com.heyweather.app.model.Country;
import com.heyweather.app.model.Province;

import android.text.TextUtils;
import android.util.Log;

public class Handle {
	
	/**
	 * �����ʹ�����������ص�ʡ������
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
					//���������������ݴ洢��province��
//					Log.i("TAG2", province.toString()+"");
					heyWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * �����ʹ�����������ص��м�����
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
					//���������������ݴ洢��city��
					heyWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	/**
	 * �����ʹ�����������ص��ؼ�����
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
					//���������������ݴ洢��country��
					heyWeatherDB.saveCountry(country);
				}
				return true;
			}
		}
		return false;
	}
}
