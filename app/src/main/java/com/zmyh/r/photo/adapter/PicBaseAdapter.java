package com.zmyh.r.photo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.TroopObj;

public class PicBaseAdapter extends PhotoBaseAdapter {

	List<CameraPicObj> dataList;
	List<CameraPicObj> choiceList;

	public PicBaseAdapter(Context context) {
		super(context);
		dataList = new ArrayList<CameraPicObj>();
		choiceList = new ArrayList<CameraPicObj>();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		if (callback != null) {
			boolean b = false;
			if (choiceList != null) {
				b = !choiceList.isEmpty();
			}
			callback.callback(b);
		}
	}

	public void addItem(List<CameraPicObj> list) {
		for (CameraPicObj obj : list) {
			dataList.add(obj);
		}
		notifyDataSetChanged();
	}

	public void setAllChoice(boolean isChoice) {
		choiceList.removeAll(choiceList);
		if (isChoice) {
			choiceList.addAll(dataList);
		}
		notifyDataSetChanged();
	}

	public void addChoiceList(CameraPicObj obj) {
		if (choiceList.contains(obj)) {
			choiceList.remove(obj);
		} else {
			choiceList.add(obj);
		}
	}

	public boolean isAllChoice() {
		if (dataList != null && choiceList != null) {
			return dataList.size() == choiceList.size();
		}
		return false;
	}

	public List<CameraPicObj> getChoiceList() {
		return choiceList;
	}

	public boolean isChoiceOne() {
		if (choiceList != null) {
			return !choiceList.isEmpty();
		}
		return false;
	}

}
