package com.zmyh.r.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zmyh.r.box.ChannelObj;

public class ChannelObjHandler {

	public static List<ChannelObj> getChannelObjList(JSONArray array) {
		List<ChannelObj> list = new ArrayList<ChannelObj>();
		for (int i = 0; i < array.length(); i++) {
			list.add(getChannelObj(JsonHandle.getJSON(array, i)));
		}
		return list;
	}

	public static ChannelObj getChannelObj(JSONObject json) {
		ChannelObj obj = new ChannelObj();

		obj.setId(JsonHandle.getString(json, ChannelObj.ID));
		obj.setIco_url(JsonHandle.getString(json, ChannelObj.ICO_URL));
		obj.setTitle(JsonHandle.getString(json, ChannelObj.TITLE));

		return obj;
	}
}
