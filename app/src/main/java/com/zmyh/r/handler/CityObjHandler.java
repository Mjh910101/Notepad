package com.zmyh.r.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.zmyh.r.box.CityObj;
import com.zmyh.r.tool.ShowMessage;

public class CityObjHandler {

    public static List<CityObj> getCityObjList(JSONArray array) {
        List<CityObj> list = new ArrayList<CityObj>();
        for (int i = 0; i < array.length(); i++) {
            list.add(getCityObj(JsonHandle.getJSON(array, i)));
        }
        return list;

    }

    public static CityObj getCityObj(JSONObject json) {
        CityObj obj = new CityObj();

        obj.set__v(JsonHandle.getInt(json, CityObj.V));
        obj.set_id(JsonHandle.getString(json, CityObj.ID));
        obj.setArea_code(JsonHandle.getString(json, CityObj.AREA_CODE));
        obj.setArea_level(JsonHandle.getInt(json, CityObj.AREA_LEVEL));
        obj.setArea_name(JsonHandle.getString(json, CityObj.AREA_NAME));
        obj.setCreateAt(JsonHandle.getLong(json, CityObj.CREATE_AT));
        obj.setArea_lat(JsonHandle.getDouble(json, CityObj.AREA_LAT));
        obj.setArea_lng(JsonHandle.getDouble(json, CityObj.AREA_LNG));

        JSONArray cities = JsonHandle.getArray(json, CityObj.CITIES);
        if (cities != null && obj.getArea_level() == 0) {
            obj.setCities(getCityObjList(cities));
        }

        return obj;
    }

    public static void saveCityObj(Context context, CityObj obj) {
        saveCityObj(context, obj, null, null);
    }

    public static void saveCityObj(Context context, CityObj obj,
                                   String area_name, String area_code) {
        if (area_name != null && area_code != null) {
            saveAreaCode(context, area_code);
            saveAreaName(context, area_name);
        }
        if (obj != null) {
            saveCityCode(context, obj.getArea_code());
            saveCityName(context, obj.getArea_name());
            saveCityId(context, obj.get_id());
            saveCityMap(context, obj.getArea_lat(), obj.getArea_lng());
        }

    }

    public static double getCityLat(Context context) {
        double d = 0;
        String str = SystemHandle.getString(context, "area_lat");
        try {
            d = Double.valueOf(str);
        } catch (Exception e) {
        }
        return d;
    }

    public static double getCityLng(Context context) {
        double d = 0;
        String str = SystemHandle.getString(context, "area_lng");
        try {
            d = Double.valueOf(str);
        } catch (Exception e) {
        }
        return d;
    }

    private static void saveCityMap(Context context, double area_lat,
                                    double area_lng) {
        SystemHandle.saveStringMessage(context, "area_lat",
                String.valueOf(area_lat));
        SystemHandle.saveStringMessage(context, "area_lng",
                String.valueOf(area_lng));
    }

    public static void saveAreaCode(Context context, String area_code) {
        SystemHandle.saveStringMessage(context, "area_code", area_code);
    }

    public static void saveAreaName(Context context, String area_name) {
        SystemHandle.saveStringMessage(context, "area_name", area_name);
    }

    public static void saveCityCode(Context context, String city_code) {
        SystemHandle.saveStringMessage(context, "city_code", city_code);
    }

    public static void saveCityName(Context context, String city_name) {
        SystemHandle.saveStringMessage(context, "city_name", city_name);
    }

    private static void saveCityId(Context context, String id) {
        SystemHandle.saveStringMessage(context, "city_id", id);
    }

    public static String getAreaCode(Context context) {
        return SystemHandle.getString(context, "area_code");
    }

    public static String getAreaName(Context context) {
        return SystemHandle.getString(context, "area_name");
    }

    public static String getCityCode(Context context) {
        return SystemHandle.getString(context, "city_code");
    }

    public static String getCityName(Context context) {
        String str = SystemHandle.getString(context, "city_name");
        if (str.equals("")) {
            return "城市";
        }
        return str;
    }

    public static String getCityId(Context context) {
        return getCityId(context, true);
    }

    public static String getCityId(Context context, boolean isShow) {
        String str = SystemHandle.getString(context, "city_id");
        if (str.equals("") && isShow) {
            ShowMessage.showToast(context, "请先选择你所在的城市~!");
        }
        return str;
    }

}
