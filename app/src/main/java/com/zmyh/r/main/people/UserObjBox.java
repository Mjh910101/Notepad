package com.zmyh.r.main.people;

import com.zmyh.r.box.UserObj;

/**
 * Created by Hua on 15/7/13.
 */
public class UserObjBox {

    private static UserObj mUserObj;

    public static void saveUserObj(UserObj obj){
        daleteUserObj();
        mUserObj=obj;
    }

    public static UserObj getUserObj(){
        return mUserObj;
    }

    public static void daleteUserObj() {
        if(mUserObj!=null){
            mUserObj=null;
        }
    }

}
