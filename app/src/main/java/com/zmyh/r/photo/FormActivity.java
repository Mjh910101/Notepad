package com.zmyh.r.photo;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.TroopObj;

public class FormActivity extends Activity {

	private Context context;

	@ViewInject(R.id.title_titleName)
	private TextView titleName;
	@ViewInject(R.id.form_dataList)
	private ListView dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_from);
		ViewUtils.inject(this);
		context = this;

		initActivity();
	}

	@OnClick({ R.id.title_back })
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.title_back:
				finish();
				break;
		}
	}

	private void initActivity() {
		titleName.setText("生成表格");
		List<TroopObj> list = TroopObjBox.getSaveList();
		if (!list.isEmpty()) {
			dataList.setAdapter(new FormBaseAdapter(list));
		}
	}

	class FormBaseAdapter extends BaseAdapter {

		private List<TroopObj> dataList;
		private LayoutInflater inflater;

		public FormBaseAdapter(List<TroopObj> list) {
			this.dataList = list;
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.form_list_item, null);
			}

			TroopObj obj = dataList.get(position);
			setView(convertView, obj);

			return convertView;
		}

		private void setView(View view, TroopObj obj) {
			TextView name = (TextView) view.findViewById(R.id.form_item_name);
			TextView sum = (TextView) view.findViewById(R.id.form_item_sum);
			TextView content = (TextView) view
					.findViewById(R.id.form_item_content);

			name.setText(obj.getMu_name());
			sum.setText(obj.getMu_total());
			content.setText(obj.getInfo());
		}

	}
}
