package com.zmyh.r.main.user;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.zmyh.r.city.AreaActivity;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.people.TagActivity;
import com.zmyh.r.tool.ShowMessage;

import org.json.JSONArray;
import org.json.JSONObject;

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
public class RegisterActivityV2 extends Activity {

    private Context context;
    private boolean isCorrect = false;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.register_telInput)
    private EditText telInput;
    @ViewInject(R.id.register_passageInput)
    private EditText passwordInput;
    @ViewInject(R.id.register_passageAgainInput)
    private EditText passwordAgainInput;
    @ViewInject(R.id.register_passageAgainIcon)
    private ImageView passwordJudge;
    @ViewInject(R.id.register_progress)
    private ProgressBar progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_v2);
        ViewUtils.inject(this);
        context = this;
        initActivity();
        setTextChangedListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (UserObjHandle.isLogin(context)) {
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case VerifyActivity.RC:
                    if (data != null) {
                        Bundle b = data.getExtras();
                        if (b != null) {
                            if (b.getBoolean(VerifyActivity.ISOK)) {
                                finish();
                            }
                        }
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.title_back, R.id.register_commit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.register_commit:
                commitBtn();
                break;
        }
    }

    private void initActivity() {
        titleName.setText("注册");
        seekIcon.setVisibility(View.GONE);
    }

    private void setTextChangedListener() {
        passwordAgainInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                passwordJudge.setVisibility(View.VISIBLE);
                if (passwordInput.getText().toString()
                        .equals(passwordAgainInput.getText().toString())) {
                    passwordJudge.setImageResource(R.drawable.password_true);
                    isCorrect = true;
                } else {
                    passwordJudge.setImageResource(R.drawable.password_false);
                    isCorrect = false;
                }
            }
        });
    }

    private String getText(EditText et) {
        return et.getText().toString();
    }

    private void commitBtn() {
        telIsRegister();

    }

    private void telIsRegister() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmUser() + "?query=" + JsonHandle.getHttpJsonToString(new String[]{"m_mobile"}, new String[]{getText(telInput)});

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.GET, url, params,
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

                        JSONArray array = JsonHandle.getArray(JsonHandle.getJSON(result), "result");
                        if (array != null && array.length() > 0) {
                            ShowMessage.showToast(context, "此号码已注册");
                        } else {
                            if (isCorrect) {
                                VerifyDialog.showVerifyDialog(context, VerifyActivity.REGISTER, getText(telInput), getText(passwordInput));
                            }
                        }
                    }
                });
    }

}
