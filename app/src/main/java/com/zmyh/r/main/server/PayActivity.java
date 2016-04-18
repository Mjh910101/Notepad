package com.zmyh.r.main.server;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.handler.SystemHandle;
import com.zmyh.r.tool.Passageway;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
 * Created by Hua on 16/4/6.
 */
public class PayActivity extends Activity {

    public final static String TOPUP_MONEY = "money";

    @ViewInject(R.id.title_back)
    private ImageView backBtn;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.topUp_confirm_zhifubaoRadio)
    private RadioButton zhifubaoRadio;
    @ViewInject(R.id.topUp_confirm_weixinRadio)
    private RadioButton weixinRadio;
    @ViewInject(R.id.topUp_confirm_unionpayRadio)
    private RadioButton unionpayRadio;
    @ViewInject(R.id.topUp_confirm_topUpManey)
    private TextView topUpManeyText;
    @ViewInject(R.id.topUp_confirm_message)
    private TextView messageText;
    @ViewInject(R.id.topUp_confirm_progress)
    private ProgressBar progress;

    private double topUpManey;
    private Context context;

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_topup_confirm);
        context = this;
        ViewUtils.inject(this);

        initActivity();
    }

    @OnClick({R.id.title_back, R.id.topUp_confirm_zhifubaoLayout, R.id.topUp_confirm_weixinLayout,
            R.id.topUp_confirm_unionpayLayout, R.id.topUp_confirm_topUpBtm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.topUp_confirm_zhifubaoLayout:
                setZhiFuBaoRadio();
                break;
            case R.id.topUp_confirm_weixinLayout:
                setWeixinRadio();
                break;
            case R.id.topUp_confirm_unionpayLayout:
                setUnionpayRadio();
                break;
            case R.id.topUp_confirm_topUpBtm:
                jumpPayRepairActivity();
                break;
        }
    }

    private void jumpPayRepairActivity() {
        int sum = mBundle.getInt("sum");
        String id = mBundle.getString("id");
        int total = SystemHandle.getInt(context, ServerObj.MU_TOTAL + "_" + id);
        SystemHandle.saveIntMessage(context, ServerObj.MU_TOTAL + "_" + id, (total - sum));
        Passageway.jumpActivity(context, PayRepairActivity.class, mBundle);
    }

    private void initActivity() {
        backBtn.setVisibility(View.VISIBLE);
        titleName.setVisibility(View.VISIBLE);
        titleName.setText("支付订金");

        setZhiFuBaoRadio();
        mBundle = getIntent().getExtras();
        if (mBundle != null) {
            topUpManey = mBundle.getDouble(TOPUP_MONEY, 0);
            messageText.setText(mBundle.getString("message"));
            if (topUpManey <= 0) {
                finish();
            } else {
                topUpManeyText.setText("￥" + topUpManey);
            }
        } else {
            finish();
        }
    }

    public void setZhiFuBaoRadio() {
        initRadio();
        zhifubaoRadio.setChecked(true);
    }

    public void setWeixinRadio() {
        initRadio();
        weixinRadio.setChecked(true);
    }

    public void setUnionpayRadio() {
        initRadio();
        unionpayRadio.setChecked(true);
    }

    private void initRadio() {
        zhifubaoRadio.setChecked(false);
        weixinRadio.setChecked(false);
        unionpayRadio.setChecked(false);
    }

}
