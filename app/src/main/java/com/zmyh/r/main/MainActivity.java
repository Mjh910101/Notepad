package com.zmyh.r.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMGroupChangeListener;
import com.easemob.EMNotifierEvent;
import com.easemob.EMValueCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.box.VersionObj;
import com.zmyh.r.city.AreaActivity;
import com.zmyh.r.dailog.ListDialog;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.dailog.MessageDialog.CallBackListener;
import com.zmyh.r.dao.DBHandler;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.download.DownloadNewAppService;
import com.zmyh.r.easemob.Constant;
import com.zmyh.r.easemob.DemoApplication;
import com.zmyh.r.easemob.DemoHXSDKHelper;
import com.zmyh.r.easemob.activitys.ChatActivity;
import com.zmyh.r.easemob.activitys.ChatAllHistoryFragment;
import com.zmyh.r.easemob.applib.controller.HXSDKHelper;
import com.zmyh.r.easemob.db.InviteMessgeDao;
import com.zmyh.r.easemob.db.UserDao;
import com.zmyh.r.easemob.domain.InviteMessage;
import com.zmyh.r.easemob.domain.User;
import com.zmyh.r.easemob.utils.CommonUtils;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.DynamicObjHandler;
import com.zmyh.r.handler.EMChatHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.MsgObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.VersionObjHandler;
import com.zmyh.r.handler.WeiXinHandler;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.interfaces.CallbackForInteger;
import com.zmyh.r.main.dynamic.DyanmaicFrameLayout;
import com.zmyh.r.main.forum.ForumFrameLayoutV2;
import com.zmyh.r.main.forum.ShareForumActivity;
import com.zmyh.r.main.people.PeopleFrameLayout;
import com.zmyh.r.main.people.TagActivity;
import com.zmyh.r.main.server.ServerFrameLayoutV3;
import com.zmyh.r.main.server.ServerFrameLayoutV4;
import com.zmyh.r.main.user.LoginActivity;
import com.zmyh.r.main.user.UserFrameLayout;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

import org.json.JSONObject;
import org.w3c.dom.DOMErrorHandler;

import cn.smssdk.SMSSDK;

public class MainActivity extends Activity implements EMEventListener {

    private final static long EXITTIME = 2000;
    private long EXIT = 0;

    public final static int SERVER = 11;
    public final static int FORUM = 12;
    public final static int PEOPLE = 13;
    public final static int USER = 14;
    public final static int MESSAGE = 15;

    private Context context;

    private int now_tap;
    private int userMessageSum = -1;

    @ViewInject(R.id.main_content)
    private FrameLayout content;
    @ViewInject(R.id.main_tap_muServer_icon)
    private ImageView muServerIcon;
    @ViewInject(R.id.main_tap_muServer_text)
    private TextView muServerText;
    @ViewInject(R.id.main_tap_muForum_icon)
    private ImageView muForumIcon;
    @ViewInject(R.id.main_tap_muForum_text)
    private TextView muForumText;
    @ViewInject(R.id.main_tap_muForum_hint)
    private ImageView muForumHint;
    @ViewInject(R.id.main_tap_muPeople_icon)
    private ImageView muPeopleIcon;
    @ViewInject(R.id.main_tap_muPeople_text)
    private TextView muPeopleText;
    @ViewInject(R.id.main_tap_muMessage_icon)
    private ImageView muMessageIcon;
    @ViewInject(R.id.main_tap_muMessage_text)
    private TextView muMessageText;
    @ViewInject(R.id.main_tap_user_icon)
    private ImageView userIcon;
    @ViewInject(R.id.main_tap_user_text)
    private TextView userText;
    @ViewInject(R.id.main_title_city)
    private TextView cityText;
    @ViewInject(R.id.main_title_name)
    private TextView titleName;
    @ViewInject(R.id.main_tap_muMessage_num)
    private TextView muMessageNum;
    @ViewInject(R.id.main_tap_user_hint)
    private ImageView userHint;

    private FragmentManager fragmentManager;

    private ServerFrameLayoutV4 mServerFrameLayout;
    private ForumFrameLayoutV2 mForumFrameLayout;
    private PeopleFrameLayout mPeopleFrameLayout;
    private ChatAllHistoryFragment mChatAllHistoryFragment;
    private UserFrameLayout mUserFrameLayout;

    // 账号在别处登录
    public boolean isConflict = false;
    // 账号被移除
    private boolean isCurrentAccountRemoved = false;
    private UserDao userDao;
    private InviteMessgeDao inviteMessgeDao;

    private android.app.AlertDialog.Builder conflictBuilder;
    private android.app.AlertDialog.Builder accountRemovedBuilder;
    private boolean isConflictDialogShow;
    private boolean isAccountRemovedDialogShow;
    private BroadcastReceiver internalDebugReceiver;


    private MyConnectionListener connectionListener = null;
    private MyGroupChangeListener groupChangeListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        examineCity();
        runUploadNewMessage();
        isUpload();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setCityText();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (System.currentTimeMillis() - EXIT < EXITTIME) {
                finish();
            } else {
                ShowMessage.showToast(context, "再次点击退出");
            }
            EXIT = System.currentTimeMillis();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case AreaActivity.RequestCode:
                    if (data != null) {
                        Bundle b = data.getExtras();
                        if (b.getBoolean("ok")) {
                            switch (now_tap) {
                                case FORUM:
                                    mForumFrameLayout.setCityData(
                                            b.getString("Area_name"),
                                            b.getString("Area_id"));
                                    break;
                                case MESSAGE:
//                                    mDyanmaicFrameLayout.setCityData(
//                                            b.getString("Area_name"),
//                                            b.getString("Area_id"));
                                    break;
                                case PEOPLE:
                                    mPeopleFrameLayout.setCityData(
                                            b.getString("Area_name"),
                                            b.getString("Area_id"));
                                    break;
                            }
                        }
                    }
                    break;
                case TagActivity.RC:
                    Bundle b = data.getExtras();
                    if (b != null) {
                        if (now_tap == PEOPLE) {
                            mPeopleFrameLayout.setTagText(b
                                    .getString(TagActivity.NOW_TAG));
                        }
                    }
                    break;
                case ShareForumActivity.resultCode:
                    if (now_tap == FORUM) {
                        mForumFrameLayout.uploadData();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isConflict && !isCurrentAccountRemoved) {
            updateUnreadLabel();
            updateUnreadAddressLable();
            EMChatManager.getInstance().activityResumed();
        }

        // unregister this event listener when this activity enters the
        // background
        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.pushActivity(this);

        // register the event listener when enter the foreground
        EMChatManager.getInstance().registerEventListener(this,
                new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});
    }

    @Override
    protected void onStop() {
        EMChatManager.getInstance().unregisterEventListener(this);
        DemoHXSDKHelper sdkHelper = (DemoHXSDKHelper) DemoHXSDKHelper.getInstance();
        sdkHelper.popActivity(this);

        super.onStop();
    }

    /**
     * 监听事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: // 普通消息
            {
                EMMessage message = (EMMessage) event.getData();

                // 提示新消息
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);

                refreshUI();
                break;
            }

            case EventOfflineMessage: {
                refreshUI();
                break;
            }

            case EventConversationListChanged: {
                refreshUI();
                break;
            }

            default:
                break;
        }
    }

    @OnClick({R.id.main_tap_muServer, R.id.main_tap_muForum,
            R.id.main_tap_muPeople, R.id.main_tap_muMessage,
            R.id.main_tap_user, R.id.main_title_city, R.id.main_title_more})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_tap_muServer:
                setTabSelection(SERVER);
                break;
            case R.id.main_tap_muForum:
                setTabSelection(FORUM);
                break;
            case R.id.main_tap_muPeople:
                setTabSelection(PEOPLE);
                break;
            case R.id.main_tap_muMessage:
                setTabSelection(MESSAGE);
                break;
            case R.id.main_tap_user:
                setTabSelection(USER);
                break;
            case R.id.main_title_city:
                Passageway.jumpActivity(context, AreaActivity.class);
                break;
            case R.id.main_title_more:
                showMoreTool();
                break;
        }
    }

    private void initActivity() {
        SMSSDK.initSDK(context, "8edd8354cca0",
                "6c7c9393f8cccc5e3f0b09bf90770779");
        loginFMChat();
        fragmentManager = getFragmentManager();
        DownloadImageLoader.initLoader(context);

        Bundle b = getIntent().getExtras();
        if (b != null && b.getInt("form") == MESSAGE) {
            setTabSelection(MESSAGE);
        } else {
            setTabSelection(SERVER);
        }
        // initDB();
    }

    private void refreshUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                Log.e("refreshUI", "refreshUI : ！！！！！！！！！！！！！！！！！！！！");
                updateUnreadLabel();
                if (now_tap == MESSAGE) {
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if (mChatAllHistoryFragment != null) {
                        mChatAllHistoryFragment.refresh();
                    }
                }
            }
        });
    }

    public void updateUnreadLabel() {
        if (UserObjHandle.isLogin(context)) {
            int count = getUnreadMsgCountTotal();
            Log.e("updateUnreadLabel", "updateUnreadLabel : " + String.valueOf(count));
            if (count > 0) {
                muMessageNum.setText(String.valueOf(count));
                muMessageNum.setVisibility(View.VISIBLE);
            } else {
                muMessageNum.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
        for (EMConversation conversation : EMChatManager.getInstance().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
    }

    private void loginFMChat() {

        if (UserObjHandle.isLogin(context)) {

            if (getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow) {
                showConflictDialog();
            } else if (getIntent().getBooleanExtra(Constant.ACCOUNT_REMOVED, false) && !isAccountRemovedDialogShow) {
                showAccountRemovedDialog();
            } else {

            }

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
                        initEMChat();
                        initializeContacts();
                    }
                }
            });
        }

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
                    loginFMChat();
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
        EMChat.getInstance().init(context);
        EMChat.getInstance().setDebugMode(false);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题

        // setContactListener监听联系人的变化等
        EMContactManager.getInstance().setContactListener(new MyContactListener());

//         注册一个监听连接状态的listener
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

    private void examineCity() {
        setCityText();
        if (CityObjHandler.getCityId(context).equals("")) {
            showCityDialog();
        }
    }

    private void showCityDialog() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setTitel("请先选择你所在的地区");
        dialog.setCommitStyle("好的");
        dialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                Passageway.jumpActivity(context, AreaActivity.class);
            }
        });
    }

    private void setCityText() {
        cityText.setText(CityObjHandler.getCityName(context) + "∨");
    }

    private void initDB() {
        Log.e("DB_TEST", "init_DB");
        try {
            DbUtils db = DBHandler.getDbUtils(context);
            List<TroopObj> troopObjList = db.findAll(TroopObj.class);
            if (troopObjList != null) {
                for (TroopObj o : troopObjList) {
                    Log.e("DB_TEST", o.toString());
                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void setTabSelection(int i) {
        now_tap = i;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        clearSelection();
        hideFragments(transaction);
        switch (i) {
            case SERVER:
                onMuServer(transaction);
                break;
            case FORUM:
                onMuForum(transaction);
                break;
            case PEOPLE:
                onMuPeople(transaction);
                break;
            case MESSAGE:
                onMuMessage(transaction);
                break;
            case USER:
                onUser(transaction);
                break;
        }
        transaction.commit();
    }

    private void onUser(FragmentTransaction transaction) {
        titleName.setText("我的账号");
        userIcon.setImageResource(R.drawable.tap_user_green_icon);
        userText.setTextColor(ColorBox.getColorForID(context, R.color.title_bg));
        userHint.setVisibility(View.GONE);
        if (userMessageSum > 0) {
            MsgObjHandler.saveMeaageSum(context, userMessageSum);
        }
        if (mUserFrameLayout == null) {
            mUserFrameLayout = new UserFrameLayout();
            transaction.add(R.id.main_content, mUserFrameLayout);
        } else {
            transaction.show(mUserFrameLayout);
        }
    }

    private void onMuPeople(FragmentTransaction transaction) {
        titleName.setText("苗木人");
        muPeopleIcon.setImageResource(R.drawable.tap_people_green_icon);
        muPeopleText.setTextColor(ColorBox
                .getColorForID(context, R.color.title_bg));
        if (mPeopleFrameLayout == null) {
            mPeopleFrameLayout = new PeopleFrameLayout();
            transaction.add(R.id.main_content, mPeopleFrameLayout);
        } else {
            transaction.show(mPeopleFrameLayout);
        }
    }

    private void onMuMessage(FragmentTransaction transaction) {
        titleName.setText("苗信");
        muMessageIcon.setImageResource(R.drawable.tap_message_green_icon);
        muMessageText.setTextColor(ColorBox.getColorForID(context,
                R.color.title_bg));
        if (UserObjHandle.isLogin(context)) {
            if (mChatAllHistoryFragment == null) {
                mChatAllHistoryFragment = new ChatAllHistoryFragment();
                transaction.add(R.id.main_content, mChatAllHistoryFragment);
            } else {
                transaction.show(mChatAllHistoryFragment);
            }
        } else {
            MessageDialog dialog = new MessageDialog(context);
            dialog.setMessage("请先登录");
            dialog.setCancelStyle("好的");
            dialog.setCancelListener(new MessageDialog.CallBackListener() {
                @Override
                public void callback() {
                    setTabSelection(MainActivity.SERVER);
                }
            });
            dialog.setCommitStyle("登录");
            dialog.setCommitListener(new MessageDialog.CallBackListener() {
                @Override
                public void callback() {
                    Passageway.jumpActivity(context, LoginActivity.class);
                    setTabSelection(MainActivity.SERVER);
                }
            });
        }
    }

    private void onMuForum(FragmentTransaction transaction) {
        titleName.setText("苗木圈");
        muForumIcon.setImageResource(R.drawable.tap_forum_green_icon);
        muForumText
                .setTextColor(ColorBox.getColorForID(context, R.color.title_bg));
        if (mForumFrameLayout == null) {
            mForumFrameLayout = new ForumFrameLayoutV2();
            transaction.add(R.id.main_content, mForumFrameLayout);
        } else {
            transaction.show(mForumFrameLayout);
        }
    }

    private void onMuServer(FragmentTransaction transaction) {
        titleName.setText("苗木供求");
        cityText.setVisibility(View.VISIBLE);
        muServerIcon.setImageResource(R.drawable.tap_server_green_icon);
        muServerText.setTextColor(ColorBox
                .getColorForID(context, R.color.title_bg));
        if (mServerFrameLayout == null) {
            mServerFrameLayout = new ServerFrameLayoutV4();
            transaction.add(R.id.main_content, mServerFrameLayout);
        } else {
            transaction.show(mServerFrameLayout);
        }
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mServerFrameLayout != null) {
            transaction.hide(mServerFrameLayout);
        }
        if (mForumFrameLayout != null) {
            transaction.hide(mForumFrameLayout);
        }
        if (mPeopleFrameLayout != null) {
            transaction.hide(mPeopleFrameLayout);
        }
        if (mUserFrameLayout != null) {
            transaction.hide(mUserFrameLayout);
        }
        if (mChatAllHistoryFragment != null) {
            transaction.hide(mChatAllHistoryFragment);
        }
    }

    private void clearSelection() {
        muServerIcon.setImageResource(R.drawable.tap_server_gray_icon);
        muForumIcon.setImageResource(R.drawable.tap_forum_gray_icon);
        muPeopleIcon.setImageResource(R.drawable.tap_people_gray_icon);
        muMessageIcon.setImageResource(R.drawable.tap_message_gray_icon);
        userIcon.setImageResource(R.drawable.tap_user_gray_icon);

        muServerText.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_04));
        muForumText.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_04));
        muPeopleText.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_04));
        muMessageText.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_04));
        userText.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_04));

        cityText.setVisibility(View.GONE);
    }

    private void showMoreTool() {
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(new String[]{"分享此APP到微信", "分享此APP到朋友圈", "二维码下载"});//, "论坛发帖"
        dialog.setItemListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int p,
                                    long arg3) {
                switch (p) {
                    case 0:
                        WeiXinHandler.shareWeiXin(context, "", "",
                                UrlHandle.getWeiXinUrl());
                        break;
                    case 1:
                        WeiXinHandler.shareFriend(context, "", "",
                                UrlHandle.getWeiXinUrl());
                        break;
                    case 2:
//                        Passageway.jumpActivity(context, ShareForumActivity.class);
                        Passageway.jumpActivity(context, CodeActivity.class);
                        break;
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * set head
     *
     * @param username
     * @return
     */
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


    /**
     * 帐号被移除的dialog
     */
    private void showAccountRemovedDialog() {
        isAccountRemovedDialogShow = true;
        UserObjHandle.logout(context);
        DemoHXSDKHelper.getInstance().logout(true, null);
        String st5 = getResources().getString(R.string.Remove_the_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (accountRemovedBuilder == null)
                    accountRemovedBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                accountRemovedBuilder.setTitle(st5);
                accountRemovedBuilder.setMessage(R.string.em_user_remove);
                accountRemovedBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        accountRemovedBuilder = null;
                        finish();
//                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                accountRemovedBuilder.setCancelable(false);
                accountRemovedBuilder.create().show();
                isCurrentAccountRemoved = true;
            } catch (Exception e) {
                EMLog.e("", "---------color userRemovedBuilder error" + e.getMessage());
            }

        }

    }

    /**
     * 显示帐号在别处登录dialog
     */
    private void showConflictDialog() {
        isConflictDialogShow = true;
        UserObjHandle.logout(context);
        DemoHXSDKHelper.getInstance().logout(false, null);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!MainActivity.this.isFinishing()) {
            // clear up global variables
            try {
                if (conflictBuilder == null)
                    conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
                conflictBuilder.setTitle(st);
                conflictBuilder.setMessage(R.string.connect_conflict);
                conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        conflictBuilder = null;
                        finish();
//                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                });
                conflictBuilder.setCancelable(false);
                conflictBuilder.create().show();
                isConflict = true;
            } catch (Exception e) {
                EMLog.e("", "---------color conflictBuilder error" + e.getMessage());
            }

        }

    }

    private boolean detection(VersionObj obj) {
        if (obj != null) {
            if (VersionObjHandler.detectionVersion(context, obj)) {
                return true;
            }
        }
        return false;
    }

    private void showMessage(final VersionObj obj) {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setTitel("发现新版本，请先更新");
        dialog.setMessage(obj.getChangelog());
        dialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                downloadApp(obj.getUpdate_url());
            }

        });
        dialog.setCancelListener(new CallBackListener() {

            @Override
            public void callback() {
//                finish();
            }
        });
    }

    private void detectionVersion(VersionObj obj) {
        if (detection(obj)) {
            showMessage(obj);
        }
    }

    public void isUpload() {
        String url = UrlHandle.getVersion();

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(result);
                        JSONObject versionJson = JsonHandle.getJSON(json,
                                "android");
                        VersionObj obj = null;
                        if (versionJson != null) {
                            obj = VersionObjHandler.getVersionObj(versionJson);
                        }
                        detectionVersion(obj);
                    }

                });
    }

    private void downloadApp(String update_url) {
        Bundle b = new Bundle();
        b.putString(DownloadNewAppService.KEY, update_url);
        Intent i = new Intent();
        i.putExtras(b);
        i.setClass(context, DownloadNewAppService.class);
        startService(i);
    }

    private void runUploadNewMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        isUploadNewMessage();
                        isUploadNewUserMessage();
                        Thread.sleep(1000 * 60);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void isUploadNewMessage() {
        DynamicObjHandler.isUploadNewMessage(context, new CallbackForBoolean() {
            @Override
            public void callback(boolean b) {
                if (b) {
                    muForumHint.setVisibility(View.VISIBLE);
                } else {
                    muForumHint.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void isUploadNewUserMessage() {
        MsgObjHandler.getMessageSize(context, new CallbackForInteger() {
            @Override
            public void callback(int i) {
                userMessageSum = i;
                if (i > MsgObjHandler.getMeaageSum(context)) {
                    userHint.setVisibility(View.VISIBLE);
                } else {
                    userHint.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 好友变化listener
     */
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
                        Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10, Toast.LENGTH_LONG)
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

    /**
     * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
     *
     * @param username
     * @param user
     */
    private static void setUserHearder(String username, User user) {
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
    }

    static void asyncFetchGroupsFromServer() {
        HXSDKHelper.getInstance().asyncFetchGroupsFromServer(new EMCallBack() {

            @Override
            public void onSuccess() {
                HXSDKHelper.getInstance().noitifyGroupSyncListeners(true);

                if (HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
                    HXSDKHelper.getInstance().notifyForRecevingEvents();
                }
            }

            @Override
            public void onError(int code, String message) {
                HXSDKHelper.getInstance().noitifyGroupSyncListeners(false);
            }

            @Override
            public void onProgress(int progress, String status) {

            }

        });
    }

    static void asyncFetchContactsFromServer() {
        HXSDKHelper.getInstance().asyncFetchContactsFromServer(new EMValueCallBack<List<String>>() {

            @Override
            public void onSuccess(List<String> usernames) {
                Context context = HXSDKHelper.getInstance().getAppContext();

                System.out.println("----------------" + usernames.toString());
                EMLog.d("roster", "contacts size: " + usernames.size());
                Map<String, User> userlist = new HashMap<String, User>();
                for (String username : usernames) {
                    User user = new User();
                    user.setUsername(username);
                    setUserHearder(username, user);
                    userlist.put(username, user);
                }
                // 添加user"申请与通知"
                User newFriends = new User();
                newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
                String strChat = context.getString(R.string.Application_and_notify);
                newFriends.setNick(strChat);

                userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
                // 添加"群聊"
                User groupUser = new User();
                String strGroup = context.getString(R.string.group_chat);
                groupUser.setUsername(Constant.GROUP_USERNAME);
                groupUser.setNick(strGroup);
                groupUser.setHeader("");
                userlist.put(Constant.GROUP_USERNAME, groupUser);

                // 添加"聊天室"
                User chatRoomItem = new User();
                String strChatRoom = context.getString(R.string.chat_room);
                chatRoomItem.setUsername(Constant.CHAT_ROOM);
                chatRoomItem.setNick(strChatRoom);
                chatRoomItem.setHeader("");
                userlist.put(Constant.CHAT_ROOM, chatRoomItem);

                // 添加"Robot"
                User robotUser = new User();
                String strRobot = context.getString(R.string.robot_chat);
                robotUser.setUsername(Constant.CHAT_ROBOT);
                robotUser.setNick(strRobot);
                robotUser.setHeader("");
                userlist.put(Constant.CHAT_ROBOT, robotUser);

                // 存入内存
                ((DemoHXSDKHelper) HXSDKHelper.getInstance()).setContactList(userlist);
                // 存入db
                UserDao dao = new UserDao(context);
                List<User> users = new ArrayList<User>(userlist.values());
                dao.saveContactList(users);

                HXSDKHelper.getInstance().notifyContactsSyncListener(true);

                if (HXSDKHelper.getInstance().isGroupsSyncedWithServer()) {
                    HXSDKHelper.getInstance().notifyForRecevingEvents();
                }

                ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().asyncFetchContactInfosFromServer(usernames, new EMValueCallBack<List<User>>() {

                    @Override
                    public void onSuccess(List<User> uList) {
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).updateContactList(uList);
                        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().notifyContactInfosSyncListener(true);
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                    }
                });
            }

            @Override
            public void onError(int error, String errorMsg) {
                HXSDKHelper.getInstance().notifyContactsSyncListener(false);
            }

        });
    }

    static void asyncFetchBlackListFromServer() {
        HXSDKHelper.getInstance().asyncFetchBlackListFromServer(new EMValueCallBack<List<String>>() {

            @Override
            public void onSuccess(List<String> value) {
                EMContactManager.getInstance().saveBlackList(value);
                HXSDKHelper.getInstance().notifyBlackListSyncListener(true);
            }

            @Override
            public void onError(int error, String errorMsg) {
                HXSDKHelper.getInstance().notifyBlackListSyncListener(false);
            }

        });
    }

    /**
     * 连接监听listener
     */
    public class MyConnectionListener implements EMConnectionListener {

        @Override
        public void onConnected() {
            boolean groupSynced = HXSDKHelper.getInstance().isGroupsSyncedWithServer();
            boolean contactSynced = HXSDKHelper.getInstance().isContactsSyncedWithServer();

            // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
            if (groupSynced && contactSynced) {
                new Thread() {
                    @Override
                    public void run() {
                        HXSDKHelper.getInstance().notifyForRecevingEvents();
                    }
                }.start();
            } else {
                if (!groupSynced) {
                    asyncFetchGroupsFromServer();
                }

                if (!contactSynced) {
                    asyncFetchContactsFromServer();
                }

                if (!HXSDKHelper.getInstance().isBlackListSyncedWithServer()) {
                    asyncFetchBlackListFromServer();
                }
            }

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mChatAllHistoryFragment.errorItem.setVisibility(View.GONE);
                }

            });
        }

        @Override
        public void onDisconnected(final int error) {
            final String st1 = getResources().getString(R.string.can_not_connect_chat_server_connection);
            final String st2 = getResources().getString(R.string.the_current_network);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        showAccountRemovedDialog();
                    } else if (error == EMError.CONNECTION_CONFLICT) {
                        // 显示帐号在其他设备登陆dialog
                        showConflictDialog();
                    } else {
                        mChatAllHistoryFragment.errorItem.setVisibility(View.VISIBLE);
                        if (NetUtils.hasNetwork(MainActivity.this))
                            mChatAllHistoryFragment.errorText.setText(st1);
                        else
                            mChatAllHistoryFragment.errorText.setText(st2);
                    }
                }

            });
        }
    }

    /**
     * 保存提示新消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg) {
        saveInviteMsg(msg);
        // 提示有新消息
        HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(null);

        // 刷新bottom bar消息未读数
        updateUnreadAddressLable();
        // 刷新好友页面ui
        if (now_tap == MESSAGE) {
            // 当前页面如果为聊天历史页面，刷新此页面
            if (mChatAllHistoryFragment != null) {
                mChatAllHistoryFragment.refresh();
            }
        }
    }

    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
    public int getUnreadAddressCountTotal() {
        int unreadAddressCountTotal = 0;
        if (((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME) != null)
            unreadAddressCountTotal = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME)
                    .getUnreadMsgCount();
        return unreadAddressCountTotal;
    }

    /**
     * 刷新申请与通知消息数
     */
    public void updateUnreadAddressLable() {
        runOnUiThread(new Runnable() {
            public void run() {
//                int count = getUnreadAddressCountTotal();
//                if (count > 0) {
////					unreadAddressLable.setText(String.valueOf(count));
//                    unreadAddressLable.setVisibility(View.VISIBLE);
//                } else {
//                    unreadAddressLable.setVisibility(View.INVISIBLE);
//                }
            }
        });

    }

    /* 保存邀请等msg
    *
            * @param msg
    */
    private void saveInviteMsg(InviteMessage msg) {
        // 保存msg
        inviteMessgeDao.saveMessage(msg);
        // 未读数加1
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
        if (user.getUnreadMsgCount() == 0)
            user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
    }

    /**
     * MyGroupChangeListener
     */
    public class MyGroupChangeListener implements EMGroupChangeListener {

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {

            boolean hasGroup = false;
            for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
                if (group.getGroupId().equals(groupId)) {
                    hasGroup = true;
                    break;
                }
            }
            if (!hasGroup)
                return;

            // 被邀请
            String st3 = getResources().getString(R.string.Invite_you_to_join_a_group_chat);
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(inviter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(inviter + " " + st3));
            // 保存邀请消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    if (now_tap == MESSAGE) {
                        // 当前页面如果为聊天历史页面，刷新此页面
                        if (mChatAllHistoryFragment != null) {
                            mChatAllHistoryFragment.refresh();
                        }
                    }
//                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                        GroupsActivity.instance.onResume();
//                    }
                }
            });

        }

        @Override
        public void onInvitationAccpted(String groupId, String inviter, String reason) {

        }

        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {

            // 提示用户被T了，demo省略此步骤
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        updateUnreadLabel();
                        if (now_tap == MESSAGE) {
                            // 当前页面如果为聊天历史页面，刷新此页面
                            if (mChatAllHistoryFragment != null) {
                                mChatAllHistoryFragment.refresh();
                            }
                        }
//                        if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                            GroupsActivity.instance.onResume();
//                        }
                    } catch (Exception e) {
                        EMLog.e("", "refresh exception " + e.getMessage());
                    }
                }
            });
        }

        @Override
        public void onGroupDestroy(String groupId, String groupName) {

            // 群被解散
            // 提示用户群被解散,demo省略
            // 刷新ui
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    if (now_tap == MESSAGE) {
                        // 当前页面如果为聊天历史页面，刷新此页面
                        if (mChatAllHistoryFragment != null) {
                            mChatAllHistoryFragment.refresh();
                        }
                    }
//                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                        GroupsActivity.instance.onResume();
//                    }
                }
            });

        }

        @Override
        public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {

            // 用户申请加入群聊
            InviteMessage msg = new InviteMessage();
            msg.setFrom(applyer);
            msg.setTime(System.currentTimeMillis());
            msg.setGroupId(groupId);
            msg.setGroupName(groupName);
            msg.setReason(reason);
            Log.d("", applyer + " 申请加入群聊：" + groupName);
            msg.setStatus(InviteMessage.InviteMesageStatus.BEAPPLYED);
            notifyNewIviteMessage(msg);
        }

        @Override
        public void onApplicationAccept(String groupId, String groupName, String accepter) {

            String st4 = getResources().getString(R.string.Agreed_to_your_group_chat_application);
            // 加群申请被同意
            EMMessage msg = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(accepter);
            msg.setTo(groupId);
            msg.setMsgId(UUID.randomUUID().toString());
            msg.addBody(new TextMessageBody(accepter + " " + st4));
            // 保存同意消息
            EMChatManager.getInstance().saveMessage(msg);
            // 提醒新消息
            HXSDKHelper.getInstance().getNotifier().viberateAndPlayTone(msg);

            runOnUiThread(new Runnable() {
                public void run() {
                    updateUnreadLabel();
                    // 刷新ui
                    if (now_tap == MESSAGE) {
                        // 当前页面如果为聊天历史页面，刷新此页面
                        if (mChatAllHistoryFragment != null) {
                            mChatAllHistoryFragment.refresh();
                        }
                    }
//                    if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                        GroupsActivity.instance.onResume();
//                    }
                }
            });
        }

        @Override
        public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
            // 加群申请被拒绝，demo未实现
        }
    }

}
