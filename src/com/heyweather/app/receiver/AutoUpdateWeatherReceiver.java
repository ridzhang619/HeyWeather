package com.heyweather.app.receiver;

import com.heyweather.app.service.AutoUpdateWeatherService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateWeatherReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent i = new Intent(context, AutoUpdateWeatherService.class);
		context.startService(i);
	}
	
}
