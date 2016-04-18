package com.zmyh.r.handler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;


import com.zmyh.r.box.VersionObj;

import org.json.JSONObject;

public class VersionObjHandler {

	public static VersionObj getVersionObj(JSONObject json) {
		VersionObj obj = new VersionObj();

		obj.setChangelog(JsonHandle.getString(json, VersionObj.CHANGE_LOG));
		obj.setUpdate_url(JsonHandle.getString(json, VersionObj.UPDATE_URL));
		obj.setVersion(JsonHandle.getInt(json, VersionObj.VERSION));
		obj.setVersionShort(JsonHandle.getInt(json, VersionObj.VERSION_SHORT));

		return obj;
	}

	public static boolean detectionVersion(Context context, VersionObj obj) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			String versionName = packInfo.versionName;
			int versionCode = packInfo.versionCode;
			Log.e("", versionCode + ":" + obj.getVersionShort());
			if (versionCode < obj.getVersionShort()) {
				return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}
