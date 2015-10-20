package com.heyweather.app.service;

import org.apache.http.protocol.HTTP;

import com.heyweather.app.util.HttpCallbackListener;
import com.heyweather.app.util.HttpUtil;
import com.heyweather.app.util.Parse;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class AutoUpdateWeatherService extends Service{

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWeather();
			}
		}).start();
		//设置每隔六个小时就执行一次更新
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int anHour = 6*60*60*1000;
		long triggerAtTime = SystemClock.elapsedRealtime()+anHour;
		Intent i = new Intent(this, AutoUpdateWeatherService.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
		return super.onStartCommand(intent, flags, startId);
	}

	protected void updateWeather() {
		// TODO Auto-generated method stub
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather", "");
		String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				Parse.handleWeatherResponse(AutoUpdateWeatherService.this, response);
				
			}
			
			@Override
			public void onFail() {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
}
