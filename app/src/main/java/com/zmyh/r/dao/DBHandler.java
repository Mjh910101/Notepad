package com.zmyh.r.dao;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;

public class DBHandler {

	public static DbUtils getDbUtils(Context context) {
		DaoConfig config = new DaoConfig(context);
		config.setDbName("DB_MMJJR"); // db名
		config.setDbVersion(12); // db版本
		DbUtils db = DbUtils.create(config);
		// DbUtils db = DbUtils.create(context, "DB_MMJJR", 10,
		// new DbUpgradeListener() {
		//
		// @Override
		// public void onUpgrade(DbUtils arg0, int arg1, int arg2) {
		// Log.e("", "old : " + arg1 + " new : " + arg2);
		// }
		// });
		// db.createTableIfNotExist(CameraPicObj.class);
		// db.createTableIfNotExist(TroopObj.class);
		return db;
	}
}
