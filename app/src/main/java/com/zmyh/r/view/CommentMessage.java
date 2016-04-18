package com.zmyh.r.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.UserObj;

public abstract class CommentMessage extends LinearLayout {

	private LayoutInflater inflater;
	public Context context;
	private View acitvity;

	public ImageView userPic;
	public TextView userName, time, content;

	public CommentMessage(Context context) {
		super(context);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		acitvity = inflater.inflate(R.layout.comment_list_item, null);
		initView(acitvity);
		addView(acitvity);
	}

	private void initView(View v) {
		userName = (TextView) v.findViewById(R.id.comment_list_userName);
		time = (TextView) v.findViewById(R.id.comment_list_time);
		content = (TextView) v.findViewById(R.id.comment_list_content);
		userPic = (ImageView) v.findViewById(R.id.comment_list_userPic);
	}

	public void setMessageContent() {
		setUserPic();
		setUserName();
		setUserContent();
		setTime();
	}

	public abstract void setUserPic();

	public abstract void setUserName();

	public abstract void setUserContent();

	public abstract void setTime();

}
