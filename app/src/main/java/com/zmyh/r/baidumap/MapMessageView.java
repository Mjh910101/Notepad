package com.zmyh.r.baidumap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;

public class MapMessageView extends LinearLayout {

	private Context context;
	private LayoutInflater inflater;
	private View view;

	public MapMessageView(Context context, String name, String time, String desc) {
		super(context);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.map_msg_view, null);

		if (name.equals("") || name.equals("null")) {
			view.findViewById(R.id.message_name).setVisibility(View.GONE);
		} else {
			((TextView) view.findViewById(R.id.message_name)).setText(name);
		}

		if (desc.equals("") || desc.equals("null")) {
			view.findViewById(R.id.message_desc).setVisibility(View.GONE);
		} else {
			((TextView) view.findViewById(R.id.message_desc)).setText(desc);
		}

		((TextView) view.findViewById(R.id.message_time)).setText(time);

		addView(view);
	}

}
