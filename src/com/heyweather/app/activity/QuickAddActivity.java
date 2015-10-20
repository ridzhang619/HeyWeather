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
		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

       
		setView();
		cityList = new String[]{"��λ","����","�Ϻ�","���","����","����","����","�麣","��ɽ",
					"�Ͼ�","����","����","����","�ൺ","֣��","ʯ��ׯ","����",
					"����","�人","��ɳ","�ɶ�","̫ԭ","����","����"};
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
	// �ڶ��������ö�λSDK����
		public void initLocation() {
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationMode.Hight_Accuracy);// ��ѡ��Ĭ�ϸ߾��ȣ����ö�λģʽ���߾��ȣ��͹��ģ����豸
			option.setCoorType("bd09ll");// ��ѡ��Ĭ��gcj02�����÷��صĶ�λ�������ϵ
			int span = 1000;
			option.setScanSpan(span);// ��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
			option.setIsNeedAddress(true);// ��ѡ�������Ƿ���Ҫ��ַ��Ϣ��Ĭ�ϲ���Ҫ
			option.setOpenGps(true);// ��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
			option.setLocationNotify(true);// ��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
			option.setIsNeedLocationDescribe(true);// ��ѡ��Ĭ��false�������Ƿ���Ҫλ�����廯�����������BDLocation.getLocationDescribe��õ�����������ڡ��ڱ����찲�Ÿ�����
			option.setIsNeedLocationPoiList(true);// ��ѡ��Ĭ��false�������Ƿ���ҪPOI�����������BDLocation.getPoiList��õ�
			option.setIgnoreKillProcess(false);// ��ѡ��Ĭ��false����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ��ɱ��
			option.SetIgnoreCacheException(false);// ��ѡ��Ĭ��false�������Ƿ��ռ�CRASH��Ϣ��Ĭ���ռ�
			option.setEnableSimulateGps(false);// ��ѡ��Ĭ��false�������Ƿ���Ҫ����gps��������Ĭ����Ҫ
			mLocationClient.setLocOption(option);
		}
	// ��������ʵ��BDLocationListener�ӿ�
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
				if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS��λ���
					sb.append("\nspeed : ");
					sb.append(location.getSpeed());// ��λ������ÿСʱ
					sb.append("\nsatellite : ");
					sb.append(location.getSatelliteNumber());
					sb.append("\nheight : ");
					sb.append(location.getAltitude());// ��λ����
					sb.append("\ndirection : ");
					sb.append(location.getDirection());// ��λ��
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
					sb.append("\ndescribe : ");
					sb.append("gps��λ�ɹ�");

				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// ���綨λ���
					sb.append("\naddr : ");
					sb.append(location.getAddrStr());
					//��Ӫ����Ϣ
					sb.append("\noperationers : ");
					sb.append(location.getOperators());
					sb.append("\ndescribe : ");
					sb.append("���綨λ�ɹ�");
				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// ���߶�λ���
					sb.append("\ndescribe : ");
					sb.append("���߶�λ�ɹ������߶�λ���Ҳ����Ч��");
				} else if (location.getLocType() == BDLocation.TypeServerError) {
					sb.append("\ndescribe : ");
					sb.append("��������綨λʧ�ܣ����Է���IMEI�źʹ��嶨λʱ�䵽loc-bugs@baidu.com��������׷��ԭ��");
				} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
					sb.append("\ndescribe : ");
					sb.append("���粻ͬ���¶�λʧ�ܣ����������Ƿ�ͨ��");
				} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
					sb.append("\ndescribe : ");
					sb.append("�޷���ȡ��Ч��λ���ݵ��¶�λʧ�ܣ�һ���������ֻ���ԭ�򣬴��ڷ���ģʽ��һ���������ֽ�����������������ֻ�");
				}
				sb.append("\nlocationdescribe : ");
				sb.append(location.getLocationDescribe());// λ�����廯��Ϣ
				List<Poi> list = location.getPoiList();// POI����
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
