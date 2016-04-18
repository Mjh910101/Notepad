package com.zmyh.r.main.server;

import java.util.List;

import org.json.JSONArray;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zmyh.r.R;
import com.zmyh.r.box.ChannelObj;
import com.zmyh.r.camera.CameraActivity;
import com.zmyh.r.dailog.ListDialog;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.ChannelObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.SandEmphasisActivity;
import com.zmyh.r.photo.PhotoActivity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.view.InsideGridView;

public class ServerHeadView extends LinearLayout implements OnClickListener {

	private LayoutInflater inflater;
	private Context context;
	private View acitvity;

	private ImageView photoPic;
	private TextView sendBtn;
	private View takePicBtn, photoBtn;
	private InsideGridView channelGrid;
	private ProgressBar progress;

	public ServerHeadView(Context context, ProgressBar progress) {
		super(context);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		acitvity = inflater.inflate(R.layout.server_head, null);
		this.progress = progress;
		initView(acitvity);
		initPhotoPic(acitvity);
		downloadChannel();
		setClickListener(this);
		addView(acitvity);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.server_head_takePic:
				Passageway.jumpActivity(context, CameraActivity.class);
				break;
			case R.id.server_head_photo:
				Passageway.jumpActivity(context, PhotoActivity.class);
				break;
			case R.id.server_head_sendBtn:
				showSendListDialog();
				break;
		}

	}

	private void showSendListDialog() {
		final ListDialog dialog = new ListDialog(context);
		dialog.setList(new String[] { "求购", "供应", "苗木大单", "清场" });
		dialog.setTitleGone();
		dialog.setItemListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int p,
									long arg3) {
				Bundle b = new Bundle();
				switch (p) {
					case 0:
						b.putString(SandEmphasisActivity.MM_CHANNEL, "00001");
						b.putBoolean(SandEmphasisActivity.IS_EMPHASIS, false);
						break;
					case 1:
						b.putString(SandEmphasisActivity.MM_CHANNEL, "00002");
						b.putBoolean(SandEmphasisActivity.IS_EMPHASIS, true);
						break;
					case 2:
						b.putString(SandEmphasisActivity.MM_CHANNEL, "00003");
						b.putBoolean(SandEmphasisActivity.IS_EMPHASIS, false);
						break;
					case 3:
						b.putString(SandEmphasisActivity.MM_CHANNEL, "00004");
						b.putBoolean(SandEmphasisActivity.IS_EMPHASIS, false);
						break;

				}
				Passageway.jumpActivity(context, SandEmphasisActivity.class, b);
				dialog.dismiss();
			}
		});
	}

	private void initView(View v) {
		takePicBtn = v.findViewById(R.id.server_head_takePic);
		photoBtn = v.findViewById(R.id.server_head_photo);
		channelGrid = (InsideGridView) v
				.findViewById(R.id.server_head_channelGrid);
		sendBtn = (TextView) v.findViewById(R.id.server_head_sendBtn);
	}

	private void initPhotoPic(View v) {
		photoPic = (ImageView) v.findViewById(R.id.server_head_photoPic);
		double w = WinTool.getWinWidth(context);
		double h = w / 108 * 35;
		photoPic.setLayoutParams(new RelativeLayout.LayoutParams((int) w,
				(int) h));
		photoPic.setImageResource(R.drawable.server_photo_bg);
	}

	private void setClickListener(OnClickListener l) {
		takePicBtn.setOnClickListener(l);
		photoBtn.setOnClickListener(l);
		sendBtn.setOnClickListener(l);
	}

	private void setChannelList(List<ChannelObj> list) {
		channelGrid.setAdapter(new ChannelBaseAdapter(list));
	}

	private void downloadChannel() {
		progress.setVisibility(View.VISIBLE);
		String url = UrlHandle.getChannel();

		RequestParams params = HttpUtilsBox.getRequestParams(context);

		HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException exception, String msg) {
						progress.setVisibility(View.GONE);
						ShowMessage.showFailure(context);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						progress.setVisibility(View.GONE);
						String result = responseInfo.result;
						Log.d("", result);

						JSONArray array = JsonHandle.getArray(
								JsonHandle.getJSON(result), "result");
						if (array != null) {
							List<ChannelObj> list = ChannelObjHandler
									.getChannelObjList(array);
							setChannelList(list);
						}
					}

				});

	}

	class ChannelBaseAdapter extends BaseAdapter {

		final static int OPEN = 1;
		final static int COLSE = 2;

		List<ChannelObj> list;
		LayoutInflater inflater;
		int state = 0;

		public ChannelBaseAdapter(List<ChannelObj> list) {
			this.list = list;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (list.size() > 8) {
				state = COLSE;
			}
		}

		@Override
		public int getCount() {
			if (state == COLSE) {
				return 8;
			}
			if (state == OPEN) {
				return list.size() + 1;
			}
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
				convertView = inflater
						.inflate(R.layout.channel_grid_item, null);
			}

			if (state == COLSE && position == 7) {
				setContent(convertView);
				setOnClick(convertView);
			} else if (state == OPEN && position == list.size()) {
				setContent(convertView);
				setOnClick(convertView);
			} else {
				ChannelObj obj = list.get(position);
				setContent(convertView, obj);
				setOnClick(convertView, obj);
			}
			return convertView;
		}

		private void setOnClick(View view) {
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (state == COLSE) {
						state = OPEN;
					} else {
						state = COLSE;
					}
					notifyDataSetChanged();
				}
			});
		}

		private void setContent(View view) {
			ImageView pic = (ImageView) view
					.findViewById(R.id.channel_grid_item_pic);
			double w = WinTool.getWinWidth(context) / 4;
			double h = w / 27 * 23;
			pic.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
			if (state == COLSE) {
				pic.setImageResource(R.drawable.open_c_icon);
			} else {
				pic.setImageResource(R.drawable.close_c_icon);
			}
		}

		private void setOnClick(View view, final ChannelObj obj) {
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Bundle bundle = new Bundle();
					bundle.putString(ServerListActivity.MM_CHANNEL, obj.getId());
					bundle.putString(ServerListActivity.MM_TITLE,
							obj.getTitle());
					Passageway.jumpActivity(context, ServerListActivity.class,
							bundle);
				}
			});
		}

		private void setContent(View view, ChannelObj obj) {
			ImageView pic = (ImageView) view
					.findViewById(R.id.channel_grid_item_pic);
			TextView title = (TextView) view
					.findViewById(R.id.channel_grid_item_title);

			// title.setText(obj.getTitle());

			double w = WinTool.getWinWidth(context) / 4;
			double h = w / 27 * 23;
			pic.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
			DownloadImageLoader.loadImage(pic, obj.getIco_url());

		}
	}

}
