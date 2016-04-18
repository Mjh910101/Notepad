package com.zmyh.r.city;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.CityObj;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForObject;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class AreaActivity extends Activity {

	public final static int RequestCode = 1024;
	public final static String COMMON = "common";

	private Context context;

	private List<CityObj> cityList;

	private Bundle bundle;
	private boolean isCommon = false;

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
		dpwnloadData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case RequestCode:
				if (data != null) {
					if (data.getExtras().getBoolean("ok")) {
						setResult(RequestCode, data);
						finish();
					}
				}
				break;
		}
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
		bundle = getIntent().getExtras();
		if (bundle != null) {
			isCommon = bundle.getBoolean(COMMON);
		}
	}

	private void setDataList(List<CityObj> cityList) {
		CityBaseAdapter cba = new CityBaseAdapter(context, cityList);
		cba.setCallback(new CallbackForObject() {

			@Override
			public void callback(Object obj) {
				CityObjBox.saveCityObj((CityObj) obj);
				Passageway.jumpActivity(context, CityActivity.class,
						RequestCode, bundle);
			}
		});
		dataList.setAdapter(cba);
	}

	private void dpwnloadData() {
		progress.setVisibility(View.VISIBLE);
		String url = UrlHandle.getMmArea()
				+ "?query="
				+ JsonHandle.getHttpJsonToString(new String[] { "area_level" },
				new String[] { "0" });

		RequestParams params = HttpUtilsBox.getRequestParams(context);

		HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException exception, String msg) {
						progress.setVisibility(View.GONE);
						ShowMessage.showFailure(context);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						progress.setVisibility(View.GONE);
						String result = responseInfo.result;
						Log.d("", result);

						JSONObject json = JsonHandle.getJSON(result);
						if (!ShowMessage.showException(context, json)) {
							JSONArray array = JsonHandle.getArray(json,
									"result");
							if (array != null) {
								cityList = CityObjHandler.getCityObjList(array);
								setDataList(cityList);
							}

						}
					}

				});
	}

}
