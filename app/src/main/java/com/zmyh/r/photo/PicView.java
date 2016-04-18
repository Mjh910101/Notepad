package com.zmyh.r.photo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.tool.AddPicDialog;
import com.zmyh.r.tool.Passageway;

public class PicView extends LinearLayout {

	private Activity mActivity;
	private Context context;
	private LayoutInflater inflater;
	private View view;
	private TroopObj mTroopObj;
	private CallbackForBoolean callback;

	public PicView(Activity mActivity, TroopObj mTroopObj, CameraPicObj obj) {
		super(mActivity);
		this.mActivity = mActivity;
		this.context = mActivity;
		this.mTroopObj = mTroopObj;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.compile_grid_item, null);
		if (obj != null) {
			setImageMessage(obj);
			setOnDeletePic(obj);
		} else {
			setViewContent();
			setOnAddPic();
		}
		addView(view);
	}

	public void setListener(CallbackForBoolean callback) {
		this.callback = callback;
	}

	public void goneDelete() {
		view.findViewById(R.id.compile_deletePic).setVisibility(View.GONE);
	}

	public void setRemark(CameraPicObj obj) {
		TextView remark = (TextView) view.findViewById(R.id.compile_remarkMsg);
		// String r = obj.getRemark();
		// if (!r.equals("")) {
		// remark.setVisibility(View.VISIBLE);
		// remark.setText(r);
		// }
		remark.setVisibility(View.GONE);

	}

	private void setOnAddPic() {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddPicDialog.showAddPicDialog(mActivity, mTroopObj
						.getCameraPicObjList().size());
			}
		});
	}

	private void setViewContent() {
		ImageView pic = (ImageView) view.findViewById(R.id.compile_pic);

		int w = WinTool.getWinWidth(context) / 3 - WinTool.dipToPx(context, 23);
		pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		pic.setImageResource(R.drawable.add_pic_icon);

		view.findViewById(R.id.compile_deletePic).setVisibility(View.INVISIBLE);
	}

	private ArrayList<String> getPicList() {
		ArrayList<String> list = new ArrayList<String>();
		for (CameraPicObj obj : mTroopObj.getCameraPicObjList()) {
			list.add(obj.getMediumFilePath());
		}
		return list;
	}

	private int getPosition(CameraPicObj obj) {
		List<CameraPicObj> list = mTroopObj.getCameraPicObjList();
		for (int i = 0; i < list.size(); i++) {
			if (obj.getId().equals(list.get(i).getId())) {
				return i;
			}
		}
		return 0;
	}

	private void setImageMessage(final CameraPicObj obj) {
		ImageView pic = (ImageView) view.findViewById(R.id.compile_pic);
		final String path = obj.getMediumFilePath();

		if (FileUtil.fileIsExists(path)) {
			DownloadImageLoader.loadImageForFile(pic, path);
		} else {
			DownloadImageLoader.loadImage(pic, obj.getMu_photo_thumbnail());
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
			}
		});
	}

	private void setOnDeletePic(final CameraPicObj obj) {
		view.findViewById(R.id.compile_deletePic).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						if (callback != null) {
							callback.callback(mTroopObj.getCameraPicObjList()
									.size() > 1);
						}

					}
				});
	}
}
