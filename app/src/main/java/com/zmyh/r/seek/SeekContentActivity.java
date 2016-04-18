package com.zmyh.r.seek;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
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
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.TroopObjHandler;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.main.server.ServerBaseAdapter;
import com.zmyh.r.main.server.ServerQGBaseAdapter;
import com.zmyh.r.photo.adapter.MntTroopBaseAdapter;
import com.zmyh.r.photo.adapter.OnlineTroopBaseAdapter;
import com.zmyh.r.photo.adapter.TroopBaseAdapter;
import com.zmyh.r.tool.ShowMessage;

public class SeekContentActivity extends Activity {

    private Context context;
    private String url;
    private String[] keys;
    private String[] values;
    private int pageIndex = 1, totalPage = 1;
    private String mmChannel = "";

    private ServerBaseAdapter sba = null;
    private ServerQGBaseAdapter qgba = null;
    private TroopBaseAdapter troopBaseAdapter;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.seek_content_dataList)
    private ListView dataList;
    @ViewInject(R.id.seek_content_progress)
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek_content);
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
        dataList.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
                        if (progress.getVisibility() == View.GONE) {
                            if (totalPage > pageIndex) {
                                if (url != null) {
                                    donwloadData(url);
                                } else {
                                    seekData(keys, values);
                                }

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
        titleName.setText("搜索结果");
        Bundle b = getIntent().getExtras();
        if (b != null) {
            url = b.getString("url");
            mmChannel = b.getString(SeekActivity.MM_CHANNEL);
            if (url != null) {
                donwloadData(url);
            } else {
                keys = b.getStringArray("keys");
                values = b.getStringArray("values");
                pageIndex = 0;
                seekData(keys, values);
            }

        }
    }

    private void setDataList(List<ServerObj> list) {
        if (mmChannel.equals("00001")) {
            if (qgba == null) {
                qgba = new ServerQGBaseAdapter(context, list);
                dataList.setAdapter(qgba);
            } else {
                qgba.addItems(list);
            }
        } else {
            if (sba == null) {
                sba = new ServerBaseAdapter(context, list);
                dataList.setAdapter(sba);
            } else {
                sba.addItems(list);
            }
        }
    }

    private void setTroopDataList(List<TroopObj> list, boolean isOnline) {
        if (troopBaseAdapter == null) {
            if (isOnline) {
                troopBaseAdapter = new OnlineTroopBaseAdapter(context, list);
            } else {
                troopBaseAdapter = new MntTroopBaseAdapter(context, list);
            }
            dataList.setAdapter(troopBaseAdapter);
        } else {
            troopBaseAdapter.addItem(list);
        }
        pageIndex += 1;
        progress.setVisibility(View.GONE);
    }

    private void seekData(String[] keys, String[] values) {
        progress.setVisibility(View.VISIBLE);
        List<TroopObj> list = TroopObjHandler.getTroopObjListForMuName(context,
                keys, values, pageIndex);
        setTroopDataList(list, false);
    }

    private void donwloadData(String url) {
        progress.setVisibility(View.VISIBLE);
        RequestParams params = HttpUtilsBox.getRequestParams(context);

        url += "&p=" + pageIndex;

        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
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

                        JSONObject json = JsonHandle.getJSON(
                                JsonHandle.getJSON(result), "result");
                        if (!ShowMessage.showException(context, json)) {
                            JSONArray array = JsonHandle.getArray(json, "data");
                            if (array != null && array.length() > 0) {
                                List<ServerObj> list = ServerObjHandler
                                        .getServerObjList(array);
                                setDataList(list);
                                pageIndex += 1;
                                totalPage = JsonHandle
                                        .getInt(json, "totalPage");
                            } else {
                                ShowMessage.showToast(context, "没有找到结果");
                            }
                        }
                    }

                });
    }

}
