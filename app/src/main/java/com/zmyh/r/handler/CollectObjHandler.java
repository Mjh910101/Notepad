package com.zmyh.r.handler;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zmyh.r.box.CommentObj;
import com.zmyh.r.box.DynamicObj;
import com.zmyh.r.box.MsgObj;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.tool.ShowMessage;

public class CollectObjHandler {

	public interface CollectCallbackListener {
		public void callback(List<CommentObj> list, int totalPage);
	}

	public interface DynamicObjCallbackListener {
		public void callback(List<MsgObj> list, int totalPage);
	}

	public static void getServerFavor(final Context context, int pageIndex,
			final DynamicObjCallbackListener callback) {
		String query = JsonHandle.getHttpJsonToString(new String[] { "poster",
				"post_id.type" },
				new String[] { UserObjHandle.getUsetId(context), "services" });
		getFavor(context, query, pageIndex, callback);
	}

	public static void getUserCollect(final Context context, int pageIndex,
			final DynamicObjCallbackListener callback) {
		String query = JsonHandle.getHttpJsonToString(new String[] { "poster",
				"post_id.type" },
				new String[] { UserObjHandle.getUsetId(context), "topic" });
		getFavor(context, query, pageIndex, callback);
	}

	private static void getFavor(final Context context, String query,
			int pageIndex, final DynamicObjCallbackListener callback) {
		String url = UrlHandle.getMmPostFavor() + "?query=" + query + "&p="
				+ pageIndex + "&l=10&sort=-createAt";

		RequestParams params = HttpUtilsBox.getRequestParams(context);

		HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException exception, String msg) {
						ShowMessage.showFailure(context);
						callback.callback(null, 0);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						Log.d("", result);

						JSONObject json = JsonHandle.getJSON(
								JsonHandle.getJSON(result), "result");
						if (!ShowMessage.showException(context, json)) {
							JSONArray array = JsonHandle.getArray(json, "data");
							if (array != null) {
								List<MsgObj> list = MsgObjHandler
										.getMsgObjList(array);
								callback.callback(list,
										JsonHandle.getInt(json, "totalPage"));
							}
						}
					}

				});
	}

	public static void getCollect(final Context context, String post_id,
			int pageIndex, final CollectCallbackListener callback) {

		String url = UrlHandle.getMmPostFavor()
				+ "?query="
				+ JsonHandle.getHttpJsonToString(new String[] { "post_id" },
						new String[] { post_id }) + "&p=" + pageIndex
				+ "&l=10&user_id=" + UserObjHandle.getUsetId(context);

		RequestParams params = HttpUtilsBox.getRequestParams(context);

		HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException exception, String msg) {
						ShowMessage.showFailure(context);
						callback.callback(null, 0);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						Log.d("", result);

						JSONObject json = JsonHandle.getJSON(
								JsonHandle.getJSON(result), "result");
						if (!ShowMessage.showException(context, json)) {
							JSONArray array = JsonHandle.getArray(json, "data");
							if (array != null) {
								List<CommentObj> list = CommentObjHandler
										.getCommentObjList(array);
								callback.callback(list,
										JsonHandle.getInt(json, "totalPage"));
							}
						}
					}

				});
	}

	public static void sendCollect(final Context context, String post_id,
			int isFavor, final CallbackForBoolean callback) {
		Log.d("is_Favor", "Favor : " + isFavor);
		String url = UrlHandle.getMmPostFavor();

		RequestParams params = HttpUtilsBox.getRequestParams(context);
		params.addBodyParameter("post_id", post_id);
		params.addBodyParameter("isFavor", String.valueOf(isFavor));
		params.addBodyParameter("poster", UserObjHandle.getUsetId(context));

		HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException exception, String msg) {
						ShowMessage.showFailure(context);
						callback.callback(false);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						String result = responseInfo.result;
						Log.d("", result);

						JSONObject json = JsonHandle.getJSON(
								JsonHandle.getJSON(result), "result");
						if (!ShowMessage.showException(context, json)) {
							int r = JsonHandle.getInt(json, "ok");
							callback.callback(r == 1);
						}
					}

				});

	}

}
