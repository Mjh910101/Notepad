package com.zmyh.r.main.server;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.forum.ForumFrameLayout;
import com.zmyh.r.tool.ShowMessage;

public class ReportActivity extends Activity {

	private final static int FALSE = 1;
	private final static int VIOLATE = 2;
	private final static int TIME_OUT = 3;

	private Context context;
	private String post_id;
	private int now_tap;

	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.title_seek)
	private ImageView seekIcon;
	@ViewInject(R.id.report_falseText)
	private TextView falseText;
	@ViewInject(R.id.report_violateText)
	private TextView violateText;
	@ViewInject(R.id.report_timeOutText)
	private TextView timeOutText;
	@ViewInject(R.id.report_progress)
	private ProgressBar progress;
	@ViewInject(R.id.report_contentInput)
	private EditText contentInput;
	@ViewInject(R.id.report_telInput)
	private EditText telInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		ViewUtils.inject(this);
		context = this;

		initActivity();
	}

	@OnClick({ R.id.title_back, R.id.report_falseText, R.id.report_violateText,
			R.id.report_timeOutText, R.id.report_submitBtn })
	public void onCilik(View v) {
		switch (v.getId()) {
			case R.id.title_back:
				finish();
				break;
			case R.id.report_falseText:
				setReportTap(FALSE);
				break;
			case R.id.report_violateText:
				setReportTap(VIOLATE);
				break;
			case R.id.report_timeOutText:
				setReportTap(TIME_OUT);
				break;
			case R.id.report_submitBtn:
				submit();
				break;
		}
	}

	private void initActivity() {
		titleName.setText("举报");
		seekIcon.setVisibility(View.GONE);
		setReportTap(FALSE);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			post_id = b.getString("id");
		}
	}

	private void setReportTap(int i) {
		now_tap = i;
		initReportTap();
		switch (i) {
			case FALSE:
				falseText
						.setTextColor(ColorBox.getColorForID(context, R.color.red));
				falseText.setBackgroundResource(R.drawable.red_onclick_off_botton);
				break;
			case VIOLATE:
				violateText.setTextColor(ColorBox.getColorForID(context,
						R.color.red));
				violateText
						.setBackgroundResource(R.drawable.red_onclick_off_botton);
				break;
			case TIME_OUT:
				timeOutText.setTextColor(ColorBox.getColorForID(context,
						R.color.red));
				timeOutText
						.setBackgroundResource(R.drawable.red_onclick_off_botton);
				break;
		}
	}

	private void initReportTap() {
		falseText.setTextColor(ColorBox.getColorForID(context, R.color.white));
		falseText.setBackgroundResource(R.drawable.red_onclick_on_botton);

		violateText
				.setTextColor(ColorBox.getColorForID(context, R.color.white));
		violateText.setBackgroundResource(R.drawable.red_onclick_on_botton);

		timeOutText
				.setTextColor(ColorBox.getColorForID(context, R.color.white));
		timeOutText.setBackgroundResource(R.drawable.red_onclick_on_botton);
	}

	private void submit() {
		String tel = telInput.getText().toString();
		String content = contentInput.getText().toString();
		if (!tel.equals("")) {
			submit(tel, content);
		} else {
			ShowMessage.showToast(context, "请先填写联系方式!~");
		}
	}

	private void submit(String tel, String content) {
		progress.setVisibility(View.VISIBLE);

		String url = UrlHandle.getMmReport();

		RequestParams params = HttpUtilsBox.getRequestParams(context);

		params.addBodyParameter("post_id ", post_id);
		params.addBodyParameter("phone", tel);
		params.addBodyParameter("type", String.valueOf(now_tap));
		params.addBodyParameter("content", content);

		HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
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
							int r = JsonHandle.getInt(json, "ok");
							if (r == 1) {
								ForumFrameLayout.UPLOAD = true;
								ShowMessage.showToast(context, "发送成功");
								finish();
							}
						}

					}

				});
	}
}
