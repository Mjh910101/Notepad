package com.zmyh.r.main.forum;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import com.zmyh.r.box.DynamicObj;
import com.zmyh.r.city.AreaActivity;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.DynamicObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class ForumFrameLayout extends Fragment {

	private final static int pageSize = 12;

	public static boolean UPLOAD = false;

	private Context context;

	private String mmArea = "";
	private int pageIndex = 1, totalPage = 1;

	private ForumBaseAdapter fba = null;

	@ViewInject(R.id.frame_dataList)
	private ListView dataList;
	@ViewInject(R.id.frame_cityText)
	private TextView cityText;
	@ViewInject(R.id.frame_progress)
	private ProgressBar progress;
	@ViewInject(R.id.frame_dataListRefresh)
	private SwipeRefreshLayout dataListRefresh;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		context = getActivity();
		View contactsLayout = inflater.inflate(R.layout.layout_frame,
				container, false);
		ViewUtils.inject(this, contactsLayout);
		initCity();
		initHeadView();
		downloadData();
		setDataListScrollListener();
		setOnRefreshListener();
		return contactsLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (UPLOAD) {
			pageIndex = 1;
			fba.removeAll();
			downloadData();
		} else {
			if (fba != null) {
				fba.notifyDataSetChanged();
			}
		}
	}

	@OnClick({ R.id.frame_cityText })
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.frame_cityText:
				Bundle b = new Bundle();
				b.putBoolean(AreaActivity.COMMON, true);
				Passageway.jumpActivity(context, AreaActivity.class,
						AreaActivity.RequestCode, b);
				break;
		}
	}

	private void initHeadView() {
		dataList.addHeaderView(new ForumHeadView(context));
		fba = new ForumBaseAdapter(context);
		dataList.setAdapter(fba);
	}

	public void setCityData(String name, String id) {
		cityText.setText(name);
		mmArea = id;
		pageIndex = 1;
		if (fba != null) {
			fba.removeAll();
		}
		downloadData();
	}

	private void initCity() {
		cityText.setText(CityObjHandler.getCityName(context) + "苗木圈");
		mmArea = CityObjHandler.getCityId(context);
	}

	private void setOnRefreshListener() {
		dataListRefresh.setColorScheme(R.color.holo_blue_bright,
				R.color.holo_green_light, R.color.holo_orange_light,
				R.color.holo_red_light);
		dataListRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				pageIndex = 1;
				if (fba != null) {
					fba.removeAll();
				}
				downloadData();
			}
		});
	}

	private void setDataListScrollListener() {
		dataList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
						if (progress.getVisibility() == View.GONE) {
							if (totalPage >= pageIndex) {
								downloadData();
							} else {
								ShowMessage.showLast(context);
							}
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

			}
		});
	}

	private void setDataList(List<DynamicObj> list) {
		fba.addItems(list);

		if (dataListRefresh.isRefreshing()) {
			dataListRefresh.setRefreshing(false);
		}
	}

	private void downloadData() {
		UPLOAD = false;
		progress.setVisibility(View.VISIBLE);
		String url = UrlHandle.getMmPost()
				+ "?sort=-createAt"
				+ "&query="
				+ JsonHandle.getHttpJsonToString(new String[] { "type",
				"mmArea" }, new String[] { "topic", mmArea }) + "&p="
				+ pageIndex + "&l=" + pageSize + "&user_id="
				+ UserObjHandle.getUsetId(context);

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

						JSONObject json = JsonHandle.getJSON(
								JsonHandle.getJSON(result), "result");
						if (!ShowMessage.showException(context, json)) {
							JSONArray array = JsonHandle.getArray(json, "data");
							if (array != null) {
								List<DynamicObj> list = DynamicObjHandler
										.getDynamicObjList(array);
								setDataList(list);
								pageIndex += 1;
								totalPage = JsonHandle
										.getInt(json, "totalPage");
							}

						}
					}

				});

	}

}
