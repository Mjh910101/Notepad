package com.zmyh.r.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.dao.DBHandler;
import com.zmyh.r.handler.TroopObjHandler.TroophandleListener;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.tool.ShowMessage;

public class CameraPicObjHandler {

	public final static int pageSize = 20;

	public interface CamerahandlerListener {
		public void onSuccess(List<CameraPicObj> dataList);

		public void onFailure(HttpException exception);
	}

	public static void saveCameraPicObj(Context context, CameraPicObj obj) {
		try {
			DBHandler.getDbUtils(context).saveOrUpdate(obj);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	public static void deleteCameraPicObj(Context context, CameraPicObj obj,
			CallbackForBoolean callback) {
		obj.setShow_pic(false);
		saveCameraPicObj(context, obj);
		if (callback != null) {
			callback.callback(true);
		}
	}

	public static List<CameraPicObj> getCameraPicObjList(DbUtils db) {
		List<CameraPicObj> list = new ArrayList<CameraPicObj>();
		try {
			List<CameraPicObj> adList = db.findAll(Selector.from(
					CameraPicObj.class).where("show_max_pic", "=", true));
			if (adList != null) {
				for (CameraPicObj obj : adList) {
					list.add(obj);
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<CameraPicObj> getCameraPicObjList(DbUtils db,
			int pageIndex) {
		List<CameraPicObj> list = new ArrayList<CameraPicObj>();
		try {
			List<CameraPicObj> adList = db.findAll(Selector
					.from(CameraPicObj.class).where("show_max_pic", "=", true)
					.orderBy("createAt", true).limit(pageSize)
					.offset(pageSize * pageIndex));
			if (adList != null) {
				for (CameraPicObj obj : adList) {
					list.add(obj);
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static CameraPicObj getMaxCameraPicObj(DbUtils db, String id)
			throws DbException {
		return db.findFirst(Selector.from(CameraPicObj.class)
				.where("id", "=", id).and("show_max_pic", "=", true));
	}

	public static CameraPicObj getCameraPicObj(Context context, String id)
			throws DbException {
		return getCameraPicObj(DBHandler.getDbUtils(context), id);
	}

	public static CameraPicObj getCameraPicObj(DbUtils db, String id)
			throws DbException {
		return db.findFirst(Selector.from(CameraPicObj.class)
				.where("id", "=", id).and("show_pic", "=", true));
	}

	public static void deleteOnlinePic(final Context context, String keys,
			final CamerahandlerListener callback) {
		String url = UrlHandle.getDeletePic() + "?user_id="
				+ UserObjHandle.getUsetId(context) + "&keys=" + keys;

		RequestParams params = HttpUtilsBox.getRequestParams();

		HttpUtilsBox.getHttpUtil().send(HttpMethod.DELETE, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException exception, String msg) {
						ShowMessage.showFailure(context);
						callback.onFailure(exception);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						Log.d("", result);
						JSONObject json = JsonHandle.getJSON(result);
						if (json != null) {
							goneDeleteListString(context);
							callback.onSuccess(null);
						}
					}
				});
	}

	public static void deleteOnlineMaxPic(final Context context,
			List<CameraPicObj> choiceList, final CamerahandlerListener callback) {

		StringBuffer key = new StringBuffer();
		for (CameraPicObj obj : choiceList) {
			key.append(obj.getMu_hq_photo_key());
			key.append("%7C");
		}

		String url = UrlHandle.getMmTreeHqPhoto() + "?user_id="
				+ UserObjHandle.getUsetId(context) + "&keys="
				+ key.toString().substring(0, key.length() - 3);

		RequestParams params = HttpUtilsBox.getRequestParams();

		HttpUtilsBox.getHttpUtil().send(HttpMethod.DELETE, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException exception, String msg) {
						ShowMessage.showFailure(context);
						callback.onFailure(exception);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						Log.d("", result);
						JSONObject json = JsonHandle.getJSON(result);
						if (json != null) {
							callback.onSuccess(null);
						}
					}

				});

	}

	public static void downloadCameraPicObj(final Context context,
			final CamerahandlerListener callback) {
		String url = UrlHandle.getMmTreeHqPhoto() + "?user_id="
				+ UserObjHandle.getUsetId(context);

		RequestParams params = HttpUtilsBox.getRequestParams();

		HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException exception, String msg) {
						ShowMessage.showFailure(context);
						callback.onFailure(exception);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						Log.d("", result);
						JSONArray array = JsonHandle.getArray(result);
						if (array != null) {
							List<CameraPicObj> list = CameraPicObjHandler
									.getCameraPicObjList(array);
							callback.onSuccess(list);
						}
					}

				});

	}

	public static List<CameraPicObj> getCameraPicObjList(JSONArray array) {
		List<CameraPicObj> list = new ArrayList<CameraPicObj>();
		for (int i = 0; i < array.length(); i++) {
			list.add(getCameraPicObj(JsonHandle.getJSON(array, i)));
		}
		return list;
	}

	private static CameraPicObj getCameraPicObj(JSONObject json) {
		CameraPicObj obj = new CameraPicObj();

		String mu_hq_photo_name = JsonHandle
				.getString(json, "mu_hq_photo_name");
		if (!mu_hq_photo_name.equals("")) {
			obj.setId(mu_hq_photo_name.replace("o_", "").replace(".jpg", "")
					.replace(".png", ""));
		}

		String mu_photo_name = JsonHandle.getString(json, "mu_photo_name");
		if (!mu_photo_name.equals("")) {
			obj.setId(mu_photo_name.replace("m_", "").replace(".jpg", "")
					.replace(".png", ""));
		}

		obj.setCreateAt(JsonHandle.getLong(json, "createAt"));

		obj.setMu_hq_photo_key(JsonHandle.getString(json, "mu_hq_photo_key"));
		obj.setMu_id(JsonHandle.getString(json, "mu_id"));
		obj.setMu_photo_key(JsonHandle.getString(json, "mu_photo_key"));
		obj.setMu_photo_original(JsonHandle
				.getString(json, "mu_photo_original"));
		obj.setMu_photo_thumbnail(JsonHandle.getString(json,
				"mu_photo_thumbnail"));
		obj.setSize(JsonHandle.getLong(json, "size"));

		return obj;
	}

	public static void goneDeleteListString(Context context) {
		SystemHandle.saveStringMessage(context, "DELETE_PIC_ID", "");
	}

	public static String getDeleteListString(Context context) {
		return SystemHandle.getString(context, "DELETE_PIC_ID");
	}

	public static void saveDeleteID(Context context, CameraPicObj obj) {
		String str = getDeleteListString(context);
		String key = obj.getMu_photo_key();
		if (key != null && !key.equals("") && !key.equals("null")) {
			if (str.equals("")) {
				str = key;
			} else {
				if (!str.contains(key)) {
					str = str + "%7C" + key;
				}
			}
			SystemHandle.saveStringMessage(context, "DELETE_PIC_ID", str);
		}
	}

}
