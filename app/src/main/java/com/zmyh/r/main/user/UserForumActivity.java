package com.zmyh.r.main.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.zmyh.r.box.DynamicObj;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.handler.DynamicObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.forum.ForumV2BaseAdapter;
import com.zmyh.r.tool.ShowMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

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
 * Created by Hua on 15/9/10.
 */
public class UserForumActivity extends Activity {

    private int pageIndex = 1, totalPage = 1;

    private Context context;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.user_frame_dataList)
    private ListView dataList;
    @ViewInject(R.id.user_frame_progress)
    private ProgressBar progress;

    private ForumV2BaseAdapter fba = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_forum);
        ViewUtils.inject(this);
        context = this;
        initActivity();
        setDataListScrollListener();
    }

    @OnClick({R.id.title_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    private void setDataListScrollListener() {
        dataList.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
                        if (progress.getVisibility() == View.GONE) {
                            if (totalPage > pageIndex) {
                                downlosdData();
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

    private void initActivity() {
        titleName.setText("我的苗木圈");
        seekIcon.setVisibility(View.GONE);
        downlosdData();
    }

    private void setDataList(List<DynamicObj> list) {
        if (fba == null) {
            fba = new ForumV2BaseAdapter(context, list);
            dataList.setAdapter(fba);
        } else {
            fba.addItems(list);
        }
    }

    private void downlosdData() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmPost()
                + "?query="
                + JsonHandle.getHttpJsonToString(new String[]{"poster", "mmChannel"},
                new String[]{UserObjHandle.getUsetId(context), "00019"})
                + "&p=" + pageIndex + "&l=10";

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        ShowMessage.showFailure(context);
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(
                                JsonHandle.getJSON(result), "result");
                        if (!ShowMessage.showException(context, json)) {
                            JSONArray array = JsonHandle.getArray(json, "data");
                            if (array != null) {
                                List<DynamicObj> list = DynamicObjHandler
                                        .getDynamicObjList(array);
                                setDataList(list);
                                pageIndex += 1;
                                totalPage = JsonHandle
                                        .getInt(json, "totalPage");
                            }
                        }
                    }

                });
    }

}
