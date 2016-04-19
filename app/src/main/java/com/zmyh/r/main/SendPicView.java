package com.zmyh.r.main;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.tool.AddPicDialog;

public class SendPicView extends LinearLayout {

	private Activity context;
	private LayoutInflater inflater;
	private View view;
	private CallbackForBoolean callback;

	public SendPicView(Activity context, CameraPicObj obj) {
		super(context);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.compile_grid_item, null);
		setImageMessage(obj);
		setOnDeletePic(obj);
		addView(view);

	}

	public SendPicView(Activity context, int id) {
		super(context);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.compile_grid_item, null);
		setViewContent(id);
		setOnAddPic();
		addView(view);
	}

	public void setListener(CallbackForBoolean callback) {
		this.callback = callback;
	}

	private void setOnAddPic() {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddPicDialog.showAddPicDialog(context, -1);
			}
		});
	}

	private void setViewContent(int id) {
		ImageView pic = (ImageView) view.findViewById(R.id.compile_pic);

		int w = WinTool.getWinWidth(context) / 3 - WinTool.dipToPx(context, 20);
		pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		pic.setScaleType(ScaleType.FIT_XY);
		pic.setImageResource(id);

		view.findViewById(R.id.compile_deletePic).setVisibility(View.INVISIBLE);
	}

	private void setImageMessage(final CameraPicObj obj) {
		final ImageView pic = (ImageView) view.findViewById(R.id.compile_pic);
		final String path = obj.getMediumFilePath();

		if (FileUtil.fileIsExists(path)) {
			DownloadImageLoader.loadImageForFile(pic, path);
		} else {
			pic.setImageResource(R.drawable.ic_launcher);
			DownloadImageLoader.loadImage(obj.getMu_photo_thumbnail(),
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {
						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, final Bitmap loadedImage) {
							FileUtil.saveMediumBitmap(loadedImage, obj);
							DownloadImageLoader.loadImageForFile(pic, path);
						}

						@Override
						public void onLoadingCancelled(String imageUri,
								View view) {
						}
					});
		}

		int w = WinTool.getWinWidth(context) / 3 - WinTool.dipToPx(context, 20);
		pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));

		pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ArrayList<String> picList = new ArrayList<String>();
				// picList.add(path);
				// Bundle b = new Bundle();
				// b.putStringArrayList("iamge_list", getPicList());
				// b.putInt("position", getPosition(obj));
				// Passageway.jumpActivity(context, ImageListAcitvity.class, b);
				if (callback != null) {
					callback.callback(false);
				}
			}
		});
	}

	private void setOnDeletePic(final CameraPicObj obj) {
		view.findViewById(R.id.compile_deletePic).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (callback != null) {
							callback.callback(true);
						}

					}
				});
	}

}
