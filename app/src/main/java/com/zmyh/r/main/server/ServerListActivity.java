package com.zmyh.r.main.server;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
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
import com.zmyh.r.baidumap.TreeMapActivity;
import com.zmyh.r.baidumap.TreeMapActivityV2;
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.city.AreaActivity;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.SystemHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.SandEmphasisActivity;
import com.zmyh.r.main.SandMuActivity;
import com.zmyh.r.main.SandQGEmphasisActivity;
import com.zmyh.r.seek.SeekActivity;
import com.zmyh.r.seek.SeekDetailActivity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class ServerListActivity extends Activity {

    public final static String MM_CHANNEL = "mmChannel";
    public final static String MM_TITLE = "mmTitle";
    public final static int RC = 100;

    private Context context;

    private int pageIndex = 1, totalPage = 1;

    private String mmChannel = "", mmArea = "";

    private ServerBaseAdapter sba = null;
    private ServerQGBaseAdapter qgba = null;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.title_map)
    private ImageView mapIcon;
    @ViewInject(R.id.title_addServerText)
    private TextView addServerIcon;
    @ViewInject(R.id.server_list_cityText)
    private TextView cityText;
    @ViewInject(R.id.server_list_dataList)
    private ListView dataList;
    @ViewInject(R.id.server_list_progress)
    private ProgressBar progress;
    @ViewInject(R.id.server_list_dataListRefresh)
    private SwipeRefreshLayout dataListRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_list);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        setDataListScrollListener();
        setOnRefreshListener();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if (sba != null) {
//            sba.notifyDataSetChanged();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle b = data.getExtras();
            switch (requestCode) {
                case AreaActivity.RequestCode:
                    if (b.getBoolean("ok")) {
                        cityText.setText(b.getString("Area_name"));
                        mmArea = b.getString("Area_id");
                        initDataList();
                        downloadData();
                    }
                    break;
                case SandEmphasisActivity.RC:
                    if (b.getBoolean("ok")) {
                        initDataList();
                        downloadData();
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.title_back, R.id.server_list_cityText,
            R.id.title_addServerText, R.id.title_seek, R.id.title_map})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.server_list_cityText:
                setCityData();
                break;
            case R.id.title_addServerText:
                jumpSandActivity();
                break;
            case R.id.title_seek:
                jumpSeekActivity();
                break;
            case R.id.title_map:
                jumpMapActivity();
                break;
        }
    }

    private void jumpMapActivity() {
        Bundle b = new Bundle();
        b.putString(TreeMapActivity.MM_CHANNEL, mmChannel);
        Passageway.jumpActivity(context, Intent.FLAG_ACTIVITY_CLEAR_TOP,
                TreeMapActivityV2.class, b);
    }

    private void jumpSeekActivity() {
        Bundle b = new Bundle();
        b.putString(SeekActivity.MM_AREA, mmArea);
        b.putString(SeekActivity.MM_CHANNEL, mmChannel);
        Passageway.jumpActivity(context, SeekDetailActivity.class, b, RC);
    }

    private void jumpSandActivity() {
        if (UserObjHandle.isLogin(context, true)) {
            Bundle b = new Bundle();
            b.putString(SandEmphasisActivity.MM_CHANNEL, mmChannel);
            if (mmChannel.equals("00022")) {
                Passageway.jumpActivity(context, SandMuActivity.class,
                        SandEmphasisActivity.RC, b);
            } else if (mmChannel.equals("00001")) {
                b.putBoolean(SandEmphasisActivity.IS_EMPHASIS,
                        mmChannel.equals("00002"));
                Passageway.jumpActivity(context, SandQGEmphasisActivity.class,
                        SandEmphasisActivity.RC, b);
            } else {
                b.putBoolean(SandEmphasisActivity.IS_EMPHASIS,
                        mmChannel.equals("00002"));
                Passageway.jumpActivity(context, SandEmphasisActivity.class,
                        SandEmphasisActivity.RC, b);
            }
        }
    }

    private void setOnRefreshListener() {
        dataListRefresh.setColorScheme(R.color.holo_blue_bright,
                R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);
        dataListRefresh.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                pageIndex = 1;
                if (sba != null) {
                    sba.removeAll();
                }
                if (qgba != null) {
                    qgba.removeAll();
                }
                downloadData();
            }
        });
    }

    private void setDataListScrollListener() {
        dataList.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
                        if (progress.getVisibility() == View.GONE) {
                            if (totalPage >= pageIndex) {
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

    private void initActivity() {
        titleName.setText("详情");
        seekIcon.setVisibility(View.VISIBLE);
        addServerIcon.setVisibility(View.VISIBLE);
        cityText.setText(CityObjHandler.getCityName(context));
        mmArea = CityObjHandler.getCityId(context);
        Bundle b = getIntent().getExtras();
        if (b != null) {
            titleName.setText(b.getString(MM_TITLE));
            mmChannel = b.getString(MM_CHANNEL);
            downloadData();
            if (!mmChannel.equals("00001")) {
                mapIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initDataList() {
        pageIndex = 1;
        sba = null;
        qgba = null;
        dataList.setAdapter(null);
    }

    private void setCityData() {
        Bundle b = new Bundle();
        b.putBoolean(AreaActivity.COMMON, true);
        Passageway.jumpActivity(context, AreaActivity.class,
                AreaActivity.RequestCode, b);
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
            saveServerTotal(list);
            if (sba == null) {
                sba = new ServerBaseAdapter(context, list);
                dataList.setAdapter(sba);
            } else {
                sba.addItems(list);
            }
        }

        if (dataListRefresh.isRefreshing()) {
            dataListRefresh.setRefreshing(false);
        }
    }

    private void saveServerTotal(List<ServerObj> list) {
        for (ServerObj obj : list) {
            try {
                int total = Integer.valueOf(obj.getMu_total());
                int mTotal = SystemHandle.getInt(context, ServerObj.MU_TOTAL + "_" + obj.getId());
                if (mTotal <= 0) {
                    SystemHandle.saveIntMessage(context, ServerObj.MU_TOTAL + "_" + obj.getId(), total);
                }else{
                    obj.setMu_total(String.valueOf(mTotal));
                }
            } catch (Exception e) {
                SystemHandle.saveIntMessage(context, ServerObj.MU_TOTAL + "_" + obj.getId(), 0);
            }
        }
    }

    private void downloadData() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmPost()
                + "?query="
                + JsonHandle.getHttpJsonToString(new String[]{"type",
                "mmChannel", "mmArea"}, new String[]{"services",
                mmChannel, mmArea}) + "&p=" + pageIndex
                + "&sort=-createAt";
        RequestParams params = HttpUtilsBox.getRequestParams(context);

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
                            if (array != null) {
                                List<ServerObj> list = ServerObjHandler
                                        .getServerObjList(array);
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
