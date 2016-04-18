package com.zmyh.r.baidumap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zmyh.r.R;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.WinTool;

public class MapImageView extends LinearLayout {

	private Context context;
	private LayoutInflater inflater;
	private ImageView pic;
	private View view;

	public MapImageView(Context context, String url) {
		super(context);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.map_img_view, null);

		pic = ((ImageView) view.findViewById(R.id.image_pic));
		int w = WinTool.getWinWidth(context) / 3;
		pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		DownloadImageLoader.loadImage(pic, url, 15);
		addView(view);
	}
}
