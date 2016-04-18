package com.zmyh.r.main.user;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.DynamicObj;
import com.zmyh.r.box.MsgObj;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.handler.CollectObjHandler;
import com.zmyh.r.handler.CollectObjHandler.DynamicObjCallbackListener;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.main.forum.ForumBaseAdapter;
import com.zmyh.r.main.forum.ForumV2BaseAdapter;
import com.zmyh.r.main.server.ServerBaseAdapter;
import com.zmyh.r.tool.ShowMessage;

public class UserFavorActivity extends Activity {

    private final static int DYNAMIC = 1;
    private final static int SEVER = 2;

    private int now_tap = 2, pageIndex = 1, totalPage = 1;

    private ForumV2BaseAdapter fba;
    private MessageBaseAdapter mba;

    private Context context;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.forum_dynamicTitle)
    private TextView dynamicTitle;
    @ViewInject(R.id.forum_dynamicLine)
    private View dynamicLine;
    @ViewInject(R.id.forum_severTitle)
    private TextView severTitle;
    @ViewInject(R.id.forum_severLine)
    private View severLine;
    @ViewInject(R.id.forum_dataList)
    private ListView dataList;
    @ViewInject(R.id.forum_progress)
    private ProgressBar progress;
    @ViewInject(R.id.forum_notContent)
    private TextView notContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forum);
        ViewUtils.inject(this);
        context = this;
        initActivity();
        setDataListScrollListener();
    }

    @OnClick({R.id.title_back, R.id.forum_dynamicTitle, R.id.forum_severTitle})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.forum_dynamicTitle:
                setTitleSelection(DYNAMIC);
                break;
            case R.id.forum_severTitle:
                setTitleSelection(SEVER);
                break;
        }
    }

    private void initActivity() {
        titleName.setText("我的收藏");
        seekIcon.setVisibility(View.GONE);
        setTitleSelection(SEVER);
    }

    private void initTitleSelection() {
        pageIndex = 1;
        totalPage = 1;
        dynamicTitle.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray));
        dynamicLine.setVisibility(View.INVISIBLE);
        severTitle.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_01));
        severLine.setVisibility(View.INVISIBLE);
        fba = null;
        mba = null;
        dataList.setAdapter(null);
    }

    private void setTitleSelection(int i) {
        initTitleSelection();
        now_tap = i;
        switch (i) {
            case DYNAMIC:
                dynamicTitle.setTextColor(ColorBox.getColorForID(context,
                        R.color.text_green));
                dynamicLine.setVisibility(View.VISIBLE);
                downloadDynamicForumList();
                break;
            case SEVER:
                severTitle.setTextColor(ColorBox.getColorForID(context,
                        R.color.text_green));
                severLine.setVisibility(View.VISIBLE);
                downloadServerForumList();
                break;
        }
    }

    private void setDataListScrollListener() {
        dataList.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
                        if (progress.getVisibility() == View.GONE) {
                            if (totalPage > pageIndex) {
                                downloadData();
                            } else {
                                ShowMessage.showLast(context);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

            }
        });
    }

    private void downloadData() {
        switch (now_tap) {
            case DYNAMIC:
                downloadDynamicForumList();
                break;
            case SEVER:
                downloadServerForumList();
                break;
        }
    }

    private void setServerCollectList(List<MsgObj> list) {
        if (mba == null) {
            mba = new MessageBaseAdapter(context, list, -1);
            dataList.setAdapter(mba);
        } else {
            mba.addItems(list);
        }
    }

    private void setCollectList(List<MsgObj> list) {
        List<DynamicObj> items = new ArrayList<DynamicObj>();
        for (MsgObj obj : list) {
            if (obj.getPost() != null) {
                items.add(obj.getPost());
            }
        }
        if (fba == null) {
            fba = new ForumV2BaseAdapter(context, items);
            dataList.setAdapter(fba);
        } else {
            fba.addItems(items);
        }
    }

    private void downloadServerForumList() {
        if (UserObjHandle.isLogin(context, true)) {
            progress.setVisibility(View.VISIBLE);
            CollectObjHandler.getServerFavor(context, pageIndex,
                    new DynamicObjCallbackListener() {

                        @Override
                        public void callback(List<MsgObj> list, int p) {
                            progress.setVisibility(View.GONE);
                            if (list != null) {
                                pageIndex += 1;
                                totalPage = p;
                                setServerCollectList(list);
                            }
                        }

                    });
        }
    }

    private void downloadDynamicForumList() {
        if (UserObjHandle.isLogin(context, true)) {
            progress.setVisibility(View.VISIBLE);
            CollectObjHandler.getUserCollect(context, pageIndex,
                    new DynamicObjCallbackListener() {

                        @Override
                        public void callback(List<MsgObj> list, int p) {
                            progress.setVisibility(View.GONE);
                            if (list != null) {
                                pageIndex += 1;
                                totalPage = p;
                                setCollectList(list);
                            }
                        }
                    });
        }
    }

}
