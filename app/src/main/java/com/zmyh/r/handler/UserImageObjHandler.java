package com.zmyh.r.handler;

import com.zmyh.r.box.UserImageObj;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hua on 15/7/15.
 */
public class UserImageObjHandler {

    public static List<UserImageObj> getUserImageObjList(JSONArray array){
        List<UserImageObj> list=new ArrayList<UserImageObj>();
        for(int i=0;i<array.length();i++){
            list.add(getUserImageObj(JsonHandle.getJSON(array,i)));
        }
        return list;
    }

    public static UserImageObj getUserImageObj(JSONObject json){
        UserImageObj obj=new UserImageObj();

        obj.setCreateAt(JsonHandle.getLong(json,UserImageObj.CREATE_AT));
        obj.setImg(JsonHandle.getString(json,UserImageObj.IMG));
        obj.setId(JsonHandle.getString(json,UserImageObj.ID));

        return obj;
    }

}
