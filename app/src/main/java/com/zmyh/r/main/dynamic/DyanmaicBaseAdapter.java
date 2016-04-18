package com.zmyh.r.main.dynamic;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.DynamicObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.DynamicObjHandler;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.tool.Passageway;

public class DyanmaicBaseAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<DynamicObj> itemList;
	private Context context;

	public DyanmaicBaseAdapter(Context context, List<DynamicObj> list) {
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
			convertView = inflater.inflate(R.layout.dyanmaic_list_item, null);
		}

		DynamicObj obj = itemList.get(position);
		setContent(convertView, obj);
		setContentPic(convertView, obj);
		setOnClick(convertView, obj);
		return convertView;
	}

	private void setOnClick(View view, final DynamicObj obj) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DynamicObjHandler.watchServerObj(context, obj);
				Bundle b = new Bundle();
				b.putString("id", obj.getId());
				Passageway.jumpActivity(context, DynamicContentActivity.class,
						b);
			}
		});
	}

	private void setContentPic(View view, DynamicObj obj) {
		ImageView pic = (ImageView) view
				.findViewById(R.id.dyanmaic_list_item_pic);
		int w = WinTool.getWinWidth(context) / 3;
		if (obj.getPost_thumbnail() != null
				&& !obj.getPost_thumbnail().equals("null")
				&& !obj.getPost_thumbnail().equals("")) {
			pic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
			DownloadImageLoader.loadImage(pic, obj.getPost_thumbnail());
		} else {
			pic.setImageBitmap(null);
			pic.setLayoutParams(new LinearLayout.LayoutParams(1, w));
		}

	}

	private void setContent(View view, DynamicObj obj) {
		TextView title = (TextView) view
				.findViewById(R.id.dyanmaic_list_item_title);
		TextView content = (TextView) view
				.findViewById(R.id.dyanmaic_list_item_context);
		TextView time = (TextView) view
				.findViewById(R.id.dyanmaic_list_item_time);
		TextView city = (TextView) view
				.findViewById(R.id.dyanmaic_list_item_city);

		title.setText(obj.getTitle());
		content.setText(obj.getIntro());
		time.setText(obj.getCreateTime());
		city.setText(obj.getCity());
		setIsNewContent(title, obj);
	}

	private void setIsNewContent(TextView view, DynamicObj obj) {
		if (DynamicObjHandler.isWatch(context, obj)) {
			view.setTextColor(ColorBox.getColorForID(context,
					R.color.text_gray_04));
		} else {
			view.setTextColor(ColorBox.getColorForID(context,
					R.color.text_green));
		}
	}

}
