package com.zmyh.r.photo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.BitmapTool;
import com.zmyh.r.handler.CameraPicObjHandler;
import com.zmyh.r.handler.DateHandle;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.WeiXinHandler;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpFlieBox;
import com.zmyh.r.http.PostFile;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.PostFileCallback;
import com.zmyh.r.tool.AddPicDialog;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.view.InsideGridView;

public class ShareTroopActivity extends Activity {

	public final static String IS_FRIEND = "IS_FRIEND";

	private Context context;
	private Activity mActivity;
	private TroopObj mTroopObj;
	private boolean is_friend = false;

	@ViewInject(R.id.title_titleName)
	private TextView title;
	@ViewInject(R.id.share_troop_title)
	private EditText titleInput;
	@ViewInject(R.id.share_troop_content)
	private EditText contentInput;
	@ViewInject(R.id.share_troop_picGrid)
	private InsideGridView picGrid;
	@ViewInject(R.id.share_troop_shareBtn)
	private TextView shareBtn;
	@ViewInject(R.id.share_troop_progress)
	private ProgressBar progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_troop);
		ViewUtils.inject(this);
		context = this;
		mActivity = this;
		initActivity();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
				case AddPicDialog.CAMERA_IMAGE_REQUEST_CODE:
					String id = data.getExtras().getString(AddPicDialog.CAMERA_KEY);
					savePic(id);
					break;
				case AddPicDialog.IMAGE_REQUEST_CODE:
					Uri imgUri = data.getData();
					savePic(imgUri);
					break;
				case AddPicDialog.APP_IMAGE_REQUEST_CODE:
					List<String> list = data.getExtras().getStringArrayList(
							AddPicDialog.CHOICE_KEY);
					savePic(list);
					break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@OnClick({ R.id.title_back, R.id.share_troop_shareBtn })
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_back:
				finish();
				break;
			case R.id.share_troop_shareBtn:
				shareBtn();
				break;
		}
	}

	private void savePic(String id) {
		CameraPicObj obj;
		try {
			obj = CameraPicObjHandler.getCameraPicObj(context, id);
			mTroopObj.addChild(obj);
			initContent(mTroopObj);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	private void savePic(Uri imgUri) {
		if (imgUri != null) {
			try {
				ContentResolver cr = getContentResolver();
				InputStream imgIS = cr.openInputStream(imgUri);

				long createTime = DateHandle.getTime();

				CameraPicObj obj = new CameraPicObj();
				obj.setId(createTime);
				obj.setCreateAt(createTime);
				CameraPicObjHandler.saveCameraPicObj(context, obj);

				FileUtil.saveImage(imgIS, obj.getMediumFilePath());

				mTroopObj.addChild(obj);
				initContent(mTroopObj);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private void savePic(List<String> list) {
		for (String id : list) {
			CameraPicObj obj;
			try {
				obj = CameraPicObjHandler.getCameraPicObj(context, id);
				try {
					CameraPicObj newObj = CameraPicObj.copy(obj);
					mTroopObj.addChild(newObj);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (DbException e1) {
				e1.printStackTrace();
			}

		}
		initContent(mTroopObj);
	}

	private void initActivity() {
		title.setText("编辑内容");
		mTroopObj = TroopObjBox.getSaveTroopObj();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			is_friend = b.getBoolean(IS_FRIEND);
		}
		if (is_friend) {
			shareBtn.setText("分享到微信朋友圈");
		} else {
			shareBtn.setText("分享到微信");
		}
		if (mTroopObj == null) {
			finish();
		} else {
			initContent(mTroopObj);
		}
	}

	private void initContent(TroopObj obj) {
		contentInput.setText(obj.getContent());
		picGrid.setAdapter(new PicBaseAdapter(obj.getCameraPicObjList()));
	}

	private List<String> getSharePicPaths() {
		List<String> list = new ArrayList<String>();
		for (CameraPicObj obj : mTroopObj.getCameraPicObjList()) {
			list.add(obj.getMediumFilePath());
		}
		return list;
	}

	private void shareFriend(String shareUrl) {
		WeiXinHandler.shareFriend(context, titleInput.getText().toString(), "",
				shareUrl);
	}

	private void shareWeiXin(String shareUrl) {
		WeiXinHandler.shareWeiXin(context, titleInput.getText().toString(), "",
				shareUrl);
	}

	private void shareBtn() {
		progress.setVisibility(View.VISIBLE);
		String url = UrlHandle.getMmShare();
		HttpFlieBox box = new HttpFlieBox();
		box.addParams("content", contentInput.getText().toString());
		box.addFileList("pic", getSharePicPaths());
		PostFile.getInstance().post(url, box, new PostFileCallback() {

			@Override
			public void callback(String result) {
				progress.setVisibility(View.GONE);
				Log.d("", result);
				JSONObject json = JsonHandle.getJSON(result);
				if (!ShowMessage.showException(context, json)) {
					if (is_friend) {
						shareFriend(JsonHandle.getString(json, "shareUrl"));
					} else {
						shareWeiXin(JsonHandle.getString(json, "shareUrl"));
					}
				}
			}

			@Override
			public void onFailure(Exception exception) {
				progress.setVisibility(View.GONE);
				ShowMessage.showFailure(context);
			}
		});
	}

	class PicBaseAdapter extends BaseAdapter {

		List<CameraPicObj> cameraPicObjList;
		LayoutInflater inflater;

		public PicBaseAdapter(List<CameraPicObj> cameraPicObjList) {
			this.cameraPicObjList = cameraPicObjList;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			if (cameraPicObjList.size() < 9) {
				return cameraPicObjList.size() + 1;
			}
			return cameraPicObjList.size();
		}

		@Override
		public Object getItem(int position) {
			return cameraPicObjList.get(position);
		}

		@Override
		public long getItemId(int convertView) {
			return convertView;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.compile_grid_item, null);
			}
			try {
				CameraPicObj obj = cameraPicObjList.get(position);
				setImageMessage(convertView, obj, position);
				setViewContent(convertView, obj);
			} catch (Exception e) {
				setViewContent(convertView);
				setOnAddPic(convertView);
			}
			return convertView;
		}

		private void setViewContent(View view, CameraPicObj obj) {
			view.findViewById(R.id.compile_deletePic).setVisibility(
					View.INVISIBLE);
		}

		private ArrayList<String> getPicList() {
			ArrayList<String> list = new ArrayList<String>();
			for (CameraPicObj obj : cameraPicObjList) {
				list.add(obj.getMediumFilePath());
			}
			return list;
		}

		private void setImageMessage(View view, CameraPicObj obj,
									 final int position) {
			ImageView pic = (ImageView) view.findViewById(R.id.compile_pic);
			final String path = obj.getMediumFilePath();

			int w = WinTool.getWinWidth(context) / 3
					- WinTool.dipToPx(context, 20);
			pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
			DownloadImageLoader.loadImageForFile(pic, path);

			pic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle b = new Bundle();
					b.putStringArrayList("iamge_list", getPicList());
					b.putInt("position", position);
					Passageway
							.jumpActivity(context, ImageListAcitvity.class, b);
				}
			});
		}

		private void setViewContent(View view) {
			ImageView pic = (ImageView) view.findViewById(R.id.compile_pic);

			int w = WinTool.getWinWidth(context) / 3
					- WinTool.dipToPx(context, 23);
			pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
			pic.setImageResource(R.drawable.add_pic_icon);

			view.findViewById(R.id.compile_deletePic).setVisibility(
					View.INVISIBLE);
		}

		private void setOnAddPic(View view) {
			ImageView pic = (ImageView) view.findViewById(R.id.compile_pic);
			pic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AddPicDialog.showAddPicDialog(mActivity,
							cameraPicObjList.size());
				}
			});
		}

	}

}