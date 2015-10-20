package com.heyweather.app.activity;

import com.baidu.location.LocationClient;
import com.example.heyweather.R;
import com.example.heyweather.R.id;
import com.example.heyweather.R.layout;
import com.heyweather.app.util.Constant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class LocationAcitivity extends Activity {

	private Button addCityWeather;
	
	private ListView lvLocation;

	private LocationClient mLocationClient;

	private String locationList;

	private String[] list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_acitivity);
		setView();
		locationList = getIntent().getStringExtra("location");
		list = new String[]{locationList};
		ArrayAdapter<String> adapter = 
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
		lvLocation.setAdapter(adapter);
		addCityWeather.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LocationAcitivity.this,QuickAddActivity.class);
				startActivity(intent);
				finish();
			}
		});
		lvLocation.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				
			}
		});
	}
	
	private void setView() {
		// TODO Auto-generated method stub
		addCityWeather = (Button)findViewById(R.id.bt_addCityWeather);
		lvLocation = (ListView)findViewById(R.id.lv_location);
	}

	
}
