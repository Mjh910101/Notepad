package com.zmyh.r.city;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.CityObj;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.interfaces.CallbackForObject;

public class CityActivity extends Activity {

	private Context context;

	private CityObj mCityObj;
	private Bundle bundle;
	private boolean isCommon;

	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.city_dataList)
	private ListView dataList;
	@ViewInject(R.id.city_progress)
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);
		ViewUtils.inject(this);
		context = this;
		initActivity();
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
		titleName.setText("区域");
		mCityObj = CityObjBox.getSaveCityObj();
		bundle = getIntent().getExtras();
		if (bundle != null) {
			isCommon = bundle.getBoolean(AreaActivity.COMMON);
		}
		if (mCityObj != null) {
			titleName.setText(mCityObj.getArea_name());
			setDataList(mCityObj.getCities());
		}
	}

	private void setDataList(List<CityObj> cityList) {
		CityBaseAdapter cba = new CityBaseAdapter(context, cityList);
		cba.setCallback(new CallbackForObject() {

			@Override
			public void callback(Object object) {
				CityObj obj = (CityObj) object;
				if (!isCommon) {
					CityObjHandler.saveCityObj(context, (CityObj) obj,
							mCityObj.getArea_name(), mCityObj.getArea_code());
				}
				Intent i = new Intent();
				Bundle b = new Bundle();
				b.putBoolean("ok", true);
				b.putString("Area_name", obj.getArea_name());
				b.putString("Area_code", obj.getArea_code());
				b.putString("Area_id", obj.get_id());
				b.putDouble("Area_lat", obj.getArea_lat());
				b.putDouble("Area_lng", obj.getArea_lng());
				i.putExtras(b);
				setResult(AreaActivity.RequestCode, i);
				finish();
			}
		});
		dataList.setAdapter(cba);
	}
}
