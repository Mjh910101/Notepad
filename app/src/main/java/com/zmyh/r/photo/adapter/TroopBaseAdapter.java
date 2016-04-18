package com.zmyh.r.photo.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.zmyh.r.box.TroopObj;

public class TroopBaseAdapter extends PhotoBaseAdapter {
	List<TroopObj> dataList;
	List<TroopObj> choiceList;

	public TroopBaseAdapter(Context context) {
		super(context);
		dataList = new ArrayList<TroopObj>();
		choiceList = new ArrayList<TroopObj>();
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

	public List<TroopObj> getChoiceList() {
		return choiceList;
	}

	public boolean isChoiceOne() {
		if (choiceList != null) {
			return !choiceList.isEmpty();
		}
		return false;
	}

	public void addItem(List<TroopObj> list) {
		for (TroopObj obj : list) {
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

	public void addChoiceList(TroopObj obj) {
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

}
