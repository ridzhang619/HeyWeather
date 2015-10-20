package com.heyweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.heyweather.R;
import com.example.heyweather.R.layout;
import com.heyweather.app.db.HeyWeatherDB;
import com.heyweather.app.model.City;
import com.heyweather.app.model.Country;
import com.heyweather.app.model.Province;
import com.heyweather.app.util.Parse;
import com.heyweather.app.util.HttpCallbackListener;
import com.heyweather.app.util.HttpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity {
	
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private HeyWeatherDB heyWeatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * 省列表
	 * */
	private List<Province> provinceList;
	/**
	 * 市列表
	 * */
	private List<City> cityList;
	/**
	 * 县列表
	 * */
	private List<Country> countryList;
	/**
	 * 选中的省份
	 */
	private Province selectedProvince;
	/**
	 * 选中的城市
	 */
	private City selectedCity;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	private boolean isFromWeatherActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_area);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		//已经选择了城市且不是从WeatherActivity跳转过来,才会直接跳转到WeatherActivity
		if (prefs.getBoolean("city_selected", false)&& !isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		titleText = (TextView)findViewById(R.id.title_text);
		listView = (ListView)findViewById(R.id.lv_view);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		Log.i("Rid", "111111111111");
		heyWeatherDB = HeyWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinceList.get(position);
					//加载城市列表
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					//加载县列表
					queryCountries();
				}else if (currentLevel == LEVEL_COUNTRY) {
					String countryCode = countryList.get(position).getCountryCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("country_code", countryCode);
					startActivity(intent);
					finish();
				}
			}

		});
		queryProvinces();
	}
	/**
	 * 查询全市所有的县,优先从数据库查询,如果没有查询到再去服务器上查询
	 * 加载县的数据
	 */
	protected void queryCountries() {
		// TODO Auto-generated method stub
		countryList = heyWeatherDB.loadCountries(selectedCity.getId());
		Log.i("DATA", "DTATATATATATATATATA");
		if (countryList.size()>0) {
			dataList.clear();
			for (Country country : countryList) {
				dataList.add(country.getCountryName());
			}
			Log.i("DATA", ""+dataList.toString());
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"country");
		}
	}
	/**
	 * 查询全国所有的省,优先从数据库查询,如果没有查询到再去服务器上查询
	 */
	private void queryProvinces() {
		// TODO Auto-generated method stub
		provinceList = heyWeatherDB.loadProvince();
		if (provinceList.size()>0) {
			dataList.clear();
			for (Province province : provinceList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
		
	}
	/**
	 * 查询全省所有的市,优先从数据库查询,如果没有查询到再去服务器上查询
	 */
	
	private void queryCities() {
		// TODO Auto-generated method stub
		cityList = heyWeatherDB.loadCities(selectedProvince.getId());
		if (cityList.size()>0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel =LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
		
	}
	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 */
	private void queryFromServer(String code, final String type) {
		// TODO Auto-generated method stub
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				boolean result = false;
				if ("province".equals(type)) {
					result = Parse.handleProvinceResponse(heyWeatherDB, response);
				}else if ("city".equals(type)) {
					result = Parse.handleCityResponse(heyWeatherDB, response, selectedProvince.getId());
				}else if ("country".equals(type)) {
					result = Parse.handleCountryResponse(heyWeatherDB, response, selectedCity.getId());
				}
				if (result) {
					//重新加载一遍省市县信息
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							}else if ("city".equals(type)) {
								queryCities();
							}else if ("country".equals(type)) {
								queryCountries();
							}
						}

					});
				}
			}
			
			@Override
			public void onFail() {
				//通过runOnUiThread()回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", 0).show();
					}
				});
			}
		});
	}
	private void closeProgressDialog() {
		// TODO Auto-generated method stub
		if (progressDialog!=null) {
			progressDialog.dismiss();
		}
	}
	private void showProgressDialog() {
		if (progressDialog==null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
	}
	
	@Override
	public void onBackPressed() {
		if (currentLevel==LEVEL_COUNTRY) {
			queryCities();
		}else if (currentLevel==LEVEL_CITY) {
			queryProvinces();
		}else{
			if (isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
}
