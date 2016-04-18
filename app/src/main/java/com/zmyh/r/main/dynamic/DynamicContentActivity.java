package com.zmyh.r.main.dynamic;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.ImageView;
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
import com.zmyh.r.handler.DynamicObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.view.LazyWebView;
import com.zmyh.r.view.VestrewWebView;

public class DynamicContentActivity extends Activity {

	private Context context;

	private DynamicObj mDynamicObj;

	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.title_seek)
	private ImageView seekIcon;
	@ViewInject(R.id.dynamic_content_progress)
	private ProgressBar progress;
	@ViewInject(R.id.dynamic_content_title)
	private TextView title;
	@ViewInject(R.id.dynamic_content_time)
	private TextView time;
	@ViewInject(R.id.dynamic_content_city)
	private TextView city;
	@ViewInject(R.id.dynamic_content_contentWeb)
	private LazyWebView contextWeb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dynamic_content);
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
		titleName.setText("行业动态详细");
		seekIcon.setVisibility(View.GONE);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			downloadData(b.getString("id"));
		}
	}

	private void setActivityContent(DynamicObj obj) {
		title.setText(obj.getTitle());
		time.setText(obj.getCreateTime());
		city.setText(obj.getCity());
		setContextWeb(obj);
	}

	private void setContextWeb(DynamicObj obj) {
		Log.e("", obj.getContent());
		Log.e("", Html.fromHtml(obj.getContent()).toString());
		// String contentStr = VestrewWebView.addJavaScript(Html.fromHtml(
		// obj.getContent()).toString());
		String contentStr = VestrewWebView.addJavaScript(obj.getContent());
		// String contentStr = VestrewWebView.addJavaScript(obj.getContent());
		contextWeb.getSettings().setJavaScriptEnabled(true);
		contextWeb.addJavascriptInterface(this, "ImageOnClick");
		contextWeb.setWebChromeClient(new WebChromeClient());
		contextWeb.setFocusable(false);
		contextWeb.loadData(contentStr);
	}

	private void downloadData(String id) {
		progress.setVisibility(View.VISIBLE);
		String url = UrlHandle.getMmPost(id) + "?user_id="
				+ UserObjHandle.getUsetId(context);

		RequestParams params = HttpUtilsBox.getRequestParams(context);

		HttpUtilsBox.getHttpUtil().send(HttpMethod.PUT, url, params,
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
							JSONObject dynamicJson = JsonHandle.getJSON(json,
									"o");
							if (dynamicJson != null) {
								mDynamicObj = DynamicObjHandler
										.getDynamicObj(dynamicJson);
								setActivityContent(mDynamicObj);
							}
						}
					}

				});
	}

}
