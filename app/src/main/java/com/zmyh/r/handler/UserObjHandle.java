package com.zmyh.r.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.zmyh.r.box.CityObj;
import com.zmyh.r.box.UserObj;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.main.user.LoginActivity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class UserObjHandle {

    private final static String TAP = "user";

    public static List<UserObj> getUserObjList(JSONArray array) {
        List<UserObj> list = new ArrayList<UserObj>();
        for (int i = 0; i < array.length(); i++) {
            list.add(getUserBox(JsonHandle.getJSON(array, i)));
        }
        return list;
    }

    public static UserObj getUserBox(JSONObject json) {
        UserObj u = new UserObj();

        u.setCreateAt(JsonHandle.getLong(json, UserObj.CREATE_AT));
        u.setId(JsonHandle.getString(json, UserObj.ID));
        u.setM_lastLogin(JsonHandle.getLong(json, UserObj.M_LAST_LOGIN));
        u.setM_mobile(JsonHandle.getString(json, UserObj.M_MOBILE));
        u.setM_password(JsonHandle.getString(json, UserObj.M_PASSWORD));
        u.setV(JsonHandle.getString(json, UserObj.V));
        u.setM_avatar(JsonHandle.getString(json, UserObj.M_AVATAR));
        u.setM_CallingCard(JsonHandle.getString(json, UserObj.M_CALLINGCARD));
        u.setM_nick_name(JsonHandle.getString(json, UserObj.M_NICK_NAME));
        u.setM_description(JsonHandle.getString(json, UserObj.M_DESCRIPTION));
        u.setIsBlock(JsonHandle.getInt(json, UserObj.IS_BLOCK));
        u.setIsFriend(JsonHandle.getInt(json, UserObj.IS_FRIEND));

        JSONObject mContactJson = JsonHandle.getJSON(json, "m_contact");
        if (mContactJson != null) {
            u.setCompany(JsonHandle.getString(mContactJson, "company"));
            u.setQq(JsonHandle.getString(mContactJson, "qq"));
            u.setEmail(JsonHandle.getString(mContactJson, "email"));
        }

        JSONObject mmAreaJson = JsonHandle.getJSON(json, "mmArea");
        if (mmAreaJson != null) {
            CityObj co = CityObjHandler.getCityObj(mmAreaJson);
            u.setMmArea(co);
        }

        JSONArray mTagArray = JsonHandle.getArray(json, "m_tag");
        if (mTagArray != null) {
            if (mTagArray.length() > 0) {
                String[] tags = new String[mTagArray.length()];
                for (int i = 0; i < mTagArray.length(); i++) {
                    tags[i] = JsonHandle.getString(mTagArray, i);
                }
                u.setM_tag(tags);
            }
        }

        JSONArray mAuthTagArray = JsonHandle.getArray(json, "m_auth_tag");
        if (mAuthTagArray != null) {
            if (mAuthTagArray.length() > 0) {
                u.setM_auth_tag(JsonHandle.getString(mAuthTagArray, 0));
            }
        }

        return u;
    }

    public static void logout(Context context) {
        saveUsetId(context, "");
        saveM_mobile(context, "");
        saveM_password(context, "");
        saveUsetV(context, "");
    }

    public static void savaUser(Context context, UserObj obj) {
        saveUsetId(context, obj.getId());
        saveCreateAt(context, obj.getCreateAt());
        saveM_lastLogin(context, obj.getM_lastLogin());
        saveM_mobile(context, obj.getM_mobile());
        saveM_password(context, obj.getM_password());
        saveUsetV(context, obj.getV());
        saveM_avatar(context, obj.getM_avatar());
        saveM_auth_tag(context, obj.getM_auth_tag());
        saveM_tap(context, obj.getM_tag());
        saveM_nick_name(context, obj.getM_nick_name());
        saveM_description(context, obj.getM_description());
        saveEmail(context, obj.getEmail());
        saveQQ(context, obj.getQq());
        saveCompany(context, obj.getCompany());
        saveM_CallingCard(context, obj.getM_CallingCard());
        CityObjHandler.saveCityObj(context, obj.getMmArea());
    }

    public static String getM_CallingCard(Context context) {
        return SystemHandle.getString(context, TAP + UserObj.M_CALLINGCARD);
    }

    private static void saveM_CallingCard(Context context, String m_CallingCard) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.M_CALLINGCARD,
                m_CallingCard);
    }

    public static String getCompany(Context context) {
        return SystemHandle.getString(context, TAP + UserObj.COMPANY);
    }

    private static void saveCompany(Context context, String company) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.COMPANY, company);
    }

    public static String getQQ(Context context) {
        return SystemHandle.getString(context, TAP + UserObj.QQ);
    }

    private static void saveQQ(Context context, String qq) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.QQ, qq);
    }

    public static String getEmail(Context context) {
        return SystemHandle.getString(context, TAP + UserObj.EMAIL);
    }

    private static void saveEmail(Context context, String email) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.EMAIL, email);
    }

    public static String getM_description(Context context) {
        return SystemHandle.getString(context, TAP + UserObj.M_DESCRIPTION);
    }

    private static void saveM_description(Context context, String m_description) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.M_DESCRIPTION,
                m_description);
    }

    public static String getM_auth_tag(Context context) {
        return SystemHandle.getString(context, TAP + UserObj.M_AUTH_TAG);
    }

    private static void saveM_auth_tag(Context context, String m_auth_tag) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.M_AUTH_TAG,
                m_auth_tag);
    }

    public static String getM_tap(Context context) {
        return SystemHandle.getString(context, TAP + UserObj.M_TAG);
    }

    private static void saveM_tap(Context context, String[] m_tap) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < m_tap.length; i++) {
            sb.append(m_tap[i]);
            if (i < m_tap.length - 1) {
                sb.append(",");
            }
        }
        SystemHandle.saveStringMessage(context, TAP + UserObj.M_TAG,
                sb.toString());
    }

    public static String getM_nick_name(Context context) {
        String name = SystemHandle
                .getString(context, TAP + UserObj.M_NICK_NAME);
        if (!name.equals("")) {
            return name;
        }
        return getM_mobile(context);
    }

    private static void saveM_nick_name(Context context, String m_nick_name) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.M_NICK_NAME,
                m_nick_name);
    }

    public static String getM_avatar(Context context) {
        return SystemHandle.getString(context, TAP + UserObj.M_AVATAR);
    }

    private static void saveM_avatar(Context context, String m_avatar) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.M_AVATAR,
                m_avatar);
    }

    private static void saveUsetV(Context context, String v) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.V, v);
    }

    private static void saveM_password(Context context, String m_password) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.M_PASSWORD,
                m_password);
    }

    private static void saveM_mobile(Context context, String m_mobile) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.M_MOBILE,
                m_mobile);
    }

    private static void saveM_lastLogin(Context context, long m_lastLogin) {
        SystemHandle.saveLongMessage(context, TAP + UserObj.M_LAST_LOGIN,
                m_lastLogin);
    }

    private static void saveCreateAt(Context context, long createAt) {
        SystemHandle
                .saveLongMessage(context, TAP + UserObj.CREATE_AT, createAt);
    }

    private static void saveUsetId(Context context, String id) {
        SystemHandle.saveStringMessage(context, TAP + UserObj.ID, id);
    }

    public static String getUsetId(Context context) {
        return SystemHandle.getString(context, TAP + UserObj.ID);
    }

    public static String getM_mobile(Context context) {
        return SystemHandle.getString(context, TAP + UserObj.M_MOBILE);
    }

    public static boolean isLogin(Context context) {
        return isLogin(context, false);
    }

    public static boolean isLogin(final Context context, boolean isShow) {
        if (!getUsetId(context).equals("")) {
            return true;
        }
        if (isShow) {
//			ShowMessage.showToast(context, "请先登录");
            MessageDialog dialog = new MessageDialog(context);
            dialog.setMessage("请先登录");
            dialog.setCancelStyle("好的");
            dialog.setCancelListener(null);
            dialog.setCommitStyle("登录");
            dialog.setCommitListener(new MessageDialog.CallBackListener() {
                @Override
                public void callback() {
                    Passageway.jumpActivity(context, LoginActivity.class);
                }
            });
        }
        return false;
    }

}
