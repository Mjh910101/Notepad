package com.zmyh.r.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.UserObj;

public abstract class UserMessage extends LinearLayout {

	private LayoutInflater inflater;
	public Context context;
	private View acitvity;

	public ImageView userPic;
	public TextView userName;

	public UserMessage(Context context) {
		super(context);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		acitvity = inflater.inflate(R.layout.user_list_item, null);

		intiView(acitvity);
		addView(acitvity);
	}

	private void intiView(View v) {
		userName = (TextView) v.findViewById(R.id.uesr_list_userName);
		userPic = (ImageView) v.findViewById(R.id.uesr_list_userPic);
	}

	public void setMessageContent() {
		setUserPic();
		setUserName();
	}

	public abstract void setUserPic();

	public abstract void setUserName();

}
