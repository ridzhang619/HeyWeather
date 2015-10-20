package com.heyweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.heyweather.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.heyweather.app.service.AutoUpdateWeatherService;
import com.heyweather.app.util.HttpCallbackListener;
import com.heyweather.app.util.HttpUtil;
import com.heyweather.app.util.Parse;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class WeatherActivity extends FragmentActivity {
	private LinearLayout weatherInfoLayout;
	/**
	 * ������ʾ������
	 */
	private TextView cityNameText;
	/**
	 * ������ʾ����ʱ��
	 */
	private TextView publishText;
	/**
	 * ������ʾ����������Ϣ
	 */
	private TextView weatherDespText;
	/**
	 * ��ʾ�������
	 */
	private TextView temp1Text;
	/**
	 * ��ʾ�������
	 */
	private TextView temp2Text;
	/**
	 * ��ʾ��ǰ����
	 */
	private TextView currentDateTextView;
	/**
	 * �л����а�ť
	 */
	private Button switchCity;
	/**
	 * refreshweather
	 */
	private TextView btShare;
	/**
	 * xialashuaxin
	 */
	private PullToRefreshScrollView refreshWeather;
	private ArrayList<Fragment> fragments;
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		setView();
		android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		fragments = new ArrayList<Fragment>();
		MyPagerAdapter mpa = new MyPagerAdapter(fm);
		viewPager.setAdapter(mpa);
	}

	private void setView() {
		// TODO Auto-generated method stub
		// weatherInfoLayout =
		// (LinearLayout)findViewById(R.id.weather_info_layout);
		viewPager = (ViewPager)findViewById(R.id.vp_weather);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateTextView = (TextView) findViewById(R.id.current_date);
		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
		/**
		 * һ������
		 */
		btShare = (Button) findViewById(R.id.share_weather);
		btShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				ShareSDK.initSDK(WeatherActivity.this);
				OnekeyShare oks = new OnekeyShare();
				// �ر�sso��Ȩ
				oks.disableSSOWhenAuthorize();

				// ����ʱNotification��ͼ������� 2.5.9�Ժ�İ汾�����ô˷���
				// oks.setNotification(R.drawable.ic_launcher,
				// getString(R.string.app_name));
				// title���⣬ӡ��ʼǡ����䡢��Ϣ��΢�š���������QQ�ռ�ʹ��
				oks.setTitle(getString(R.string.share));
				// titleUrl�Ǳ�����������ӣ�������������QQ�ռ�ʹ��
				oks.setTitleUrl("http://sharesdk.cn");
				// text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
				oks.setText("���Ƿ����ı�");
				// imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
				oks.setImagePath("/sdcard/test.jpg");// ȷ��SDcard������ڴ���ͼƬ
				// url����΢�ţ��������Ѻ�����Ȧ����ʹ��
				oks.setUrl("http://sharesdk.cn");
				// comment���Ҷ�������������ۣ�������������QQ�ռ�ʹ��
				oks.setComment("���ǲ��������ı�");
				// site�Ƿ�������ݵ���վ���ƣ�����QQ�ռ�ʹ��
				oks.setSite(getString(R.string.app_name));
				// siteUrl�Ƿ�������ݵ���վ��ַ������QQ�ռ�ʹ��
				oks.setSiteUrl("http://sharesdk.cn");

				// ��������GUI
				oks.show(WeatherActivity.this);

			}
		});

		String countryCode = getIntent().getStringExtra("country_code");
		if (!TextUtils.isEmpty(countryCode)) {
			// ���ؼ�����ʱ��ȥ��ѯ����
			publishText.setText("ͬ����...");
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		} else {
			// û���ؼ�����ʱ��ֱ����ʾ��������
			showWeather();
		}
		switchCity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
				intent.putExtra("from_weather_activity", true);
				startActivity(intent);
				finish();
			}
		});
		refreshWeather.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				// TODO Auto-generated method stub
				publishText.setText("ͬ����...");
				new showWeatherTask().execute();

			}
		});
	}

	/**
	 * ��ѯ�ؼ���������Ӧ����������
	 * 
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countryCode + ".xml";
		queryFromServer(address, "countryCode");
	}

	/**
	 * ��ѯ������������Ӧ������
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	/**
	 * ���ݴ���ĵ�ַ���������������ѯ�������Ż���������Ϣ
	 */
	private void queryFromServer(String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				if ("countryCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// �ӷ��������ص������н�������������
						// "\\|"ת���ַ�
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// ������������ص�������Ϣ
					Parse.handleWeatherResponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}

					});
				}
			}

			@Override
			public void onFail() {
				// TODO Auto-generated method stub
				publishText.setText("ͬ��ʧ��- -");
			}
		});
	}

	/**
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ,����ʾ��������
	 */
	private void showWeather() {
		Log.i("showWeather", "showWeather................");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Log.i("weatherInfo", prefs.getString("city_name", "").toString() + "");
		cityNameText.setText(prefs.getString("city_name", ""));
		temp2Text.setText(prefs.getString("temp1", ""));
		temp1Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather", ""));
		publishText.setText("����" + prefs.getString("publish_time", "") + "����");
		Log.i("publish", "����" + prefs.getString("publish_time", "") + "����");
		currentDateTextView.setText(prefs.getString("current_date", ""));
		// weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent i = new Intent(WeatherActivity.this, AutoUpdateWeatherService.class);
		startService(i);
	}

	class showWeatherTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this);
			String weatherCode = prefs.getString("city_id", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				queryWeatherInfo(weatherCode);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			refreshWeather.onRefreshComplete();
			super.onPostExecute(result);
		}

	}
	class MyPagerAdapter extends FragmentPagerAdapter{

		public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragments.size();
		}
		
	}
}
