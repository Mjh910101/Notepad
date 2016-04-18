package com.zmyh.r.main.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.city.AreaActivity;
import com.zmyh.r.tool.Passageway;

/**
 * Created by Hua on 15/7/15.
 */
public class UserImageActivity extends Activity {

    public final static String ID = "id";
    public final static String IS_ADD="isAdd";

    private Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_image);
        ViewUtils.inject(this);
        context = this;

//        initActivity();
    }

    @OnClick({ R.id.title_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

}
