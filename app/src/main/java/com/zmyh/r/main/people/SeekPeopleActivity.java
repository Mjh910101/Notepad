package com.zmyh.r.main.people;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

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
import com.zmyh.r.box.UserObj;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.server.ServerBaseAdapter;
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
 * Created by Hua on 15/9/8.
 */
public class SeekPeopleActivity extends Activity {

    private Context context;

    private int pageIndex = 1, totalPage = 1;
    private String seekStr = "";

    private InputMethodManager imm = null;
    private PeopleBaseAdapter pba = null;

    @ViewInject(R.id.title_seekInput)
    private EditText seekInput;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.seek_dataList)
    private ListView dataList;
    @ViewInject(R.id.seek_progress)
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        setDataListScrollListener();
        setInputOnKeyListener();
    }

    @OnClick({R.id.title_back, R.id.title_seek})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_seek:
                seekBtn();
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
                                donwloadData();
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

    private void setInputOnKeyListener() {
        seekInput.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    seekBtn();
                    return true;
                }
                return false;
            }

        });
    }

    private void seekBtn() {
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(seekInput.getApplicationWindowToken(),
                    0);
        }
        if (pba != null) {
            pba.removeAll();
        }
        seekStr = seekInput.getText().toString();
        pageIndex = 1;
        donwloadData();
    }

    private void initActivity() {
        seekInput.setVisibility(View.VISIBLE);
        seekIcon.setVisibility(View.VISIBLE);
        imm = (InputMethodManager) seekInput.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
    }

    private void setDataList(List<UserObj> list) {
        if (pba == null) {
            pba = new PeopleBaseAdapter(context, list);
            dataList.setAdapter(pba);
        } else {
            pba.addItems(list);
        }

    }

    private void donwloadData() {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmUser()
                + "?query="
                + JsonHandle.getHttpJsonToString(new String[]{"m_nick_name.$like", "m_mobile.$like"}, new String[]{seekStr, seekStr}) + "&p="
                + pageIndex + "&sort=-createAt" + "&user_id="
                + UserObjHandle.getUsetId(context);
        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.GET, url, params,
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
                                List<UserObj> list = UserObjHandle
                                        .getUserObjList(array);
                                setDataList(list);
                                pageIndex += 1;
                                totalPage = JsonHandle
                                        .getInt(json, "totalPage");
                            } else {
                                ShowMessage.showToast(context, "暂时没有找到相关信息");
                            }
                        }
                    }

                });
    }
}
