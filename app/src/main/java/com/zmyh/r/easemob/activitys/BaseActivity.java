package com.zmyh.r.easemob.activitys;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.zmyh.r.easemob.applib.controller.HXSDKHelper;

public class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onresume时，取消notification显示
        HXSDKHelper.getInstance().getNotifier().reset();

    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }
}