package com.zmyh.r.handler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.zmyh.r.box.TypeDictObj;

public class TypeDictHandle {

	public static TypeDictBox getTypeDictBox(Context context) {
		InputStream is = null;
		String result = "";
		try {
			is = context.getAssets().open("mm_dict.txt");
			byte b[] = new byte[10 * 1024];
			int len = 0;
			int temp = 0;
			while ((temp = is.read()) != -1) {
				b[len] = (byte) temp;
				len++;
			}
			is.close();
			result = new String(b, 0, len, "utf-8");

		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject json = JsonHandle.getJSON(result);
		JSONArray array = JsonHandle.getArray(json, "typeDict");
		return getTypeDictBox(array);
	}

	public static TypeDictBox getTypeDictBox(JSONArray array) {
		List<TypeDictObj> list = new ArrayList<TypeDictObj>();
		for (int i = 0; i < array.length(); i++) {
			list.add(getTypeDictObj(JsonHandle.getJSON(array, i)));
		}
		return new TypeDictBox(list);
	}

	private static TypeDictObj getTypeDictObj(JSONObject json) {
		TypeDictObj obj = new TypeDictObj();
		obj.setMu_gf(JsonHandle.getString(json, TypeDictObj.MU_GF));
		obj.setMu_j(JsonHandle.getString(json, TypeDictObj.MU_J));
		obj.setMu_jz_time(JsonHandle.getString(json, TypeDictObj.MU_JZ_TIME));
		obj.setMu_sz_type(JsonHandle.getString(json, TypeDictObj.MU_SZ_TYPE));
		obj.setMu_type(JsonHandle.getString(json, TypeDictObj.MU_TYPE));
		obj.setMu_zg(JsonHandle.getString(json, TypeDictObj.MU_ZG));
		return obj;
	}

}
