package com.zmyh.r.handler;

import java.util.ArrayList;
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
import com.zmyh.r.box.UserObj;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.tool.ShowMessage;

public class CommentObjHandler {

	public interface CommentCallbackListener {
		public void callback(List<CommentObj> list, int totalPage);
	}

	public static List<CommentObj> getCommentObjList(JSONArray array) {
		List<CommentObj> list = new ArrayList<CommentObj>();
		for (int i = 0; i < array.length(); i++) {
			list.add(getCommentObj(JsonHandle.getJSON(array, i)));
		}
		return list;

	}

	private static CommentObj getCommentObj(JSONObject json) {
		CommentObj obj = new CommentObj();

		obj.setComment(JsonHandle.getString(json, CommentObj.COMMENT));
		obj.setCreateAt(JsonHandle.getLong(json, CommentObj.CREATE_AT));
		obj.setId(JsonHandle.getString(json, CommentObj.ID));
		obj.setPost_id(JsonHandle.getString(json, CommentObj.POST_ID));

		JSONObject posterJson = JsonHandle.getJSON(json, CommentObj.POSTER);
		if (posterJson != null) {
			obj.setPoster(UserObjHandle.getUserBox(posterJson));
		}

		return obj;
	}

	public static void getComment(final Context context, String post_id,
			int pageIndex, final CommentCallbackListener callback) {

		String url = UrlHandle.getMmPostComment()
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
								List<CommentObj> list = getCommentObjList(array);
								callback.callback(list,
										JsonHandle.getInt(json, "totalPage"));
							}
						}
					}

				});
	}

	public static void sendComment(final Context context, String post_id,
			String comment, final CallbackForBoolean callback) {

		String url = UrlHandle.getMmPostComment();

		RequestParams params = HttpUtilsBox.getRequestParams(context);
		params.addBodyParameter("post_id", post_id);
		params.addBodyParameter("poster", UserObjHandle.getUsetId(context));
		params.addBodyParameter("comment", comment);

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
