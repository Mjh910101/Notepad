package com.zmyh.r.main.server;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AbsListView.OnScrollListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zmyh.r.R;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.ShowMessage;

public class ServerFrameLayout extends Fragment {

	private Context context;
	private int pageIndex = 1, totalPage = 1;;

	@ViewInject(R.id.server_dataList)
	private ListView dataList;
	@ViewInject(R.id.server_progress)
	private ProgressBar progress;

	private ServerBaseAdapter sba;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		View contactsLayout = inflater.inflate(R.layout.layout_server,
				container, false);
		ViewUtils.inject(this, contactsLayout);
		setHeadView();
		downloadData();
		setDataListScrollListener();
		return contactsLayout;
	}

	private void setHeadView() {
		sba = new ServerBaseAdapter(context);
		dataList.addHeaderView(new ServerHeadView(context, progress));
		dataList.setAdapter(sba);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void setDataListScrollListener() {
		dataList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
						if (progress.getVisibility() == View.GONE) {
							if (totalPage > pageIndex) {
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

	private void setDataList(List<ServerObj> list) {
		if (sba == null) {
			sba = new ServerBaseAdapter(context, list);
			dataList.setAdapter(sba);
		} else {
			sba.addItems(list);
		}
	}

	private void downloadData() {
		progress.setVisibility(View.VISIBLE);

		String url = UrlHandle.getMmPost()
				+ "?query="
				+ JsonHandle.getHttpJsonToString(new String[] { "hot" },
						new String[] { "1" }) + "&p=" + pageIndex
				+ "&sort=-createAt";
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
							List<ServerObj> list = ServerObjHandler
									.getServerObjList(array);
							setDataList(list);
							pageIndex += 1;
							totalPage = JsonHandle.getInt(json, "totalPage");
						}
					}

				});
	}

}
