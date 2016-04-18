package com.zmyh.r.tool;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.zmyh.r.handler.JsonHandle;

public class ShowMessage {

	public static void logException(Exception e) {
		if (e != null) {
			e.printStackTrace();
		}
	}

	public static void showToast(Context context, String msg, int duration) {
		Toast.makeText(context, msg, duration).show();
	}

	public static void showToast(Context context, String msg) {
		if (context != null) {
			showToast(context, msg, 0);
		}
	}

	public static void showFailure(Context context) {
		showToast(context, "网络不佳");
	}

	public static void showLast(Context context) {
		showToast(context, "没有数据了");
	}

	public static void showException(Context context, Exception e) {
		showToast(context, "网络不佳");
		logException(e);
	}

	public static boolean showException(Context context, JSONObject error) {
		if (error != null) {
			String msg = JsonHandle.getString(error, "error");
			Log.e("error", msg);
			if (!msg.equals("")) {
				showToast(context, msg);
				return true;
			}
		}
		return false;
	}

}
