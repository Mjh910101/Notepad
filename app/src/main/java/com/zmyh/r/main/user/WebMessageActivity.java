package com.zmyh.r.main.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;

public class WebMessageActivity extends Activity {

	private Context context;

	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.webMessage_content)
	private WebView content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_message);
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
		titleName.setText("");
		Bundle b = getIntent().getExtras();
		if (b != null) {
			titleName.setText(b.getString("title"));
			loadUrl(b.getString("url"));
		}
	}

	private void loadUrl(String url) {
		content.getSettings().setJavaScriptEnabled(true);
		content.loadUrl(url);
	}

}
