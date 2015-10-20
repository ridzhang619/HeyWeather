package com.heyweather.app.activity;

import java.util.List;

import com.baidu.android.common.logging.Log;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.example.heyweather.R;
import com.example.heyweather.R.id;
import com.example.heyweather.R.layout;
import com.example.heyweather.R.menu;
import com.heyweather.app.util.Constant;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

public class QuickAddActivity extends Activity {
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private GridView gvQuickAdd;
	private String[] cityList;
	private Handler handler;
	private TextView etSearch;
	private Vibrator mVibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quick_add);
		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

       
		setView();
		cityList = new String[]{"定位","北京","上海","天津","重庆","广州","深圳","珠海","佛山",
					"南京","苏州","杭州","济南","青岛","郑州","石家庄","福州",
					"厦门","武汉","长沙","成都","太原","沈阳","南宁"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,cityList);
		gvQuickAdd.setAdapter(adapter);
		gvQuickAdd.setOnItemClickListener(new OnItemClickListener() {


			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					 initLocation();
					mLocationClient.start();
					
					break;
				case 1:
					
					break;

				default:
					break;
				}
		
			}
		});
		
	}

	private void setView() {
		// TODO Auto-generated method stub
		gvQuickAdd = (GridView)findViewById(R.id.gv_quickadd);
		etSearch = (TextView)findViewById(R.id.et_search);
	}
	// 第二步，配置定位SDK参数
		public void initLocation() {
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
			option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
			int span = 1000;
			option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
			option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
			option.setOpenGps(true);// 可选，默认false,设置是否使用gps
			option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
			option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
			option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
			option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
			option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
			option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
			mLocationClient.setLocOption(option);
		}
	// 第三步，实现BDLocationListener接口
		public class MyLocationListener implements BDLocationListener {
			
			private String district;
			private String city;

			@Override
			public void onReceiveLocation(BDLocation location) {
				//Receive Location
				StringBuffer sb = new StringBuffer(256);
				sb.append("time : ");
				sb.append(location.getTime());
				sb.append("\nerror code : ");
				sb.append(location.getLocType());
				sb.append("\nlatitude : ");
				sb.append(location.getLatitude());
				sb.append("\nlontitude : ");
				sb.append(location.getLongitude());
				sb.append("\nradius : ");
				sb.append(location.getRadius());
				if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
					sb.append("\nspeed : ");
					sb.append(location.getSpeed());// 单位：公里每小时
					sb.append("\nsatellite : ");
					sb.append(location.getSatelliteNumber());
					sb.append("\nheight : ");
					sb.append(location.getAltitude());// 单位：米
					sb.append("\ndirection : ");
					sb.append(location.getDirection());// 单位度
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
					sb.append("\ndescribe : ");
					sb.append("gps定位成功");

				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
					//运营商信息
					sb.append("\noperationers : ");
					sb.append(location.getOperators());
					sb.append("\ndescribe : ");
					sb.append("网络定位成功");
				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
					sb.append("\ndescribe : ");
					sb.append("离线定位成功，离线定位结果也是有效的");
				} else if (location.getLocType() == BDLocation.TypeServerError) {
					sb.append("\ndescribe : ");
					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
				} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
					sb.append("\ndescribe : ");
					sb.append("网络不同导致定位失败，请检查网络是否通畅");
				} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
					sb.append("\ndescribe : ");
					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
				}
				sb.append("\nlocationdescribe : ");
				sb.append(location.getLocationDescribe());// 位置语义化信息
				List<Poi> list = location.getPoiList();// POI数据
				if (list != null) {
					sb.append("\npoilist size = : ");
					sb.append(list.size());
					for (Poi p : list) {
						sb.append("\npoi= : ");
						sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
					}
				}
				Log.i("BaiduLocationApiDem", sb.toString());
				district = location.getDistrict();
				Intent intent = new Intent(QuickAddActivity.this,LocationAcitivity.class);
				intent.putExtra("location",district);
				startActivity(intent);
				finish();
			}
		}
}
