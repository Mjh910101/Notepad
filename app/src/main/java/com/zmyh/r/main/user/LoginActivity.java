package com.zmyh.r.main.user;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.util.HanziToPinyin;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.UserObj;
import com.zmyh.r.easemob.Constant;
import com.zmyh.r.easemob.DemoApplication;
import com.zmyh.r.easemob.DemoHXSDKHelper;
import com.zmyh.r.easemob.activitys.ChatActivity;
import com.zmyh.r.easemob.applib.controller.HXSDKHelper;
import com.zmyh.r.easemob.db.InviteMessgeDao;
import com.zmyh.r.easemob.db.UserDao;
import com.zmyh.r.easemob.domain.InviteMessage;
import com.zmyh.r.easemob.domain.User;
import com.zmyh.r.handler.EMChatHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LoginActivity extends Activity {

    private Context context;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.login_progress)
    private ProgressBar progress;
    @ViewInject(R.id.login_telInput)
    private EditText telInput;
    @ViewInject(R.id.login_passwordInput)
    private EditText passwordInput;

    private UserDao userDao;
    private InviteMessgeDao inviteMessgeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        context = this;

        initActivity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isLogin();
    }

    @OnClick({R.id.title_back, R.id.login_finish, R.id.login_register,
            R.id.login_login, R.id.login_forgetPassword})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
            case R.id.login_finish:
                finish();
                break;
            case R.id.login_register:
                Passageway.jumpActivity(context, RegisterActivityV2.class);
                break;
            case R.id.login_login:
                commit();
                break;
            case R.id.login_forgetPassword:
                Passageway.jumpActivity(context, ForgetActivity.class);
                break;
        }
    }

    private void initActivity() {
        isLogin();
    }

    private void isLogin() {
        titleName.setText("登录");
        seekIcon.setVisibility(View.GONE);
        if (UserObjHandle.isLogin(context)) {
            finish();
        }
    }

    private void commit() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getLogin();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("m_mobile", telInput.getText().toString());
        params.addBodyParameter("m_password", passwordInput.getText()
                .toString());

        HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        progress.setVisibility(View.GONE);
                        ShowMessage.showFailure(context);
                        Log.e("msg", msg);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(result);
                        if (json != null) {
                            JSONObject authJson = JsonHandle.getJSON(json,
                                    "auth");
                            if (authJson != null) {
                                UserObj user = UserObjHandle
                                        .getUserBox(authJson);
                                UserObjHandle.savaUser(context, user);
                                loginEMChat();
                            } else {
                                String msg = JsonHandle.getString(json,
                                        "message");
                                ShowMessage.showToast(context, msg);
                            }

                        }
                    }
                });
    }

    private void loginEMChatInActivity() {
        progress.setVisibility(View.VISIBLE);
        final String user_id = UserObjHandle.getUsetId(context);
        EMChatManager.getInstance().login(user_id, user_id, new EMCallBack() {

            @Override
            public void onSuccess() {
                // 登陆成功，保存用户名密码
                DemoApplication.getInstance().setUserName(user_id);
                DemoApplication.getInstance().setPassword(user_id);

                try {
                    // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                    // ** manually load all local groups and
                    EMGroupManager.getInstance().loadAllGroups();
                    EMChatManager.getInstance().loadAllConversations();
                    // 处理好友和群组
                    initializeContacts();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 取好友或者群聊失败，不让进入主页面
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progress.setVisibility(View.GONE);
                            DemoHXSDKHelper.getInstance().logout(true, null);
                            Toast.makeText(getApplicationContext(), R.string.login_failure_failed, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
                boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
                        DemoApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }
                progress.setVisibility(View.GONE);
                // 进入主页面
//                Intent intent = new Intent(LoginActivity.this,
//                        MainActivity.class);
//                startActivity(intent);

                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Deprecated
    private void loginEMChat() {
        progress.setVisibility(View.VISIBLE);
        EMChatHandler.loginEMChat(this, new EMChatHandler.EMChatLoginListener() {
            @Override
            public void onError(int code, String message) {
                if (code == EMError.INVALID_PASSWORD_USERNAME) {
                    registerEMChat();
                }
            }

            @Override
            public void callback(boolean b) {
                if (b) {
                    progress.setVisibility(View.GONE);
                    initEMChat();
                    initializeContacts();
                    finish();
                }
            }
        });
    }

    private void registerEMChat() {
        final String user_id = UserObjHandle.getUsetId(context);
        EMChatHandler.registerEMChat(this, user_id, new EMChatHandler.EMChatRegisterListener() {
            @Override
            public void onError(int errorCode) {

            }

            @Override
            public void callback(boolean b) {
                if (b) {
                    loginEMChat();
                }
            }
        });
    }

    private void initEMChat() {
        userDao = new UserDao(this);
        inviteMessgeDao = new InviteMessgeDao(this);
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

        if (processAppName == null || !processAppName.equalsIgnoreCase("com.zmyh.r")) {
            Log.e("", "enter the service process!");
            //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
//        EMChat.getInstance().init(context);
//        EMChat.getInstance().setDebugMode(false);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题

        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(new MyContactListener());

        // 注册一个监听连接状态的listener
//        connectionListener = new MyConnectionListener();
//        EMChatManager.getInstance().addConnectionListener(connectionListener);

//        groupChangeListener = new MyGroupChangeListener();
        // 注册群聊相关的listener
//        EMGroupManager.getInstance().addGroupChangeListener(groupChangeListener);

        //异步获取当前用户的昵称和头像
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().asyncGetCurrentUserInfo(context);

        // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
        EMChatManager.getInstance().updateCurrentUserNick(
                UserObjHandle.getM_nick_name(context).trim());
    }

    private void initializeContacts() {
        Map<String, User> userlist = new HashMap<String, User>();
        // 添加user"申请与通知"
        User newFriends = new User();
        newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
        String strChat = getResources().getString(
                R.string.Application_and_notify);
        newFriends.setNick(strChat);

        userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
        // 添加"群聊"
        User groupUser = new User();
        String strGroup = getResources().getString(R.string.group_chat);
        groupUser.setUsername(Constant.GROUP_USERNAME);
        groupUser.setNick(strGroup);
        groupUser.setHeader("");
        userlist.put(Constant.GROUP_USERNAME, groupUser);

        // 添加"Robot"
        User robotUser = new User();
        String strRobot = getResources().getString(R.string.robot_chat);
        robotUser.setUsername(Constant.CHAT_ROBOT);
        robotUser.setNick(strRobot);
        robotUser.setHeader("");
        userlist.put(Constant.CHAT_ROBOT, robotUser);

        // 存入内存
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
        // 存入db
        UserDao dao = new UserDao(this);
        List<User> users = new ArrayList<User>(userlist.values());
        dao.saveContactList(users);
    }

    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    User setUserHead(String username) {
        User user = new User();
        user.setUsername(username);
        String headerName = null;
        if (!TextUtils.isEmpty(user.getNick())) {
            headerName = user.getNick();
        } else {
            headerName = user.getUsername();
        }
        if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
            user.setHeader("");
        } else if (Character.isDigit(headerName.charAt(0))) {
            user.setHeader("#");
        } else {
            user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1)
                    .toUpperCase());
            char header = user.getHeader().toLowerCase().charAt(0);
            if (header < 'a' || header > 'z') {
                user.setHeader("#");
            }
        }
        return user;
    }

    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(List<String> usernameList) {
            // 保存增加的联系人
            Map<String, User> localUsers = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
            Map<String, User> toAddUsers = new HashMap<String, User>();
            for (String username : usernameList) {
                User user = setUserHead(username);
                // 添加好友时可能会回调added方法两次
                if (!localUsers.containsKey(username)) {
                    userDao.saveContact(user);
                }
                toAddUsers.put(username, user);
            }
            localUsers.putAll(toAddUsers);
            // 刷新ui

        }

        @Override
        public void onContactDeleted(final List<String> usernameList) {
            // 被删除
            Map<String, User> localUsers = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList();
            for (String username : usernameList) {
                localUsers.remove(username);
                userDao.deleteContact(username);
                inviteMessgeDao.deleteMessage(username);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    // 如果正在与此用户的聊天页面
                    String st10 = getResources().getString(R.string.have_you_removed);
                    if (ChatActivity.activityInstance != null
                            && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
                        Toast.makeText(LoginActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
                                .show();
                        ChatActivity.activityInstance.finish();
                    }
                }
            });

        }

        @Override
        public void onContactInvited(String username, String reason) {

            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            Log.d("", username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);

        }

        @Override
        public void onContactAgreed(String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            Log.d("", username + "同意了你的好友请求");
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);

        }

        @Override
        public void onContactRefused(String username) {

            // 参考同意，被邀请实现此功能,demo未实现
            Log.d(username, username + "拒绝了你的好友请求");
        }

    }

}
