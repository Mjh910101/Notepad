package com.zmyh.r.photo.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.CameraPicObjHandler;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class OnlinePicBaseAdapter extends PicBaseAdapter {

	private static ExecutorService executorService;

	public OnlinePicBaseAdapter(Context context,
								List<CameraPicObj> cameraPicObjList) {
		super(context);
		dataList = cameraPicObjList;
		executorService = Executors.newFixedThreadPool(3);
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.mnt_image_list_item, null);
		}

		CameraPicObj obj = dataList.get(position);
		setViewContent(convertView, obj);
		setImageMessage(convertView, obj, position);
		setOnChoice(convertView, obj);
		setOnDownload(convertView, obj, position);

		return convertView;
	}

	private void setOnDownload(View view, final CameraPicObj obj, int position) {
		final ImageView uploadIcon = (ImageView) view
				.findViewById(R.id.image_item_upload);
		final TextView uploadText = (TextView) view
				.findViewById(R.id.image_item_uploadText);

		uploadIcon.setImageResource(R.drawable.download_o_icon);

		if (FileUtil.fileIsExists(obj.getOriginalFilePath())) {
			uploadText.setText("打开");
			uploadIcon.setVisibility(View.GONE);
			uploadText.setVisibility(View.VISIBLE);
		} else {
			if (obj.getState() == CameraPicObj.WAIT) {
				uploadText.setText("1%");
				uploadIcon.setVisibility(View.GONE);
				uploadText.setVisibility(View.VISIBLE);
			} else {
				uploadIcon.setVisibility(View.VISIBLE);
				uploadText.setVisibility(View.GONE);
			}
		}

		uploadIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				uploadIcon.setVisibility(View.GONE);
				uploadText.setVisibility(View.VISIBLE);
				uploadText.setText("1%");
				downloadPic(obj);
			}
		});

		uploadText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (FileUtil.fileIsExists(obj.getOriginalFilePath())) {
					seePic(obj);
				}
			}

		});
	}

	public void downloadPic(final CameraPicObj obj) {
		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
//					if (!FileUtil.fileIsExists(obj.getOriginalFilePath())) {
						FileUtil.saveOriginalBitmap(obj.getMu_photo_original(),
								obj);
						obj.setMax_state(CameraPicObj.FINISH);
						obj.setShow_max_pic(true);
						CameraPicObjHandler.saveCameraPicObj(context, obj);
//					}else{
//						Message.obtain(h,10086).sendToTarget();
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				Message.obtain(h).sendToTarget();
			}
		});
	}

	private void setViewContent(View view, CameraPicObj obj) {
		TextView tapName = (TextView) view
				.findViewById(R.id.image_item_tapName);

		tapName.setText(obj.getCreateTime());
	}

	private void setImageMessage(View view, final CameraPicObj obj,
								 final int position) {
		ImageView pic = (ImageView) view.findViewById(R.id.image_item_pic);
		final String url = obj.getMu_photo_thumbnail();

		int w = WinTool.getWinWidth(context) / 4;
		pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		DownloadImageLoader.loadImage(pic, url, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
										FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String imageUri, View view,
										  Bitmap loadedImage) {
				if (loadedImage != null) {
					if (loadedImage.getWidth() > loadedImage.getHeight()) {
						view.setRotation(90);
					}
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});

		pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				seePic(position);
			}
		});
	}

	private void setOnChoice(View view, final CameraPicObj obj) {
		ImageView onClickIcon = (ImageView) view
				.findViewById(R.id.image_item_onClick);

		if (choiceList.contains(obj)) {
			onClickIcon.setImageResource(R.drawable.on_click);
		} else {
			onClickIcon.setImageResource(R.drawable.on_click_off);
		}

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				addChoiceList(obj);
				notifyDataSetChanged();
			}
		});
	}

	private Handler h = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 10086:
					ShowMessage.showToast(context,"已下载数据将不做处理");
					break;
				default:
					notifyDataSetChanged();
					break;
			}
		}

	};

	private void seePic(CameraPicObj obj) {
		ArrayList<String> list = new ArrayList<String>();
		list.add(obj.getOriginalFilePath());
		Bundle b = new Bundle();
		b.putStringArrayList("iamge_list", list);
		b.putInt("position", 0);
		Passageway.jumpActivity(context, ImageListAcitvity.class, b);
	}

	private void seePic(int position) {
		Bundle b = new Bundle();
		b.putStringArrayList("iamge_list", getPicList());
		b.putInt("position", position);
		b.putBoolean("isOnline", true);
		b.putBoolean("isRotate", true);
		Passageway.jumpActivity(context, ImageListAcitvity.class, b);
	}

	private ArrayList<String> getPicList() {
		ArrayList<String> list = new ArrayList<String>();
		for (CameraPicObj obj : dataList) {
			list.add(obj.getMu_photo_original());
		}
		return list;
	}

}
