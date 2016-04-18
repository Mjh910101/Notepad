package com.zmyh.r.tool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.zmyh.r.camera.CameraActivity;
import com.zmyh.r.dailog.ListDialog;
import com.zmyh.r.photo.AppImageActivity;

public class AddPicDialog {

	public static final int CAMERA_IMAGE_REQUEST_CODE = 10000;
	public static final int IMAGE_REQUEST_CODE = 10001;
	public static final int APP_IMAGE_REQUEST_CODE = 10002;

	public static final String GET_ONR_KEY = "GET_ONE";
	public static final String NOT_SAVE_KEY = "NOT_MAX_KEY";
	public final static String CHOICE_KEY = "ChoiceImageList";
	public final static String CAMERA_KEY = "ChoiceImage";

	public static void showAddPicDialog(final Activity a, final int size) {
		final ListDialog dialog = new ListDialog(a);
		dialog.setTitleGone();
		String[] strs = null;
		if (size < 0) {
			strs = new String[] { "拍照", "手机相册" };
		} else {
			strs = new String[] { "拍照", "手机相册", "应用相册" };
		}
		dialog.setList(strs);
		dialog.setItemListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				switch (position) {
					case 0:
						Bundle b = new Bundle();
						b.putBoolean(GET_ONR_KEY, true);
						if (size < 0) {
							b.putBoolean(NOT_SAVE_KEY, true);
						}
						Passageway.jumpActivity(a, CameraActivity.class,
								CAMERA_IMAGE_REQUEST_CODE, b);
						break;
					case 1:
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						intent.setType("image/*");
						intent.putExtra("return-data", true);
						a.startActivityForResult(intent, IMAGE_REQUEST_CODE);
						break;
					case 2:
						Bundle bundle = new Bundle();
						bundle.putInt("size", size);
						Passageway.jumpActivity(a, AppImageActivity.class,
								APP_IMAGE_REQUEST_CODE, bundle);
						break;
				}
				dialog.dismiss();
			}
		});

	}
}
