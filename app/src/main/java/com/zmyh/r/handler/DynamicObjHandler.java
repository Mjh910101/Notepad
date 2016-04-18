package com.zmyh.r.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zmyh.r.box.DynamicObj;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.tool.ShowMessage;

public class DynamicObjHandler {

    private final static String TAP = "dynamic_";

    public static boolean isWatch(Context context, DynamicObj obj) {
        return SystemHandle.getBoolean(context, TAP + obj.getId());
    }

    public static void watchServerObj(Context context, DynamicObj obj) {
        SystemHandle.saveBooleanMessage(context, TAP + obj.getId(), true);
    }

    public static List<DynamicObj> getDynamicObjList(JSONArray array) {
        List<DynamicObj> list = new ArrayList<DynamicObj>();

        for (int i = 0; i < array.length(); i++) {
            list.add(getDynamicObj(JsonHandle.getJSON(array, i)));
        }

        return list;

    }

    public static DynamicObj getDynamicObj(JSONObject json) {
        DynamicObj obj = new DynamicObj();

        obj.setContent(JsonHandle.getString(json, DynamicObj.CONTENT));
        obj.setCreateAt(JsonHandle.getLong(json, DynamicObj.CREATE_AT));
        obj.setId(JsonHandle.getString(json, DynamicObj.ID));
        obj.setPoster(UserObjHandle.getUserBox(JsonHandle.getJSON(json,
                DynamicObj.PORTER)));
        obj.setTitle(JsonHandle.getString(json, DynamicObj.TITLE));
        obj.setType(JsonHandle.getString(json, DynamicObj.TYPE));
        obj.setPost_thumbnail(JsonHandle.getString(json,
                DynamicObj.POST_THUMBNAIL));
        obj.setIntro(JsonHandle.getString(json, DynamicObj.INTRO));
        obj.setComment_count(JsonHandle.getInt(json, DynamicObj.COMMENT_COUNT));
        obj.setFavor_count(JsonHandle.getInt(json, DynamicObj.FAVOR_COUNT));

        JSONArray picArray = JsonHandle.getArray(json, DynamicObj.PIC);
        if (picArray != null) {
            String[] pias = new String[picArray.length()];
            for (int i = 0; i < picArray.length(); i++) {
                pias[i] = JsonHandle.getString(picArray, i);
            }
            obj.setPic(pias);
        }

        JSONArray cityJson = JsonHandle.getArray(json, DynamicObj.MM_AREA);
        if (cityJson != null) {
            obj.setMmArea(CityObjHandler.getCityObj(JsonHandle.getJSON(
                    cityJson, 0)));
        }

        return obj;
    }

    public static void seeDynamicObj(Context context, DynamicObj obj) {
        SystemHandle.saveBooleanMessage(context, obj.getId(), true);
    }

    public static boolean isSeeDynamicObj(Context context, DynamicObj obj) {
        return SystemHandle.getBoolean(context, obj.getId());
    }

    public static void saveDynamicListCreate(Context context, DynamicObj obj) {
        SystemHandle.saveLongMessage(context, "DynamicListCreate", obj.getCreateAt());
    }

    public static long getDynamicListCreate(Context context) {
        return SystemHandle.getLong(context, "DynamicListCreate");
    }

    public static void isUploadNewMessage(final Context context, final CallbackForBoolean callbackForBoolean) {
        String url = UrlHandle.getMmPost()
                + "?sort=-createAt&p=1&l=1"
                + "&query="
                + JsonHandle.getHttpJsonToString(new String[]{"mmChannel",
                "mmArea"}, new String[]{"00019", CityObjHandler.getCityId(context, false)});

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
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
                                List<DynamicObj> list = DynamicObjHandler
                                        .getDynamicObjList(array);
                                if (!list.isEmpty()) {
                                    DynamicObj obj = list.get(0);
                                    if (callbackForBoolean != null) {
                                        Log.e("", obj.getCreateAt() + " --- " + getDynamicListCreate(context));
                                        callbackForBoolean.callback(obj.getCreateAt() > getDynamicListCreate(context));
                                    }
                                }
                            }

                        }
                    }

                });
    }

}
