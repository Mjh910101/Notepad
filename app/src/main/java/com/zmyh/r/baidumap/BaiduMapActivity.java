package com.zmyh.r.baidumap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.tool.ShowMessage;

public class BaiduMapActivity extends Activity {

	private final static int MAP_ZOOM = 14;

	public final static String TITLE_NAME = "TITLE_NAME";
	public final static String DESC = "DESC";
	public final static String TIME = "TIME";
	public final static String LAT = "LAT";
	public final static String LON = "LON";

	private Context context;
	private double lat = 0, lon = 0;

	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.title_seek)
	private ImageView seekIcon;
	@ViewInject(R.id.baidu_mapView)
	private MapView mMapView;

	private BaiduMap mBaiduMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_baidu);
		ViewUtils.inject(this);
		context = this;
		initActivity();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

	@OnClick({ R.id.title_back })
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_back:
				finish();
				break;
		}
	}

	private void initActivity() {
		titleName.setText("地图信息");
		seekIcon.setVisibility(View.GONE);
		mBaiduMap = mMapView.getMap();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			titleName.setText(bundle.getString(TITLE_NAME));
			lat = bundle.getDouble(LAT);
			lon = bundle.getDouble(LON);
			if (lat <= 0 || lon <= 0) {
				close();
			} else {
				setMapMessage(bundle.getString(TITLE_NAME),
						bundle.getString(TIME), bundle.getString(DESC));
			}
		} else {
			close();
		}
	}

	private void setMapMessage(String name, String time, String desc) {
		Log.e("Baidu", "lat: : " + lat + "   lon : " + lon);
		LatLng point = new LatLng(lat, lon);
		initMapStart(point);
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(R.drawable.map_icon);
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap);
		mBaiduMap.addOverlay(option);

		MapMessageView msg = new MapMessageView(context, name, time, desc);
		InfoWindow mInfoWindow = new InfoWindow(msg, point, -160);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}

	private void initMapStart(LatLng point) {
		MapStatus mMapStatus = new MapStatus.Builder().target(point)
				.zoom(MAP_ZOOM).build();
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
				.newMapStatus(mMapStatus);
		mBaiduMap.animateMapStatus(mMapStatusUpdate, 500);

	}

	private void close() {
		ShowMessage.showToast(context, "地图信息不完整");
		finish();
	}

}
