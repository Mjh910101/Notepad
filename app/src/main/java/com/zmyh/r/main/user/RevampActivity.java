package com.zmyh.r.main.user;

import org.json.JSONObject;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.ShowMessage;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RevampActivity extends Activity {

	private Context context;
	private boolean isCorrect = false;

	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.title_seek)
	private ImageView seekIcon;
	@ViewInject(R.id.revamp_oldPassageInput)
	private EditText oldPassageInput;
	@ViewInject(R.id.revamp_passageInput)
	private EditText passwordInput;
	@ViewInject(R.id.revamp_passageAgainInput)
	private EditText passwordAgainInput;
	@ViewInject(R.id.revamp_passageAgainIcon)
	private ImageView passwordJudge;
	@ViewInject(R.id.revamp_progress)
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_revamp);
		ViewUtils.inject(this);
		context = this;

		initActivity();
		setTextChangedListener();
	}

	@OnClick({ R.id.title_back, R.id.revamp_commit })
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_back:
				finish();
				break;
			case R.id.revamp_commit:
				commit();
				break;
		}
	}

	private void initActivity() {
		titleName.setText("重设密码");
		seekIcon.setVisibility(View.GONE);
	}

	private void setTextChangedListener() {
		passwordAgainInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
									  int arg3) {
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
										  int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				passwordJudge.setVisibility(View.VISIBLE);
				if (passwordInput.getText().toString()
						.equals(passwordAgainInput.getText().toString())) {
					passwordJudge.setImageResource(R.drawable.password_true);
					isCorrect = true;
				} else {
					passwordJudge.setImageResource(R.drawable.password_false);
					isCorrect = false;
				}
			}
		});
	}

	private void commit() {
		if (isCorrect) {
			progress.setVisibility(View.VISIBLE);

			String url = UrlHandle.getMmUser();

			RequestParams params = HttpUtilsBox.getRequestParams(context);
			params.addBodyParameter("newPwd", passwordInput.getText()
					.toString());
			params.addBodyParameter("oldPwd", oldPassageInput.getText()
					.toString());
			params.addBodyParameter("user_id", UserObjHandle.getUsetId(context));

			HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException exception,
											  String msg) {
							progress.setVisibility(View.GONE);
							ShowMessage.showFailure(context);
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							progress.setVisibility(View.GONE);
							String result = responseInfo.result;
							Log.d("", result);

							JSONObject json = JsonHandle.getJSON(result);
							if (json != null) {
								ShowMessage.showToast(context, "发送成功");
								finish();
							}
						}
					});
		}
	}

}
