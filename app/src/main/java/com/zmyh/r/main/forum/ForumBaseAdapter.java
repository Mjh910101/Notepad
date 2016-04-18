package com.zmyh.r.main.forum;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.DynamicObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.DynamicObjHandler;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.view.InsideGridView;

public class ForumBaseAdapter extends BaseAdapter {

	private static String R1 = "%@1@%";
	private static String R2 = "%@2@%";
	private static String RC = "%@c@%";
	private static String PRICE_1 = "<font color=" + RC + ">" + R1;
	private static String PRICE_2 = "</font><font color=\"#44b13f\">" + R2
			+ "</font>";

	private LayoutInflater inflater;
	private List<DynamicObj> itemList;
	private Context context;

	public ForumBaseAdapter(Context context) {
		this.itemList = new ArrayList<DynamicObj>();
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public ForumBaseAdapter(Context context, List<DynamicObj> list) {
		this.itemList = list;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItems(List<DynamicObj> list) {
		for (DynamicObj obj : list) {
			addItem(obj);
		}
		notifyDataSetChanged();
	}

	private void addItem(DynamicObj obj) {
		itemList.add(obj);
	}

	public void removeAll() {
		itemList.removeAll(itemList);
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.forum_list_item_v2, null);
		}

		DynamicObj dynamicObj = itemList.get(position);
		setContent(convertView, dynamicObj);
		setContentView(convertView, dynamicObj);
		setPicList(convertView, dynamicObj);
		setNewIcon(convertView, dynamicObj);
		setOnClick(convertView, dynamicObj);
		return convertView;
	}

	private void setNewIcon(View view, DynamicObj obj) {

	}

	private void setOnClick(View view, final DynamicObj obj) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jumpContent(obj);
			}

		});

	}

	private void jumpContent(DynamicObj obj) {
		DynamicObjHandler.watchServerObj(context, obj);
		Bundle b = new Bundle();
		b.putString("id", obj.getId());
		Passageway.jumpActivity(context, ForumContentActivity.class, b);
	}

	private void setPicList(View view, DynamicObj obj) {
		InsideGridView picGrid = (InsideGridView) view
				.findViewById(R.id.forum_listitem_picGrid);
		List<String> pisList = obj.getPicList();
		int w = 0, width = 0;
		switch (pisList.size()) {
			case 1:
				// w = WinTool.dipToPx(context, 180);
				// width = WinTool.dipToPx(context, 180);
				w = WinTool.dipToPx(context, 80);
				width = WinTool.dipToPx(context, 80);
				picGrid.setNumColumns(1);
				picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
						LayoutParams.WRAP_CONTENT));
				break;
			case 2:
			case 4:
				w = WinTool.dipToPx(context, 80);
				width = WinTool.dipToPx(context, 170);
				picGrid.setNumColumns(2);
				picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
						LayoutParams.WRAP_CONTENT));
				break;
			default:
				w = WinTool.dipToPx(context, 80);
				width = WinTool.dipToPx(context, 255);
				picGrid.setNumColumns(3);
				picGrid.setLayoutParams(new LinearLayout.LayoutParams(width,
						LayoutParams.WRAP_CONTENT));
				break;
		}
		picGrid.setAdapter(new PicBaseAdapter(obj, w));
	}

	private void setContent(View view, DynamicObj obj) {
		// TextView title = (TextView) view
		// .findViewById(R.id.forum_listitem_title);
		TextView userName = (TextView) view
				.findViewById(R.id.forum_listitem_userName);
		TextView time = (TextView) view.findViewById(R.id.forum_listitem_time);
		// TextView content = (TextView) view
		// .findViewById(R.id.forum_listitem_content);
		ImageView userPic = (ImageView) view
				.findViewById(R.id.forum_listitem_userIcon);
		TextView city = (TextView) view.findViewById(R.id.forum_listitem_city);
		TextView commentTitle = (TextView) view
				.findViewById(R.id.forum_listitem_commentTitle);
		TextView collectTitle = (TextView) view
				.findViewById(R.id.forum_content_collectTitle);

		// title.setText(obj.getTitle());
		userName.setText(obj.getUserName());
		time.setText(obj.getCreateTime());
		// content.setText(obj.getIntro());
		city.setText(obj.getCity());
		commentTitle.setText(String.valueOf(obj.getComment_count()));
		collectTitle.setText(String.valueOf(obj.getFavor_count()));

		int w = WinTool.getWinWidth(context) / 10;
		userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
		DownloadImageLoader.loadImage(userPic, obj.getUserPic(), w / 2);

		if(!DynamicObjHandler.isWatch(context, obj)){
			userName.setTextColor(ColorBox.getColorForID(context,R.color.black));
			time.setTextColor(ColorBox.getColorForID(context,R.color.text_green));
		}else{
			userName.setTextColor(ColorBox.getColorForID(context,R.color.text_gray_04));
			time.setTextColor(ColorBox.getColorForID(context,R.color.text_gray_04));
		}
	}

	private void setContentView(View v, DynamicObj obj) {
		int max = 58;
		TextView content = (TextView) v
				.findViewById(R.id.forum_listitem_content);
		String c = obj.getIntro();
		if (!c.equals("")) {
			String m = "更多";
			if (c.length() >= max) {
				c = c.subSequence(0, max) + "...";
				c = PRICE_1.replace(R1, c) + PRICE_2.replace(R2, m);
			} else {
				c = PRICE_1.replace(R1, c);
			}
			if (DynamicObjHandler.isWatch(context, obj)) {
				c = c.replace(RC, "#999999");
			} else {
				c = c.replace(RC, "#000000");
			}
			content.setVisibility(View.VISIBLE);
			content.setText(Html.fromHtml(c));
		} else {
			content.setVisibility(View.GONE);
		}
	}

	class PicBaseAdapter extends BaseAdapter {

		private DynamicObj obj;
		private List<String> picList;
		private LayoutInflater inflater;
		private int w;
		private int size;

		public PicBaseAdapter(DynamicObj obj, int w) {
			this.obj = obj;
			this.picList = obj.getPicList();
			this.w = w;
			this.size = picList.size();
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// return picList.size() > 9 ? 9 : picList.size();
			switch (size) {
				case 1:
				case 2:
				case 3:
				case 4:
					return size;
				case 5:
				case 6:
					return 6;
				case 7:
				case 8:
				case 9:
					return 9;
				default:
					if (size <= 0) {
						return 0;
					} else {
						return 9;

					}
			}
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.image_layout, null);
			}
			String url = "";
			try {
				url = picList.get(position);
			} catch (Exception e) {
				e.printStackTrace();
			}

			setPic(convertView, url);
			setOnClickPic(convertView, url, position);

			return convertView;
		}

		private void seePic(int position) {
			Bundle b = new Bundle();
			b.putStringArrayList("iamge_list", (ArrayList<String>) picList);
			b.putInt("position", position);
			b.putBoolean("isOnline", true);
			Passageway.jumpActivity(context, ImageListAcitvity.class, b);
		}

		private void setOnClickPic(View view, final String url,
								   final int position) {
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (!url.equals("")) {
						seePic(position);
					} else {
						jumpContent(obj);
					}
				}

			});
		}

		private void setPic(View view, String url) {
			ImageView image = (ImageView) view
					.findViewById(R.id.image_layout_item);
			image.setLayoutParams(new LinearLayout.LayoutParams(w, w));
			image.setScaleType(ScaleType.CENTER_CROP);
			if (!url.equals("")) {
				DownloadImageLoader.loadImage(image, url);
			}
		}

	}
}
