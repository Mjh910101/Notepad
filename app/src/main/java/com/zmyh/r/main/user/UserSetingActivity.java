package com.zmyh.r.main.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.tool.Passageway;

public class UserSetingActivity extends Activity {

	private Context context;

	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.title_seek)
	private ImageView seekIcon;
	@ViewInject(R.id.user_seting_pic)
	private ImageView userPic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_uset_seting);
		ViewUtils.inject(this);
		context = this;

		initActivity();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		setUserPic();
	}

	@OnClick({ R.id.title_back, R.id.user_seting_rewamp,
			R.id.user_seting_picBox })
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_back:
				finish();
				break;
			case R.id.user_seting_rewamp:
				Passageway.jumpActivity(context, RevampActivity.class);
				break;
			case R.id.user_seting_picBox:
				Passageway.jumpActivity(context, PicCompileActivity.class);
				break;
		}
	}

	private void initActivity() {
		titleName.setText("编辑个人资料");
		seekIcon.setVisibility(View.GONE);
		setUserPic();
	}

	private void setUserPic() {
		int w = WinTool.dipToPx(context, 40);
		userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		DownloadImageLoader.loadImage(userPic,
				UserObjHandle.getM_avatar(context), w / 2);
	}
}
