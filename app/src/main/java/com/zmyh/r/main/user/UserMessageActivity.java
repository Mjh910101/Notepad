package com.zmyh.r.main.user;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zmyh.r.box.ChannelObj;
import com.zmyh.r.box.DynamicObj;
import com.zmyh.r.box.MsgObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.DateHandle;
import com.zmyh.r.handler.DynamicObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.MsgObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.forum.ForumContentActivity;
import com.zmyh.r.main.server.ServerContentActivity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class UserMessageActivity extends Activity {

    public final static int SEND = 1;
    public final static int GATHER = 2;

    private int now_tap = 2, pageIndex = 1, totalPage = 1;

    private Context context;

    private MessageBaseAdapter lba;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.message_sendTitle)
    private TextView sendTitle;
    @ViewInject(R.id.message_sendLine)
    private View sendLine;
    @ViewInject(R.id.message_gatherTitle)
    private TextView gatherTitle;
    @ViewInject(R.id.message_gatherLine)
    private View gatherLine;
    @ViewInject(R.id.message_dataList)
    private ListView dataList;
    @ViewInject(R.id.message_progress)
    private ProgressBar progress;
    @ViewInject(R.id.message_notContent)
    private TextView notContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_message);
        ViewUtils.inject(this);
        context = this;
        initActivity();
        setDataListScrollListener();
    }

    @OnClick({R.id.title_back, R.id.message_gatherTitle,
            R.id.message_sendTitle})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.message_gatherTitle:
                setTitleSelection(GATHER);
                break;
            case R.id.message_sendTitle:
                setTitleSelection(SEND);
                break;
        }
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

    private void downloadData() {
        switch (now_tap) {
            case GATHER:
                downloadGatherList();
                break;
            case SEND:
                downloadSendList();
                break;
        }
    }

    private void initActivity() {
        titleName.setText("我的消息");
        seekIcon.setVisibility(View.GONE);
        setTitleSelection(SEND);
    }

    private void initTitleSelection() {
        pageIndex = 1;
        totalPage = 1;
        gatherTitle.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray));
        gatherLine.setVisibility(View.INVISIBLE);
        sendTitle.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_01));
        sendLine.setVisibility(View.INVISIBLE);
        lba = null;
        dataList.setAdapter(null);
    }

    private void setTitleSelection(int i) {
        initTitleSelection();
        now_tap = i;
        switch (i) {
            case GATHER:
                gatherTitle.setTextColor(ColorBox.getColorForID(context,
                        R.color.text_green));
                gatherLine.setVisibility(View.VISIBLE);
                downloadGatherList();
                break;
            case SEND:
                sendTitle.setTextColor(ColorBox.getColorForID(context,
                        R.color.text_green));
                sendLine.setVisibility(View.VISIBLE);
                downloadSendList();
                break;
        }
    }

    private void setDatalist(List<MsgObj> list) {
        if (lba == null) {
            lba = new MessageBaseAdapter(context, list, now_tap);
            dataList.setAdapter(lba);
        } else {
            lba.addItems(list);
        }
    }

    private void downloadGatherList() {
        downloadDataList(JsonHandle.getHttpJsonToString(
                new String[]{"post_id.poster"},
                new String[]{UserObjHandle.getUsetId(context)}));
    }

    private void downloadSendList() {
        downloadDataList(JsonHandle.getHttpJsonToString(
                new String[]{"poster"},
                new String[]{UserObjHandle.getUsetId(context)}));

    }

    private void downloadDataList(String query) {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmPostComment() + "?query=" + query + "&p="
                + pageIndex + "&l=10&sort=-createAt";

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
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
                                List<MsgObj> list = MsgObjHandler
                                        .getMsgObjList(array);
                                setDatalist(list);
                            }
                            pageIndex += 1;
                            totalPage = JsonHandle.getInt(json, "totalPage");
                        }
                    }

                });
    }

}
