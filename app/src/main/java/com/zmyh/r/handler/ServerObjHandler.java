package com.zmyh.r.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.zmyh.r.box.CityObj;
import com.zmyh.r.box.ServerObj;

public class ServerObjHandler {

    private final static String TAP = "server_";

    public static boolean isWatch(Context context, ServerObj obj) {
        return SystemHandle.getBoolean(context, TAP + obj.getId());
    }

    public static void watchServerObj(Context context, ServerObj obj) {
        SystemHandle.saveBooleanMessage(context, TAP + obj.getId(), true);
    }

    public static void saveDaleteServerObj(Context context, ServerObj obj, String channel) {
        String str = getDaleteServerObj(context, channel);
//        JSONObject json = JsonHandle.getJSON(
//                new String[]{ServerObj.TITLE, ServerObj.CREATE_AT, ServerObj.POST_THUMBNAIL, ServerObj.MM_AREA},
//                new String[]{obj.getTitle(),
//                        String.valueOf(obj.getCreateAt()),
//                        obj.getPost_thumbnail(),
//                        JsonHandle.getJSON(new String[]{CityObj.AREA_NAME}, new String[]{obj.getCity()}).toString()});
        JSONObject json = obj.getJson();
        Log.e("json", json.toString());
        if (str.equals("")) {
            SystemHandle.saveStringMessage(context, "delete_server" + "_" + channel,
                    json.toString());
        } else {
            if (!str.contains(json.toString())) {
                SystemHandle.saveStringMessage(context, "delete_server" + "_" + channel, str
                        + "|" + json.toString());
            }
        }
    }

    public static String getDaleteServerObj(Context context, String channel) {
        return SystemHandle.getString(context, "delete_server" + "_" + channel);
    }

    public static List<ServerObj> getServerObjList(JSONArray array) {
        List<ServerObj> list = new ArrayList<ServerObj>();
        for (int i = 0; i < array.length(); i++) {
            list.add(getServerObj(JsonHandle.getJSON(array, i)));
        }
        return list;

    }

    public static ServerObj getServerObj(JSONObject json) {
        ServerObj obj = new ServerObj();

        obj.setJson(json);
        obj.setComment_count(JsonHandle.getInt(json, ServerObj.COMMENT_COUNT));
        obj.setContent(JsonHandle.getString(json, ServerObj.CONTENT));
        obj.setCreateAt(JsonHandle.getLong(json, ServerObj.CREATE_AT));
        obj.setFavor_count(JsonHandle.getInt(json, ServerObj.FAVOR_COUNT));
        obj.setHot(JsonHandle.getInt(json, ServerObj.HOT));
        obj.setId(JsonHandle.getString(json, ServerObj.ID));
        obj.setIntro(JsonHandle.getString(json, ServerObj.INTRO));
        obj.setMmChannel(ChannelObjHandler.getChannelObj(JsonHandle.getJSON(
                json, ServerObj.MM_CHANNEL)));
        obj.setPost_thumbnail(JsonHandle.getString(json,
                ServerObj.POST_THUMBNAIL));
        obj.setPoster(UserObjHandle.getUserBox(JsonHandle.getJSON(json,
                ServerObj.PRSTER)));
        obj.setTitle(JsonHandle.getString(json, ServerObj.TITLE));
        obj.setType(JsonHandle.getString(json, ServerObj.TYPE));
        obj.setExpireDate(JsonHandle.getInt(json, ServerObj.ExpireDate));

        JSONArray cityJson = JsonHandle.getArray(json, ServerObj.MM_AREA);
        if (cityJson != null) {
            obj.setMmArea(CityObjHandler.getCityObj(JsonHandle.getJSON(
                    cityJson, 0)));
        }

        JSONArray picArray = JsonHandle.getArray(json, ServerObj.PIC);
        if (picArray != null) {
            List<String> picList = new ArrayList<String>();
            for (int i = 0; i < picArray.length(); i++) {
                picList.add(JsonHandle.getString(picArray, i));
            }
            obj.setPic(picList);
        }

        JSONObject infoJson = JsonHandle.getJSON(json, ServerObj.CONTACT_INFFO);
        if (infoJson != null) {
            obj.setAddress(JsonHandle.getString(infoJson, ServerObj.ADDRESS));
            obj.setName(JsonHandle.getString(infoJson, ServerObj.NAME));
            obj.setPhone(JsonHandle.getString(infoJson, ServerObj.PHONE));
        }

        JSONObject treeJson = JsonHandle.getJSON(json, ServerObj.TREE);
        if (treeJson != null) {
            obj.setMu_gf(JsonHandle.getString(treeJson, ServerObj.MU_GF));
            obj.setMu_j(JsonHandle.getString(treeJson, ServerObj.MU_J));
            obj.setMu_jz_time(JsonHandle.getString(treeJson,
                    ServerObj.MU_JZ_TIME));
            obj.setMu_price(JsonHandle.getString(treeJson, ServerObj.MU_PRICE));
            obj.setMu_sz_type(JsonHandle.getString(treeJson,
                    ServerObj.MU_SZ_TYPE));
            obj.setMu_total(JsonHandle.getString(treeJson, ServerObj.MU_TOTAL));
            obj.setMu_type(JsonHandle.getString(treeJson, ServerObj.MU_TYPE));
            obj.setMu_zg(JsonHandle.getString(treeJson, ServerObj.MU_ZG));
            obj.setMu_coordinate_lat(JsonHandle.getDouble(treeJson,
                    ServerObj.MU_COORDINATE_LAT));
            obj.setMu_coordinate_long(JsonHandle.getDouble(treeJson,
                    ServerObj.MU_COORDINATE_LONG));
        }


        return obj;
    }
}
