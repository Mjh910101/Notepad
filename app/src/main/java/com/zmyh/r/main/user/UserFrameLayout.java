package com.zmyh.r.main.user;

import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.dailog.MessageDialog.CallBackListener;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.easemob.DemoHXSDKHelper;
import com.zmyh.r.handler.ImageHandle;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.MsgObjHandler;
import com.zmyh.r.handler.SystemHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.interfaces.CallbackForInteger;
import com.zmyh.r.interfaces.CallbackForString;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class UserFrameLayout extends Fragment {

    private Context context;

    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.user_rewamp)
    private TextView rewamp;
    @ViewInject(R.id.user_loginBox)
    private LinearLayout loginBox;
    @ViewInject(R.id.user_logout)
    private TextView logout;
    @ViewInject(R.id.user_userName)
    private TextView userName;
    @ViewInject(R.id.user_userPic)
    private ImageView userPic;
    @ViewInject(R.id.user_progress)
    private ProgressBar progress;
    @ViewInject(R.id.user_toolBox)
    private LinearLayout toolBox;
    @ViewInject(R.id.user_deleteRight)
    private TextView size;
    @ViewInject(R.id.user_messageSum)
    private TextView messageSum;
    @ViewInject(R.id.user_messageHint)
    private ImageView messageHint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View contactsLayout = inflater.inflate(R.layout.layout_user, container,
                false);
        ViewUtils.inject(this, contactsLayout);
        return contactsLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        isLogin();
        setImageSize();
    }

    @OnClick({R.id.user_login, R.id.user_register, R.id.user_logout,
            R.id.user_rewamp, R.id.user_favorBox, R.id.user_messageBox,
            R.id.user_publishBox, R.id.user_deleteBox, R.id.user_helpBox,
            R.id.user_aboutBox, R.id.user_regainBox, R.id.user_userPic,
            R.id.user_dynamicBox})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_login:
                Passageway.jumpActivity(context, LoginActivity.class);
                break;
            case R.id.user_register:
                Passageway.jumpActivity(context, RegisterActivityV2.class);
                break;
            case R.id.user_logout:
                logoutBtn();
                break;
            case R.id.user_rewamp:
                Passageway.jumpActivity(context, UserSetingActivityV2.class);
                break;
            case R.id.user_favorBox:
                Passageway.jumpActivity(context, UserFavorActivity.class);
                break;
            case R.id.user_messageBox:
                Passageway.jumpActivity(context, UserMessageActivity.class);
                break;
            case R.id.user_publishBox:
                Passageway.jumpActivity(context, UserPublishActivity.class);
                break;
            case R.id.user_deleteBox:
                deleteBtn();
                break;
            case R.id.user_helpBox:
                jumpWebMessage(UrlHandle.getHelpUrl(), "帮助");
                break;
            case R.id.user_aboutBox:
                jumpWebMessage(UrlHandle.getAboutusUrl(), "关于我们");
                break;
            case R.id.user_regainBox:
                regainBtn();
                break;
            case R.id.user_userPic:
                Passageway.jumpActivity(context, PicCompileActivity.class);
                break;
            case R.id.user_dynamicBox:
                Passageway.jumpActivity(context, UserForumActivity.class);
                break;
        }
    }

    private void jumpWebMessage(String url, String title) {
        Bundle b = new Bundle();
        b.putString("url", url);
        b.putString("title", title);
        Passageway.jumpActivity(context, WebMessageActivity.class, b);
    }

    private void isLogin() {
        if (UserObjHandle.isLogin(context)) {
            rewamp.setVisibility(View.VISIBLE);
            loginBox.setVisibility(View.GONE);
            logout.setVisibility(View.VISIBLE);
            userName.setText(UserObjHandle.getM_mobile(context) + "欢迎你");
            setUserPic();
            toolBox.setVisibility(View.VISIBLE);
            dowmloadMessageSize();
        } else {
            rewamp.setVisibility(View.GONE);
            loginBox.setVisibility(View.VISIBLE);
            logout.setVisibility(View.GONE);
            userPic.setVisibility(View.GONE);
            userName.setText("欢迎你");
            toolBox.setVisibility(View.GONE);
        }
    }

    private void dowmloadMessageSize() {
        progress.setVisibility(View.VISIBLE);
        MsgObjHandler.getMessageSize(context, new CallbackForInteger() {
            @Override
            public void callback(int result) {
                progress.setVisibility(View.GONE);
                setMessageSize(result);
            }
        });
    }


    private void setUserPic() {
        userPic.setVisibility(View.VISIBLE);
        int w = WinTool.getWinWidth(context) / 4;
        userPic.setLayoutParams(new LinearLayout.LayoutParams(w, w));
        DownloadImageLoader.loadImage(userPic,
                UserObjHandle.getM_avatar(context), w / 2);
    }

    private void setImageSize() {
        ImageHandle.getFileSum(new CallbackForString() {

            @Override
            public void callback(String result) {
                Message.obtain(handler, 1, result).sendToTarget();
            }
        });
    }

    private void deleteBtn() {
        progress.setVisibility(View.VISIBLE);
        ImageHandle.delete(new CallbackForBoolean() {

            @Override
            public void callback(boolean b) {
                setImageSize();
            }

        });
    }

    private void regainBtn() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("是否恢复默认设置?");
        dialog.setCancelStyle("取消");
        dialog.setCancelListener(null);
        dialog.setCommitStyle("确定");
        dialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                SystemHandle.saveIsGoneShow(context, false);
            }
        });
    }

    private void logoutBtn() {

        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getLogout();

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        params.addBodyParameter("user_id", UserObjHandle.getUsetId(context));

        HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        progress.setVisibility(View.GONE);
                        ShowMessage.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(result);
                        if (json != null) {
                            int auth = JsonHandle.getInt(json, "auth");
                            if (auth == 0) {
                                UserObjHandle.logout(context);
                                DemoHXSDKHelper.getInstance().logout(false, null);
                                isLogin();
                            }
                        }
                    }
                });

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progress.setVisibility(View.GONE);
            String s = (String) msg.obj;
            size.setText(s + "MB");
        }

    };

    public void setMessageSize(int sum) {
        sum = sum - MsgObjHandler.getWatvhSum(context);
        if (sum < 0) {
            sum = 0;
        }
        messageSum.setVisibility(View.VISIBLE);
        messageSum.setText(String.valueOf(sum) + "条未读消息");
        if (sum > 0) {
            messageHint.setVisibility(View.VISIBLE);
        } else {
            messageHint.setVisibility(View.GONE);
        }
    }
}
