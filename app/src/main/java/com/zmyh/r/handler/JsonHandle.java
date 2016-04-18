package com.zmyh.r.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Json操作
 * 
 * @author Administrator
 * 
 */
public class JsonHandle {

	/**
	 * 获取JSONObject
	 * 
	 * @param result
	 * @return
	 */
	public static JSONObject getJSON(String result) {
		JSONObject json = null;
		try {
			json = new JSONObject(result);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return json;
	}

	/**
	 * 获取JSONObject
	 * 
	 * @param arr
	 * @param index
	 * @return
	 */
	public static JSONObject getJSON(JSONArray arr, int index) {
		JSONObject json = null;
		try {
			json = arr.getJSONObject(index);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return json;
	}

	/**
	 * 获取JSONObject
	 * 
	 * @param js
	 * @param key
	 * @return
	 */
	public static JSONObject getJSON(JSONObject js, String key) {
		JSONObject json = null;
		try {
			json = js.getJSONObject(key);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return json;
	}

	/**
	 * 获取JSONArray
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static JSONArray getArray(JSONObject json, String key) {
		JSONArray array = null;
		try {
			array = json.getJSONArray(key);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return array;
	}

	public static JSONArray getArray(String result) {
		JSONArray array = null;
		try {
			array = new JSONArray(result);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return array;
	}

	/**
	 * 获取JSONArray
	 * 
	 * @param array
	 * @param index
	 * @return
	 */
	public static JSONArray getArray(JSONArray array, int index) {
		JSONArray newArray = null;
		try {
			newArray = array.getJSONArray(index);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return newArray;
	}

	/**
	 * 获取String
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static String getString(JSONObject json, String key) {
		String str = "";
		try {
			str = json.getString(key);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return str;
	}

	/**
	 * 
	 * @param array
	 * @param index
	 * @return
	 */
	public static String getString(JSONArray array, int index) {
		String str = "";
		try {
			str = array.getString(index);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return str;
	}

	/**
	 * 获取Int
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static int getInt(JSONObject json, String key) {
		int num = -1;
		try {
			num = json.getInt(key);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return num;
	}

	/**
	 * 获取Boolean
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static boolean getBoolean(JSONObject json, String key) {
		boolean b = false;
		try {
			b = json.getBoolean(key);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return b;
	}

	/**
	 * 获取long
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static long getLong(JSONObject json, String key) {
		long num = -1;
		try {
			num = Long.valueOf(json.getString(key));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return num;
	}

	/**
	 * 获取double
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static double getDouble(JSONObject json, String key) {
		double num = -1;
		try {
			num = json.getDouble(key);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return num;
	}

	public static boolean put(JSONObject json, String key, String value) {
		try {
			json.put(key, value);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean put(JSONObject json, String key, int value) {
		try {
			json.put(key, value);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean put(JSONObject json, String key, long value) {
		try {
			json.put(key, value);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean put(JSONObject json, String key, double value) {
		try {
			json.put(key, value);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean put(JSONObject json, String key, boolean value) {
		try {
			json.put(key, value);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean put(JSONObject json, String key, JSONArray value) {
		try {
			json.put(key, value);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean put(JSONArray array, JSONObject value) {
		array.put(value);
		return true;
	}

	public static JSONObject getJSON(String[] keys, String[] values) {
		JSONObject json = new JSONObject();
		if (keys.length == values.length) {
			int l = values.length;
			for (int i = 0; i < l; i++) {
				JsonHandle.put(json, keys[i], values[i]);
			}
		}
		return json;
	}

	public static String getHttpJsonToString(String[] keys, String[] values) {
		JSONObject json = getJSON(keys, values);
		String str = json.toString();
		Log.e("newstr", "str:    " + str);
		String newstr = str;
		try {
			newstr = URLEncoder.encode(str, "utf-8");
			Log.e("newstr", "newstr:    " + newstr);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return newstr;
	}
}
