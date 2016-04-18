package com.zmyh.r.main.server;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.main.MainActivity;
import com.zmyh.r.tool.Passageway;

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
public class PayRepairActivity extends Activity {

    @ViewInject(R.id.title_back)
    private ImageView backBtn;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;

    private Context context;

    private Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_repair);
        context = this;
        ViewUtils.inject(this);

        initActivity();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        }
        return false;
    }

    @OnClick({R.id.payResult_payResultText})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.payResult_payResultText:
                showMessageDialog();
                break;
        }
    }

    private void showMessageDialog() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("系统已经通知网站管理员处理订单" + "\n" + "您亦可发送短信息通知卖家");
        dialog.setCommitStyle("返回首页");
        dialog.setCommitListener(new MessageDialog.CallBackListener() {
            @Override
            public void callback() {
                Passageway.jumpToActivity(context, MainActivity.class);
            }
        });
        dialog.setCancelStyle("去发短信");
        dialog.setCancelListener(new MessageDialog.CallBackListener() {
            @Override
            public void callback() {
                Passageway.jumpToActivity(context, MainActivity.class);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(Uri.parse("smsto:" + mBundle.getString("smsto")));
                sendIntent.putExtra("sms_body", mBundle.getString("sms_body"));
                startActivity(sendIntent);
            }
        });
    }

    private void initActivity() {
        backBtn.setVisibility(View.INVISIBLE);
        titleName.setVisibility(View.VISIBLE);
        titleName.setText("支付成功");

        mBundle = getIntent().getExtras();
    }

}
