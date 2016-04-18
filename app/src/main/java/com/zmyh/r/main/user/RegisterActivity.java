package com.zmyh.r.main.user;

import org.json.JSONObject;

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

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.UserObj;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.ShowMessage;

public class RegisterActivity extends Activity {

	private Context context;
	private boolean isCorrect = false;

	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.title_seek)
	private ImageView seekIcon;
	@ViewInject(R.id.register_telInput)
	private EditText telInput;
	@ViewInject(R.id.register_passageInput)
	private EditText passwordInput;
	@ViewInject(R.id.register_passageAgainInput)
	private EditText passwordAgainInput;
	@ViewInject(R.id.register_passageAgainIcon)
	private ImageView passwordJudge;
	@ViewInject(R.id.register_progress)
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ViewUtils.inject(this);
		context = this;
		initActivity();
		setTextChangedListener();
	}

	@OnClick({ R.id.title_back, R.id.register_commit })
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_back:
				finish();
				break;
			case R.id.register_commit:
				commit();
				break;
		}
	}

	private void initActivity() {
		titleName.setText("注册");
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

			String url = UrlHandle.getRegister();

			RequestParams params = HttpUtilsBox.getRequestParams(context);
			params.addBodyParameter("m_mobile", telInput.getText().toString());
			params.addBodyParameter("m_password", passwordInput.getText()
					.toString());

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
								UserObj user = UserObjHandle
										.getUserBox(JsonHandle.getJSON(json,
												"auth"));
								UserObjHandle.savaUser(context, user);
								finish();
							}
						}
					});
		}
	}
}
