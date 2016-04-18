package com.zmyh.r.handler;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.zmyh.r.easemob.DemoApplication;
import com.zmyh.r.interfaces.CallbackForBoolean;

/**
 * *
 * * ┏┓      ┏┓
 * *┏┛┻━━━━━━┛┻┓
 * *┃          ┃
 * *┃          ┃
 * *┃ ┳┛   ┗┳  ┃
 * *┃          ┃
 * *┃    ┻     ┃
 * *┃          ┃
 * *┗━┓      ┏━┛
 * *  ┃      ┃
 * *  ┃      ┃
 * *  ┃      ┗━━━┓
 * *  ┃          ┣┓
 * *  ┃         ┏┛
 * *  ┗┓┓┏━━━┳┓┏┛
 * *   ┃┫┫   ┃┫┫
 * *   ┗┻┛   ┗┻┛
 * Created by Hua on 15/8/27.
 */
public class EMChatHandler {

    public interface EMChatLoginListener extends CallbackForBoolean {
        void onError(int code, String message);
    }

    public static void loginEMChat(final Activity activity, final EMChatLoginListener callback) {

        if (UserObjHandle.isLogin(activity)) {
            final String user_id = UserObjHandle.getUsetId(activity);
            Log.e("user_id", user_id);
            EMChatManager.getInstance().login(user_id, user_id, new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            DemoApplication.getInstance().setUserName(user_id);
                            DemoApplication.getInstance().setPassword(user_id);
                            Log.d("main", "登陆聊天服务器成功！");
                            EMGroupManager.getInstance().loadAllGroups();
                            EMChatManager.getInstance().loadAllConversations();
                            if (callback != null) {
                                callback.callback(true);
                            }
                        }
                    });
                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(int code, String message) {
                    Log.d("main", "登陆聊天服务器失败！" + " code : " + code + "  message : " + message);
                    if (callback != null) {
                        callback.callback(false);
                        callback.onError(code, message);
                    }
                }
            });

        }

    }

    public interface EMChatRegisterListener extends CallbackForBoolean {
        void onError(int errorCode);
    }

    public static void registerEMChat(final Activity activity, final String user_id, final EMChatRegisterListener callback) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 调用sdk注册方法
                    EMChatManager.getInstance().createAccountOnServer(user_id, user_id);
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (callback != null) {
                                callback.callback(true);
                            }
                        }
                    });
                } catch (final EaseMobException e) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            int errorCode = e.getErrorCode();
                            if (errorCode == EMError.NONETWORK_ERROR) {
                                Toast.makeText(activity, "网络异常，请检查网络！", Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                Toast.makeText(activity, "用户已存在！", Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.UNAUTHORIZED) {
                                Toast.makeText(activity, "注册失败，无权限！", Toast.LENGTH_SHORT).show();
                            } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                                Toast.makeText(activity, "非法用户名", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "注册失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            if (callback != null) {
                                callback.callback(false);
                                callback.onError(errorCode);
                            }
                        }
                    });
                }
            }
        }).start();
    }

}
