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
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/**
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/**
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/**
	 * 显示最高气温
	 */
	private TextView temp1Text;
	/**
	 * 显示最低气温
	 */
	private TextView temp2Text;
	/**
	 * 显示当前日期
	 */
	private TextView currentDateTextView;
	/**
	 * 切换城市按钮
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
		 * 一键分享
		 */
		btShare = (Button) findViewById(R.id.share_weather);
		btShare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				ShareSDK.initSDK(WeatherActivity.this);
				OnekeyShare oks = new OnekeyShare();
				// 关闭sso授权
				oks.disableSSOWhenAuthorize();

				// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
				// oks.setNotification(R.drawable.ic_launcher,
				// getString(R.string.app_name));
				// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
				oks.setTitle(getString(R.string.share));
				// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
				oks.setTitleUrl("http://sharesdk.cn");
				// text是分享文本，所有平台都需要这个字段
				oks.setText("我是分享文本");
				// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
				oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
				// url仅在微信（包括好友和朋友圈）中使用
				oks.setUrl("http://sharesdk.cn");
				// comment是我对这条分享的评论，仅在人人网和QQ空间使用
				oks.setComment("我是测试评论文本");
				// site是分享此内容的网站名称，仅在QQ空间使用
				oks.setSite(getString(R.string.app_name));
				// siteUrl是分享此内容的网站地址，仅在QQ空间使用
				oks.setSiteUrl("http://sharesdk.cn");

				// 启动分享GUI
				oks.show(WeatherActivity.this);

			}
		});

		String countryCode = getIntent().getStringExtra("country_code");
		if (!TextUtils.isEmpty(countryCode)) {
			// 有县级代号时就去查询天气
			publishText.setText("同步中...");
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countryCode);
		} else {
			// 没有县级代号时就直接显示本地天气
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
				publishText.setText("同步中...");
				new showWeatherTask().execute();

			}
		});
	}

	/**
	 * 查询县级代号所对应的天气代号
	 * 
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode) {
		String address = "http://www.weather.com.cn/data/list3/city" + countryCode + ".xml";
		queryFromServer(address, "countryCode");
	}

	/**
	 * 查询天气代号所对应的天气
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
		queryFromServer(address, "weatherCode");
	}

	/**
	 * 根据传入的地址和类型向服务器查询天气代号或者天气信息
	 */
	private void queryFromServer(String address, final String type) {
		// TODO Auto-generated method stub
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				if ("countryCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						// 从服务器返回的数据中解析出天气代号
						// "\\|"转义字符
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {
					// 处理服务器返回的天气信息
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
				publishText.setText("同步失败- -");
			}
		});
	}

	/**
	 * 从SharedPreferences文件中读取存储的天气信息,并显示到界面上
	 */
	private void showWeather() {
		Log.i("showWeather", "showWeather................");
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Log.i("weatherInfo", prefs.getString("city_name", "").toString() + "");
		cityNameText.setText(prefs.getString("city_name", ""));
		temp2Text.setText(prefs.getString("temp1", ""));
		temp1Text.setText(prefs.getString("temp2", ""));
		weatherDespText.setText(prefs.getString("weather", ""));
		publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
		Log.i("publish", "今天" + prefs.getString("publish_time", "") + "发布");
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
