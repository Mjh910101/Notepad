package com.zmyh.r.handler;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

public class MapHandler {

	public interface MapListener {
		public void callback(BDLocation location);
	}

	public static void getPicAddress(Context context, final MapListener callback) {
		final LocationClient mLocationClient = new LocationClient(context);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Device_Sensors);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		option.SetIgnoreCacheException(true);
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				callback.callback(location);
				Log.e("baidu", "lat: " + location.getLatitude() + "    lon : "
						+ location.getLongitude());
				mLocationClient.stop();
			}
		});

		mLocationClient.start();
	}

}
