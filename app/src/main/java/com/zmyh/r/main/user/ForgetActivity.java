package com.zmyh.r.main.user;

import org.json.JSONObject;

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
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.ShowMessage;

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

public class ForgetActivity extends Activity {

    private Context context;
    private boolean isCorrect = false;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.forget_telInput)
    private EditText telInput;
    @ViewInject(R.id.forget_passageInput)
    private EditText passwordInput;
    @ViewInject(R.id.forget_passageAgainInput)
    private EditText passwordAgainInput;
    @ViewInject(R.id.forget_passageAgainIcon)
    private ImageView passwordJudge;
    @ViewInject(R.id.forget_progress)
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        setTextChangedListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            switch (requestCode) {
                case VerifyActivity.RC:
                    if(data!=null){
                        Bundle b=data.getExtras();
                        if(b!=null  ){
                            if(b.getBoolean(VerifyActivity.ISOK)){
                                finish();
                            }
                        }
                    }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.title_back, R.id.forget_commit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.forget_commit:
                commit();
                break;
        }
    }

    private void initActivity() {
        titleName.setText("忘记密码");
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

    private void commit() {
        if (isCorrect) {
            VerifyDialog.showVerifyDialog(context, VerifyActivity.FORGET, getText(telInput), getText(passwordInput));
        }
    }

}
