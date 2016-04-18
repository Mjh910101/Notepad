package com.zmyh.r.photo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zmyh.r.interfaces.CallbackForBoolean;

public class PhotoBaseAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater;
	CallbackForBoolean callback;

	public PhotoBaseAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return null;
	}

	public void setIsClickListener(CallbackForBoolean callback) {
		this.callback = callback;
	}

}
