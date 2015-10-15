package com.heyweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.heyweather.R;
import com.example.heyweather.R.layout;
import com.heyweather.app.db.HeyWeatherDB;
import com.heyweather.app.model.City;
import com.heyweather.app.model.Country;
import com.heyweather.app.model.Province;
import com.heyweather.app.util.Handle;
import com.heyweather.app.util.HttpCallbackListener;
import com.heyweather.app.util.HttpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
	 * ʡ�б�
	 * */
	private List<Province> provinceList;
	/**
	 * ���б�
	 * */
	private List<City> cityList;
	/**
	 * ���б�
	 * */
	private List<Country> countryList;
	/**
	 * ѡ�е�ʡ��
	 */
	private Province selectedProvince;
	/**
	 * ѡ�еĳ���
	 */
	private City selectedCity;
	/**
	 * ��ǰѡ�еļ���
	 */
	private int currentLevel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_area);
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
					queryCities();
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					queryCountries();
				}
			}

		});
		queryProvinces();
	}
	/**
	 * ��ѯȫ�����е���,���ȴ����ݿ��ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ
	 * �����ص�����
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
	 * ��ѯȫ�����е�ʡ,���ȴ����ݿ��ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ
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
			titleText.setText("�й�");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
		
	}
	/**
	 * ��ѯȫʡ���е���,���ȴ����ݿ��ѯ,���û�в�ѯ����ȥ�������ϲ�ѯ
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
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ��������
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
					result = Handle.handleProvinceResponse(heyWeatherDB, response);
				}else if ("city".equals(type)) {
					result = Handle.handleCityResponse(heyWeatherDB, response, selectedProvince.getId());
				}else if ("country".equals(type)) {
					result = Handle.handleCountryResponse(heyWeatherDB, response, selectedCity.getId());
				}
				if (result) {
					//���¼���һ��ʡ������Ϣ
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
				//ͨ��runOnUiThread()�ص����̴߳����߼�
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", 0).show();
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
			progressDialog.setMessage("���ڼ���...");
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
			finish();
		}
	}
}
