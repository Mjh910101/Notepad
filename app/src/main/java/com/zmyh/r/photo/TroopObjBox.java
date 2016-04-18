package com.zmyh.r.photo;

import java.util.ArrayList;
import java.util.List;

import com.zmyh.r.box.TroopObj;

public class TroopObjBox {

	private static TroopObj obj = null;
	private static List<TroopObj> list = new ArrayList<TroopObj>();

	public static void saveTroopObj(TroopObj mTroopObj) {
		removeObj();
		obj = mTroopObj;

	}

	public static TroopObj getSaveTroopObj() {
		return obj;
	}

	public static void removeObj() {
		if (obj != null) {
			obj = null;
		}
	}

	public static void savaTroopList(List<TroopObj> choiceList) {
		list.removeAll(list);
		list.addAll(choiceList);
	}

	public static List<TroopObj> getSaveList() {
		return list;
	}

}
