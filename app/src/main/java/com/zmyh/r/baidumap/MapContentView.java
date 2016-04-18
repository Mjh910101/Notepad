package com.zmyh.r.baidumap;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zmyh.r.R;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.main.server.ServerContentActivity;
import com.zmyh.r.main.server.ServerContentNoLoginActivity;
import com.zmyh.r.tool.Passageway;

public class MapContentView extends LinearLayout {

    private ServerObj mServerObj;
    private LayoutInflater infater;
    private LinearLayout contentBox;
    private Context context;
    private View Activity;

    private TextView name, people, content, intro;

    public MapContentView(Context context) {
        super(context);
        this.context = context;
    }

    public MapContentView(Context context, ServerObj mServerObj,
                          boolean isOnClick) {
        this(context);
        this.mServerObj = mServerObj;
        infater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Activity = infater.inflate(R.layout.map_content, null);

        initView();
        initContent();
        isLogin();

        if (isOnClick) {
            content.setVisibility(View.VISIBLE);
            contentBox.setOnClickListener(onClick);
        } else {
            content.setVisibility(View.INVISIBLE);
        }

        addView(Activity);
    }

    private void initContent() {
        name.setText(mServerObj.getTitle());
        people.setText("联系人:" + mServerObj.getName() + "("
                + mServerObj.getPhone() + ")");
        intro.setText(mServerObj.getInfo(false));
    }

    private void initView() {
        name = (TextView) Activity.findViewById(R.id.mapContent_name);
        content = (TextView) Activity.findViewById(R.id.mapContent_content);
        people = (TextView) Activity.findViewById(R.id.mapContent_people);
        intro = (TextView) Activity.findViewById(R.id.mapContent_intro);
        contentBox = (LinearLayout) Activity.findViewById(R.id.mapContent_box);
    }

    OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Bundle b = new Bundle();
            b.putString("id", mServerObj.getId());
            if (UserObjHandle.isLogin(context)) {
                Passageway.jumpActivity(context, ServerContentActivity.class, b);
            } else {
                Passageway.jumpActivity(context, ServerContentNoLoginActivity.class, b);
            }
        }
    };

    public void isLogin() {
        if (UserObjHandle.isLogin(context)) {

        } else {
            people.setVisibility(View.GONE);
            intro.setText("注册登陆后可见详细信息");
        }
    }
}
