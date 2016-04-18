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
import com.zmyh.r.box.MsgObj;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForInteger;
import com.zmyh.r.tool.ShowMessage;

public class MsgObjHandler {

    public static List<MsgObj> getMsgObjList(JSONArray array) {
        List<MsgObj> list = new ArrayList<MsgObj>();
        for (int i = 0; i < array.length(); i++) {
            list.add(getMsgObj(JsonHandle.getJSON(array, i)));
        }
        return list;
    }

    private static MsgObj getMsgObj(JSONObject json) {
        MsgObj obj = new MsgObj();

        obj.setComment(JsonHandle.getString(json, MsgObj.COMMENT));
        obj.setCreateAt(JsonHandle.getLong(json, MsgObj.CREATE_AT));
        obj.setId(JsonHandle.getString(json, MsgObj.ID));

        JSONObject postJson = JsonHandle.getJSON(json, MsgObj.POST_ID);
        if (postJson != null) {
            obj.setPost(DynamicObjHandler.getDynamicObj(postJson));

            JSONObject channelJson = JsonHandle.getJSON(postJson,
                    MsgObj.MM_CHANNEL);
            if (channelJson != null) {
                obj.setMmChannel(ChannelObjHandler.getChannelObj(channelJson));
            }
        }

        JSONObject posterJson = JsonHandle.getJSON(json, MsgObj.POSTER);
        if (posterJson != null) {
            obj.setPoster(UserObjHandle.getUserBox(posterJson));
        }

        return obj;
    }

    private static final String M_KEY = "message_watch_sum_key";
    private static final String S_KEY = "message_sum_key";

    public static void watch(Context context, MsgObj obj) {
        if (!SystemHandle.getBoolean(context, obj.getId())) {
            SystemHandle.saveBooleanMessage(context, obj.getId(), true);
            int sum = getWatvhSum(context);
            sum += 1;
            SystemHandle.saveIntMessage(context, M_KEY, sum);
        }
    }

    public static int getWatvhSum(Context context) {
        return SystemHandle.getInt(context, M_KEY);
    }

    public static void saveMeaageSum(Context context, int sum) {
        SystemHandle.saveIntMessage(context, S_KEY, sum);
    }

    public static int getMeaageSum(Context context) {
        return SystemHandle.getInt(context, S_KEY);
    }

    public static void getMessageSize(final Context context, final CallbackForInteger callback) {

        String url = UrlHandle.getMmPostComment() + "/count" + "?query=" +
                JsonHandle.getHttpJsonToString(
                        new String[]{"post_id.poster"},
                        new String[]{UserObjHandle.getUsetId(context)});

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        callback.callback(0);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(
                                JsonHandle.getJSON(result), "result");
                        if (!ShowMessage.showException(context, json)) {
                            callback.callback(JsonHandle.getInt(json, "o"));
                        }
                    }

                });
    }


}
