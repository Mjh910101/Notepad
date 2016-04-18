package com.zmyh.r.main.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.tool.Passageway;

public class ForumHeadView extends LinearLayout {

	private Context context;
	private LayoutInflater inflater;
	private View acitvity;

	private TextView shareBtn;

	public ForumHeadView(Context context) {
		super(context);
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		acitvity = inflater.inflate(R.layout.forum_head, null);

		initView();
		initOnClickLietener();

		addView(acitvity);
	}

	private void initOnClickLietener() {
		shareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Passageway.jumpActivity(context, ShareForumActivity.class);
			}
		});
	}

	private void initView() {
		shareBtn = (TextView) acitvity.findViewById(R.id.forum_content_share);
	}

}
