package com.zmyh.r.camera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.interfaces.CallbackForBoolean;

public class MinPicView extends LinearLayout {

	private Context context;
	private CameraPicObj obj;
	private LayoutInflater inflater;
	private View view;

	private CallbackForBoolean callback;

	public MinPicView(Context context, CameraPicObj obj) {
		super(context);
		this.context = context;
		this.obj = obj;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.min_pic_view, null);

		int w = WinTool.getWinWidth(context) / 4;
		view.setLayoutParams(new LinearLayout.LayoutParams(w, w));

		initView(view);

		addView(view);
	}

	private void initView(View view) {
		ImageView pic = (ImageView) view.findViewById(R.id.minPix_pic);
		ImageView delete = (ImageView) view.findViewById(R.id.minPix_deletePic);
		pic.setScaleType(ScaleType.CENTER_CROP);
		DownloadImageLoader.loadImageForFile(pic, obj.getMediumFilePath());

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (callback != null) {
					callback.callback(true);
				}
			}
		});

		pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (callback != null) {
					callback.callback(false);
				}
			}
		});
	}

	public void setCallbackListener(CallbackForBoolean callback) {
		this.callback = callback;
	}

}
