package com.zmyh.r.main.user;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.exceptions.EaseMobException;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.UserObj;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.easemob.DemoHXSDKHelper;
import com.zmyh.r.easemob.applib.controller.HXSDKHelper;
import com.zmyh.r.easemob.db.InviteMessgeDao;
import com.zmyh.r.easemob.db.UserDao;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.EMChatHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.ShowMessage;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
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
 * Created by Hua on 15/7/20.
 */
public class VerifyActivity extends Activity {

    public final static int RC = 46694;
    public final static int REGISTER = 100;
    public final static int FORGET = 200;
    private final static int M = 60;
    private final static String CODE = "86";

    public final static String COME_KEY = "come";
    public final static String TEL_KEY = "tel";
    public final static String PASSWORD_KEY = "password";
    public final static String ISOK = "ok";

    private Context context;
    private String tel = "", pass = "";
    private int com = 0;

    private UserDao userDao;
    private InviteMessgeDao inviteMessgeDao;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.verify_telInput)
    private TextView telText;
    @ViewInject(R.id.verify_getCode)
    private TextView getCodeText;
    @ViewInject(R.id.verify_codeInput)
    private EditText codeInput;
    @ViewInject(R.id.verify_progress)
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        ViewUtils.inject(this);
        context = this;
        initActivity();
    }

    @OnClick({R.id.title_back, R.id.verify_confirmBtn, R.id.verify_getCode})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.verify_confirmBtn:
                String code = codeInput.getText().toString();
                SMSSDK.submitVerificationCode(CODE, tel, code);
//                registerBtn();
                break;
            case R.id.verify_getCode:
                getCode(tel);
                break;
        }
    }

    private void initActivity() {
        SMSSDK.registerEventHandler(eh);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            com = b.getInt(COME_KEY);
            tel = b.getString(TEL_KEY);
            pass = b.getString(PASSWORD_KEY);
            setTitleName(com);
            getCode(tel);
//            submit(com);
        }

    }

    private void setTitleName(int com) {
        switch (com) {
            case REGISTER:
                titleName.setText("注册");
                break;
            case FORGET:
                titleName.setText("忘记密码");
                break;
        }
    }

    private void getCode(String tel) {
        this.tel = tel;
        telText.setText(tel);
        Log.e("phone ==>>", tel);
        SMSSDK.getVerificationCode(CODE, tel);
        startClock();
    }

    private void startClock() {
        getCodeText.setClickable(false);
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = M; i >= 0; i--) {
                    Message.obtain(clockHandler, i).sendToTarget();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    private void submit(int com) {
        progress.setVisibility(View.VISIBLE);
        switch (com) {
            case REGISTER:
                registerBtn();
                break;
            case FORGET:
                forgetBtn();
                break;
        }
    }

    private void forgetBtn() {

        String url = UrlHandle.getMmUser();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("m_mobile", tel);
        params.addBodyParameter("m_password", pass);
        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception,
                                          String msg) {
                        progress.setVisibility(View.GONE);
                        ShowMessage.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(
                                JsonHandle.getJSON(result), "result");
                        if (!ShowMessage.showException(context, json)) {
                            UserObj user = UserObjHandle.getUserBox(json);
                            UserObjHandle.savaUser(context, user);
                            close();
                        }
                    }
                });
    }

    private void registerBtn() {

        String url = UrlHandle.getRegister();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("m_mobile", tel);
        params.addBodyParameter("m_password", pass);
        params.addBodyParameter("mmArea", CityObjHandler.getCityId(context));
        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception,
                                          String msg) {
                        progress.setVisibility(View.GONE);
                        ShowMessage.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
//                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(result);
                        if (json != null) {
                            UserObj user = UserObjHandle
                                    .getUserBox(JsonHandle.getJSON(json,
                                            "auth"));
                            UserObjHandle.savaUser(context, user);
//                            close();
                            registerEMBtn();
                        }
                    }
                });
    }

    private void registerEMBtn() {
        final String user_id = UserObjHandle.getUsetId(context);
        EMChatHandler.registerEMChat(this, user_id, new EMChatHandler.EMChatRegisterListener() {
            @Override
            public void onError(int errorCode) {

            }

            @Override
            public void callback(boolean b) {
                if (b) {
                    loginEMBtn(user_id);
//                    close();
                }
            }
        });
    }

    private void loginEMBtn(String user_id) {
        EMChatHandler.loginEMChat(this, new EMChatHandler.EMChatLoginListener() {
            @Override
            public void onError(int code, String message) {

            }

            @Override
            public void callback(boolean b) {
                if (b) {
                    initEMChat();
                    close();
                }
            }
        });
    }

    private void close() {
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putBoolean(ISOK, true);
        i.putExtras(b);
        setResult(RC, i);
        finish();
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
            return;
        }
        EMChat.getInstance().init(context);
        EMChat.getInstance().setDebugMode(false);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题

        //异步获取当前用户的昵称和头像
        ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().asyncGetCurrentUserInfo(context);

        // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
        EMChatManager.getInstance().updateCurrentUserNick(
                UserObjHandle.getM_nick_name(context).trim());
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

    private Handler clockHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int time = msg.what;
            getCodeText.setText(time + "秒后重新获取");
            if (time == 0) {
                getCodeText.setText("点击获取");
                getCodeText.setClickable(true);
            }
        }

    };

    private void showSmsException() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("验证有误，请重新输入");
        dialog.setCommitStyle("确定");
        dialog.setCommitListener(null);
    }

    private Handler msgHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    submit(com);
                    break;
                default:
                    showSmsException();
                    break;
            }

        }

    };

    EventHandler eh = new EventHandler() {

        @Override
        public void afterEvent(int event, int result, Object data) {

            if (result == SMSSDK.RESULT_COMPLETE) { // 回调完成
                Log.e("", "回调完成");
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                    Log.e("", "提交验证码成功");
                    Message.obtain(msgHandler, 200).sendToTarget();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {// 获取验证码成功
                    Log.e("", "获取验证码成功");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {// 返回支持发送验证码的国家列表

                }
            } else {
                Log.e("", "回调失败");
                ((Throwable) data).printStackTrace();
                Message.obtain(msgHandler, 404).sendToTarget();
            }
        }

    };


}
