package com.zmyh.r.main.server;

import java.util.List;

import org.json.JSONArray;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.baidumap.TreeMapActivity;
import com.zmyh.r.box.ChannelObj;
import com.zmyh.r.camera.CameraActivity;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.ChannelObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.photo.PhotoActivity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.view.InsideGridView;

public class ServerFrameLayoutV2 extends Fragment {

	private Context context;

	@ViewInject(R.id.server_channelGrid)
	private InsideGridView channelGrid;
	@ViewInject(R.id.server_progress)
	private ProgressBar progress;
	@ViewInject(R.id.server_takePic)
	private ImageView takePic;
	@ViewInject(R.id.server_photo)
	private ImageView photo;
	@ViewInject(R.id.server_mapBtn)
	private ImageView mapBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		View contactsLayout = inflater.inflate(R.layout.layout_server_v2,
				container, false);
		ViewUtils.inject(this, contactsLayout);
		initActivity();
		downloadData();
		return contactsLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@OnClick({ R.id.server_takePic, R.id.server_photo, R.id.server_mapBtn })
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.server_takePic:
			Passageway.jumpActivity(context, CameraActivity.class);
			break;
		case R.id.server_photo:
			Passageway.jumpActivity(context, PhotoActivity.class);
			break;
		case R.id.server_mapBtn:
			Bundle b = new Bundle();
			b.putString(TreeMapActivity.MM_CHANNEL, "00022");
			Passageway.jumpActivity(context, TreeMapActivity.class, b);
			break;
		}
	}

	private void initActivity() {
		double w = WinTool.getWinWidth(context);
		double h = w / 128 * 75;
		mapBtn.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));

		double pw = w / 2;
		double ph = pw / 64 * 55;
		takePic.setLayoutParams(new LinearLayout.LayoutParams((int) pw,
				(int) ph));
		photo.setLayoutParams(new LinearLayout.LayoutParams((int) pw, (int) ph));
	}

	private void setChannelList(List<ChannelObj> list) {
		channelGrid.setAdapter(new ChannelBaseAdapter(list));
	}

	private void downloadData() {
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
			// state = OPEN;
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
