package com.zmyh.r.city;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.CityObj;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.interfaces.CallbackForObject;

public class CityBaseAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<CityObj> itemList;
	private Context context;

	private CallbackForObject callback;

	public CityBaseAdapter(Context context, List<CityObj> cityList) {
		this.context = context;
		this.itemList = cityList;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setCallback(CallbackForObject callback) {
		this.callback = callback;
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
			convertView = inflater.inflate(R.layout.city_list_item, null);
		}

		CityObj obj = itemList.get(position);
		setContent(convertView, obj);
		setOnClick(convertView, obj);
		return convertView;
	}

	private void setOnClick(View view, final CityObj obj) {
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (callback != null) {
					callback.callback(obj);
				}
			}
		});
	}

	private void setContent(View view, CityObj obj) {
		TextView cityName = (TextView) view
				.findViewById(R.id.city_list_cityName);
		TextView select = (TextView) view.findViewById(R.id.city_list_select);

		cityName.setText(obj.getArea_name());

		if (CityObjHandler.getAreaCode(context).equals(obj.getArea_code())
				|| CityObjHandler.getCityCode(context).equals(
						obj.getArea_code())) {
			cityName.setTextColor(ColorBox.getColorForID(context,
					R.color.text_green));
			select.setTextColor(ColorBox.getColorForID(context,
					R.color.text_green));
		} else {
			cityName.setTextColor(ColorBox
					.getColorForID(context, R.color.black));
			select.setTextColor(ColorBox.getColorForID(context, R.color.black));
		}

		if (obj.getArea_level() == 0) {
			select.setVisibility(View.VISIBLE);
		} else {
			select.setVisibility(View.GONE);
		}

	}

}
