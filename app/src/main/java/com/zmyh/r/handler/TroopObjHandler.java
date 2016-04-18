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
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.dao.DBHandler;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.tool.ShowMessage;

public class TroopObjHandler {

    public final static int pageSize = 20;

    public interface TroophandleListener {
        public void onSuccess(List<TroopObj> dataList);

        public void onFailure(HttpException exception);
    }

    public static TroopObj getTroopObj(DbUtils db, String mu_id)
            throws DbException {
        return db.findFirst(Selector.from(TroopObj.class)
                .where("mu_id", "=", mu_id).and("is_show", "=", true));
    }

    public static List<TroopObj> getTroopObjListForMuName(Context context,
                                                          String[] keys, String[] values, int pageIndex) {
        List<TroopObj> list = new ArrayList<TroopObj>();
        try {
            DbUtils db = DBHandler.getDbUtils(context);

            Selector s = Selector.from(TroopObj.class).where("is_show", "=",
                    true);
            if (keys != null && values != null) {
                if (keys.length == values.length) {
                    for (int i = 0; i < keys.length; i++) {
                        Log.e("", keys[i] + " " + values[i]);
                        if (keys[i].equals("mu_price")) {
                            s.and(keys[i], "<", values[i]);
                        } else if (keys[i].equals("mu_total")) {
                            s.and(keys[i], ">", values[i]);
                        } else {
                            s.and(keys[i], "=", values[i]);
                        }
                    }
                }
            }
            s.orderBy("mu_createTime", true).limit(pageSize)
                    .offset(pageSize * pageIndex);
            Log.e("SQL", "SQL = " + s.toString());
            List<TroopObj> adList = db.findAll(s);
            if (adList != null) {
                for (TroopObj obj : adList) {
                    Log.e("TroopObj", obj.toString());
                    String[] childIdList = obj.getMu_photo().split("\\|");
                    for (int i = 0; i < childIdList.length; i++) {
                        Log.e("child_id", childIdList[i]);
                        CameraPicObj child = CameraPicObjHandler
                                .getCameraPicObj(db, childIdList[i]);
                        if (child != null) {
                            Log.e("child", child.getId());
                            obj.addChild(child);
                        }
                    }
                    list.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<TroopObj> getTroopObjListForMuName(Context context,
                                                          String mu_name, int pageIndex) {
        List<TroopObj> list = new ArrayList<TroopObj>();
        try {
            DbUtils db = DBHandler.getDbUtils(context);
            List<TroopObj> adList = db.findAll(Selector.from(TroopObj.class)
                    .where("is_show", "=", true).and("mu_name", "=", mu_name)
                    .orderBy("mu_createTime", true).limit(pageSize)
                    .offset(pageSize * pageIndex));
            if (adList != null) {
                for (TroopObj obj : adList) {
                    Log.e("TroopObj", obj.toString());
                    String[] childIdList = obj.getMu_photo().split("\\|");
                    for (int i = 0; i < childIdList.length; i++) {
                        Log.e("child_id", childIdList[i]);
                        CameraPicObj child = CameraPicObjHandler
                                .getCameraPicObj(db, childIdList[i]);
                        if (child != null) {
                            Log.e("child", child.getId());
                            obj.addChild(child);
                        }
                    }
                    list.add(obj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<TroopObj> getTroopObjList(DbUtils db, String gs,
                                                 int pageIndex) {
        List<TroopObj> list = new ArrayList<TroopObj>();
        try {
            List<TroopObj> adList = db.findAll(Selector.from(TroopObj.class)
                    .where("is_show", "=", true).and("mu_gs", "=", gs)
                    .orderBy("mu_createTime", true).limit(pageSize)
                    .offset(pageSize * pageIndex));
            if (adList != null) {
                for (TroopObj obj : adList) {
                    Log.e("TroopObj", obj.toString());
                    String[] childIdList = obj.getMu_photo().split("\\|");
                    for (int i = 0; i < childIdList.length; i++) {
                        Log.e("child_id", childIdList[i]);
                        CameraPicObj child = CameraPicObjHandler
                                .getCameraPicObj(db, childIdList[i]);
                        if (child != null) {
                            Log.e("child", child.getId());
                            obj.addChild(child);
                        }
                    }
                    list.add(obj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<TroopObj> getTroopObjList(DbUtils db, int pageIndex) {
        List<TroopObj> list = new ArrayList<TroopObj>();
        try {
            List<TroopObj> adList = db.findAll(Selector.from(TroopObj.class)
                    .where("is_show", "=", true).orderBy("mu_createTime", true)
                    .limit(pageSize).offset(pageSize * pageIndex));
            if (adList != null) {
                for (TroopObj obj : adList) {
                    Log.e("TroopObj", obj.toString());
                    String[] childIdList = obj.getMu_photo().split("\\|");
                    for (int i = 0; i < childIdList.length; i++) {
                        Log.e("child_id", childIdList[i]);
                        CameraPicObj child = CameraPicObjHandler
                                .getCameraPicObj(db, childIdList[i]);
                        if (child != null) {
                            Log.e("child", child.getId());
                            obj.addChild(child);
                        }
                    }
                    list.add(obj);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void downloadTroopObj(final Context context,
                                        final TroophandleListener callback) {
        String url = UrlHandle.getMmTree() + "?user_id="
                + UserObjHandle.getUsetId(context);

        RequestParams params = HttpUtilsBox.getRequestParams();

        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        callback.onFailure(exception);
                        ShowMessage.showFailure(context);

                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("", result);
                        JSONObject json = JsonHandle.getJSON(result);
                        if (json != null) {
                            JSONArray array = JsonHandle.getArray(json, "data");
                            if (array != null) {
                                List<TroopObj> list = TroopObjHandler
                                        .getTroopObjList(array);
                                callback.onSuccess(list);
                            }
                        }
                    }

                });
    }

    public static void saveTroopObj(Context context, TroopObj mTroopObj) {
        try {
            DBHandler.getDbUtils(context).saveOrUpdate(mTroopObj);
            if (mTroopObj.getMu_gs() != null
                    && !mTroopObj.getMu_gs().equals("null")
                    && !mTroopObj.getMu_gs().equals("")) {
                saveTroopMu_gs(context, mTroopObj.getMu_gs());
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void saveTroopMu_gs(Context context, String mu_gs) {
        String str = getTroopMugs(context);
        boolean b = false;
        if (!mu_gs.equals("")) {
            if (str.equals("")) {
                b = true;
                str = mu_gs;
            } else {
                List<String> list = getTroopMugsList(context);
                if (!list.contains(mu_gs)) {
                    b = true;
                    str = str + "|" + mu_gs;
                }
            }
        }
        if (b) {
            SystemHandle.saveStringMessage(context, "mu_gs", str);
        }

    }

    public static List<String> getTroopMugsList(Context context) {
        List<String> list = new ArrayList<String>();
        String str = SystemHandle.getString(context, "mu_gs");
        String[] strs = str.split("\\|");
        for (String s : strs) {
            list.add(s);
        }
        return list;
    }

    private static String getTroopMugs(Context context) {
        return SystemHandle.getString(context, "mu_gs");
    }

    public static void deleteTroopObj(Context context, TroopObj mTroopObj,
                                      CallbackForBoolean callback) {
        mTroopObj.setShow(false);
        saveTroopObj(context, mTroopObj);
        if (callback != null) {
            callback.callback(true);
        }
    }

    public static List<TroopObj> getTroopObjList(JSONArray array) {
        List<TroopObj> list = new ArrayList<TroopObj>();
        for (int i = 0; i < array.length(); i++) {
            list.add(getTroopObj(JsonHandle.getJSON(array, i)));
        }
        return list;
    }

    private static TroopObj getTroopObj(JSONObject json) {
        TroopObj obj = new TroopObj();

        obj.setMu_contact(JsonHandle.getString(json, "mu_contact"));
        obj.setMu_createTime(JsonHandle.getLong(json, "mu_createTime"));
        obj.setMu_desc(JsonHandle.getString(json, "mu_desc"));
        obj.setMu_from(JsonHandle.getString(json, "mu_from"));
        obj.setMu_gf_max(JsonHandle.getString(json, "mu_gf_max"));
        obj.setMu_gf_min(JsonHandle.getString(json, "mu_gf_min"));
        obj.setMu_id(JsonHandle.getString(json, "mu_id"));
        obj.setMu_j_max(JsonHandle.getString(json, "mu_j_max"));
        obj.setMu_j_min(JsonHandle.getString(json, "mu_j_min"));
        obj.setMu_jz_time(JsonHandle.getString(json, "mu_jz_time"));
        obj.setMu_lastUpdateTime(JsonHandle.getLong(json, "mu_lastUpdateTime"));
        obj.setMu_latitude(JsonHandle.getDouble(json, "mu_coordinate_lat"));
        obj.setMu_longitude(JsonHandle.getDouble(json, "mu_coordinate_long"));
        obj.setMu_name(JsonHandle.getString(json, "mu_name"));
        obj.setMu_phone_1(JsonHandle.getString(json, "mu_phone_1"));
        obj.setMu_phone_2(JsonHandle.getString(json, "mu_phone_2"));
        obj.setMu_photo(JsonHandle.getString(json, "mu_photo"));
        obj.setMu_price(JsonHandle.getString(json, "mu_price"));
        obj.setMu_sz_type(JsonHandle.getString(json, "mu_sz_type"));
        obj.setMu_total(JsonHandle.getString(json, "mu_total"));
        obj.setMu_type(JsonHandle.getString(json, "mu_type"));
        obj.setMu_zb(JsonHandle.getString(json, "mu_zb"));
        obj.setMu_zg_max(JsonHandle.getString(json, "mu_zg_max"));
        obj.setMu_zg_min(JsonHandle.getString(json, "mu_zg_min"));
        obj.setMu_gs(JsonHandle.getString(json, "mu_gs"));

        JSONArray photoArray = JsonHandle.getArray(json, "mmPhoto");
        if (photoArray != null) {
            obj.setCameraPicObjList(CameraPicObjHandler
                    .getCameraPicObjList(photoArray));
        }

        return obj;
    }

    public static void deleteOnlineTroopObj(final Context context,
                                            List<TroopObj> choiceList, final TroophandleListener callback) {
        StringBuffer key = new StringBuffer();
        for (TroopObj obj : choiceList) {
            key.append(obj.getMu_id());
            key.append("%7C");
        }
        String url = UrlHandle.getDeleteTroop(key.toString().substring(0,
                key.length() - 3))
                + "?user_id=" + UserObjHandle.getUsetId(context);

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

}
