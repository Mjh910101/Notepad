package com.zmyh.r.main;

import android.content.Context;
import android.widget.LinearLayout;

import com.zmyh.r.box.CommentObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.DateHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.view.CommentMessage;

public class CommentMessageView extends CommentMessage {

	private CommentObj obj;

	public CommentMessageView(Context context, CommentObj obj) {
		super(context);
		this.obj = obj;
		setMessageContent();
	}

	@Override
	public void setUserPic() {
		int w = WinTool.getWinWidth(context) / 10;
		userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		DownloadImageLoader.loadImage(userPic, obj.getUsetPic(), w / 2);

	}

	@Override
	public void setUserName() {
		userName.setText(obj.getUserName());
	}

	@Override
	public void setUserContent() {
		content.setText(obj.getComment());
	}

	@Override
	public void setTime() {
		time.setText(DateHandle.getIsTodayFormat(obj.getCreateAt() * 1000));
	}

}
