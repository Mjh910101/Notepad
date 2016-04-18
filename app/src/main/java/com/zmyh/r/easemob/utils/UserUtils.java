package com.zmyh.r.easemob.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zmyh.r.R;
import com.zmyh.r.box.UserObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.easemob.DemoHXSDKHelper;
import com.zmyh.r.easemob.applib.controller.HXSDKHelper;
import com.zmyh.r.easemob.domain.User;
import com.zmyh.r.handler.UserObjHandle;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     *
     * @param username
     * @return
     */
    public static User getUserInfo(String username) {
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
        if (user == null) {
            user = new User(username);
        }

//        if (user != null) {
//            //demo没有这些数据，临时填充
//            if (TextUtils.isEmpty(user.getNick()))
//                user.setNick(username);
//        }
        return user;
    }

    /**
     * 设置用户头像
     *
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView) {
        User user = getUserInfo(username);
        if (user != null && user.getAvatar() != null && !user.getAvatar().equals("") &&
                !user.getAvatar().equals("null")) {
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

    /**
     * 设置当前用户头像
     */
    public static void setCurrentUserAvatar(Context context, ImageView imageView) {
//		User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
//		if (user != null && user.getAvatar() != null) {
//			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
//		} else {
//			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
//		}
        String avatar = UserObjHandle.getM_avatar(context);
        if (imageView != null && avatar != null && !avatar.equals("")) {
            Picasso.with(context).load(avatar).placeholder(R.drawable.default_avatar).into(imageView);
        } else {
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }

    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username, TextView textView) {
        User user = getUserInfo(username);
        if (user != null) {
            textView.setText(user.getNick());
        } else {
            textView.setText(username);
        }
    }

    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(Context context, TextView textView) {
//    	User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
//    	if(textView != null){
//    		textView.setText(user.getNick());
//    	}
        if (textView != null) {
            textView.setText(UserObjHandle.getM_nick_name(context));
        }
    }

    /**
     * 保存或更新某个用户
     */
    public synchronized static void saveUserInfo(User newUser) {
        if (newUser == null || newUser.getUsername() == null) {
            return;
        }
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
    }

    public static void saveUserInfo(UserObj obj) {
        try {
            User user = new User(obj.getId());
            user.setAvatar(obj.getM_avatar());
            user.setNick(obj.getM_nick_name());
            Log.e("", user.toString());
            saveUserInfo(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
