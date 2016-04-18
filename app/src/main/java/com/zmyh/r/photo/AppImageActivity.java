package com.zmyh.r.photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.dao.DBHandler;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.CameraPicObjHandler;
import com.zmyh.r.handler.SystemHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.tool.AddPicDialog;

public class AppImageActivity extends Activity {

	private Context context;

	private ArrayList<String> choiceImageList;

	private int size;

	@ViewInject(R.id.title_seek)
	private ImageView seekIcon;
	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.title_next)
	private TextView next;
	@ViewInject(R.id.appImage_dataGrid)
	private GridView dataGrid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appimage);
		ViewUtils.inject(this);
		context = this;
		initActivity();
	}

	@OnClick({ R.id.title_back, R.id.title_next })
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_back:
				finish();
				break;
			case R.id.title_next:
				close();
				break;
		}
	}

	private void close() {
		Intent i = new Intent();
		Bundle b = new Bundle();
		b.putStringArrayList(AddPicDialog.CHOICE_KEY, choiceImageList);
		i.putExtras(b);
		setResult(AddPicDialog.APP_IMAGE_REQUEST_CODE, i);
		finish();
	}

	private void initActivity() {
		titleName.setText("选择图片");
		seekIcon.setVisibility(View.INVISIBLE);
		next.setVisibility(View.VISIBLE);

		choiceImageList = new ArrayList<String>();
		dataGrid.setAdapter(new GridBaseAdapter(initFileData(new File(FileUtil
				.getMediumPath()))));

		Bundle b = getIntent().getExtras();
		if (b != null) {
			size = b.getInt("size");
			setNextSize();
		}

	}

	private void setNextSize() {
		next.setText("(" + (size + choiceImageList.size()) + "/9)下一步");
	}

	private List<CameraPicObj> initFileData(File file) {
		List<CameraPicObj> list = CameraPicObjHandler
				.getCameraPicObjList(DBHandler.getDbUtils(context));
		return list;
	}

	private void showMaxDialog() {
		MessageDialog dialog = new MessageDialog(context);
		dialog.setMessage("一组最多9张");
		dialog.setCommitStyle("知道了");
		dialog.setCommitListener(null);
	}

	class GridBaseAdapter extends BaseAdapter {

		private List<CameraPicObj> list;
		private LayoutInflater inflater;

		public GridBaseAdapter(List<CameraPicObj> list) {
			this.list = list;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			setNextSize();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.appimage_item, null);
			}

			CameraPicObj obj = list.get(position);
			setViewContent(convertView, obj);
			setIsChoice(convertView, obj);
			setOnClickImage(convertView, obj);

			return convertView;
		}

		private void setOnClickImage(View view, final CameraPicObj obj) {
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String id = obj.getId();
					Log.e("", id);
					if (choiceImageList.contains(id)) {
						choiceImageList.remove(id);
					} else {
						if (size + choiceImageList.size() < 9) {
							choiceImageList.add(id);
						} else {
							showMaxDialog();
						}

					}
					notifyDataSetChanged();
				}

			});
		}

		private boolean setIsChoice(View view, CameraPicObj obj) {
			ImageView choicePic = (ImageView) view
					.findViewById(R.id.appImage_choicePic);

			for (String id : choiceImageList) {
				if (id.equals(obj.getId())) {
					choicePic.setVisibility(View.VISIBLE);
					return true;
				}
			}

			choicePic.setVisibility(View.INVISIBLE);
			return false;

		}

		private void setViewContent(View view, CameraPicObj obj) {
			ImageView pic = (ImageView) view.findViewById(R.id.appImage_pic);

			final String path = obj.getMediumFilePath();

			int w = WinTool.getWinWidth(context) / 3
					- WinTool.dipToPx(context, 7);
			pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
			DownloadImageLoader.loadImageForFile(pic, path);

		}
	}
}
