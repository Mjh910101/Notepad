package com.zmyh.r.main.user;

import java.util.ArrayList;
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
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.forum.ForumFrameLayout;
import com.zmyh.r.main.server.ServerBaseAdapter;
import com.zmyh.r.main.server.ServerQGBaseAdapter;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class UserPublishActivity extends Activity {

    public final static int SEND = 1;
    public final static int DELETE = 2;
    private final static int GY = 3;
    private final static int QG = 4;

    private int now_tap = 2, pageIndex = 1, totalPage = 1;
    private String now_channel = "";
    private Context context;

    private PublishServerBaseAdapter sba;
    private PublishServerQGBaseAdapter qgba;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.publish_sendTitle)
    private TextView sendTitle;
    @ViewInject(R.id.publish_sendLine)
    private View sendLine;
    @ViewInject(R.id.publish_deleteTitle)
    private TextView deleteTitle;
    @ViewInject(R.id.publish_deleteLine)
    private View deleteLine;
    @ViewInject(R.id.publish_dataList)
    private ListView dataList;
    @ViewInject(R.id.publish_progress)
    private ProgressBar progress;
    @ViewInject(R.id.publish_notContent)
    private TextView notContent;
    @ViewInject(R.id.publish_gyIcon)
    private TextView gyIcon;
    @ViewInject(R.id.publish_qgIcon)
    private TextView qgIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_publish);
        ViewUtils.inject(this);
        context = this;
        initActivity();
        setDataListScrollListener();
    }

    @OnClick({R.id.title_back, R.id.publish_sendTitle,
            R.id.publish_deleteTitle, R.id.publish_gyIcon, R.id.publish_qgIcon})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.publish_sendTitle:
                setTitleSelection(SEND);
                break;
            case R.id.publish_deleteTitle:
                setTitleSelection(DELETE);
                break;
            case R.id.publish_gyIcon:
                setMmChannel(GY, now_tap);
                break;
            case R.id.publish_qgIcon:
                setMmChannel(QG, now_tap);
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
                                if (now_tap == SEND) {
                                    downlosdSendData(now_channel);
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
        titleName.setText("上下架");
        seekIcon.setVisibility(View.GONE);
        setTitleSelection(SEND);
    }

    private void initTitleSelection() {

        sendTitle.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray));
        sendLine.setVisibility(View.INVISIBLE);
        deleteTitle.setTextColor(ColorBox.getColorForID(context,
                R.color.text_gray_01));
        deleteLine.setVisibility(View.INVISIBLE);

    }

    private void setTitleSelection(int i) {
        initTitleSelection();
        now_tap = i;
        switch (i) {
            case SEND:
                sendTitle.setTextColor(ColorBox.getColorForID(context,
                        R.color.text_green));
                sendLine.setVisibility(View.VISIBLE);
                setMmChannel(GY, i);
                break;
            case DELETE:
                deleteTitle.setTextColor(ColorBox.getColorForID(context,
                        R.color.text_green));
                deleteLine.setVisibility(View.VISIBLE);
                setMmChannel(GY, i);
                break;
        }
    }

    private void setMmChannel(int i, int tap) {
        initChannelIcon();
        switch (i) {
            case GY:
                now_channel = "00002";
                gyIcon.setTextColor(ColorBox.getColorForID(context, R.color.white));
                gyIcon.setBackgroundResource(R.drawable.publish_gy_green_icon);
                break;
            case QG:
                now_channel = "00001";
                qgIcon.setTextColor(ColorBox.getColorForID(context, R.color.white));
                qgIcon.setBackgroundResource(R.drawable.publish_qg_green_icon);
                break;
        }
        if (tap == SEND) {
            downlosdSendData(now_channel);
        } else {
            getDeleteSendData(now_channel);
        }
    }

    private void initChannelIcon() {
        qgIcon.setTextColor(ColorBox.getColorForID(context, R.color.text_gray));
        qgIcon.setBackgroundResource(R.drawable.publish_qg_gray_icon);

        gyIcon.setTextColor(ColorBox.getColorForID(context, R.color.text_gray));
        gyIcon.setBackgroundResource(R.drawable.publish_gy_gray_icon);


        pageIndex = 1;
        totalPage = 1;

        sba = null;
        qgba = null;
        dataList.setAdapter(null);
    }

    public interface DeleteServerCallback {
        void callback(ServerObj dObj);
    }

    private void setDataList(List<ServerObj> list) {
        DeleteServerCallback callback = new DeleteServerCallback() {
            @Override
            public void callback(ServerObj dObj) {
                deleteSendData(dObj);
            }
        };
        if (now_channel.equals("00002")) {
            if (sba == null) {
                sba = new PublishServerBaseAdapter(context, list, now_tap);
                sba.setDeleteCallback(callback);
                dataList.setAdapter(sba);
            } else {
                sba.addItems(list);
            }
        } else {
            if (qgba == null) {
                qgba = new PublishServerQGBaseAdapter(context, list, now_tap);
                qgba.setDeleteCallback(callback);
                dataList.setAdapter(qgba);
            } else {
                qgba.addItems(list);
            }
        }

    }

    private void getDeleteSendData(String channel) {
        String str = ServerObjHandler.getDaleteServerObj(context, channel);
        if (!str.equals("")) {
            String[] array = str.split("\\|");
            List<ServerObj> list = new ArrayList<ServerObj>(array.length);
            for (int i = 0; i < array.length; i++) {
                JSONObject json = JsonHandle.getJSON(array[i]);
                list.add(ServerObjHandler.getServerObj(json));
            }
            setDataList(list);
        }
    }

    public void deleteSendData(final ServerObj cObj) {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmPost() + "/" + cObj.getId();

        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.DELETE, url, params,
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
                            int r = JsonHandle.getInt(
                                    JsonHandle.getJSON(json, "o"), "ok");
                            if (r == 1) {
                                ServerObjHandler.saveDaleteServerObj(context,
                                        cObj, now_channel);
                                removeItem(cObj);
                            }
                        }
                    }

                });
    }

    private void removeItem(ServerObj obj) {
        if (sba != null) {
            sba.removeItem(obj);
        }
        if (qgba != null) {
            qgba.removeItem(obj);
        }
    }

    private void downlosdSendData(String mmChannel) {
        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmPost()
                + "?query="
                + JsonHandle.getHttpJsonToString(new String[]{"poster", "mmChannel"},
                new String[]{UserObjHandle.getUsetId(context), mmChannel})
                + "&p=" + pageIndex + "&l=100&sort=-createAt";

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
                                List<ServerObj> list = ServerObjHandler
                                        .getServerObjList(array);
                                setDataList(list);
                            }
                        }
                    }

                });
    }

//    class ServerBaseAdapter extends BaseAdapter {
//
//        private LayoutInflater inflater;
//        private List<ServerObj> itemList;
//
//        private ServerObj cObj;
//
//        public ServerBaseAdapter(List<ServerObj> list) {
//            this.itemList = list;
//            inflater = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//
//        public void addItems(List<ServerObj> list) {
//            for (ServerObj obj : list) {
//                addItem(obj);
//            }
//            notifyDataSetChanged();
//        }
//
//        private void addItem(ServerObj obj) {
//            itemList.add(obj);
//        }
//
//        private void removeItem(ServerObj obj) {
//            itemList.remove(obj);
//            notifyDataSetChanged();
//        }
//
//        @Override
//        public int getCount() {
//            return itemList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return itemList.get(position);
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = inflater
//                        .inflate(R.layout.publish_list_item, null);
//            }
//
//            ServerObj obj = itemList.get(position);
//
//            setContent(convertView, obj);
//            setToolBox(convertView, obj);
//            setOnClick(convertView, obj);
//            setOnAmendBtn(convertView);
//            setOnDeleteBtn(convertView);
//            return convertView;
//        }
//
//        private void setOnDeleteBtn(View view) {
//            view.findViewById(R.id.server_list_deleteBtn).setOnClickListener(
//                    new OnClickListener() {
//
//                        @Override
//                        public void onClick(View arg0) {
//                            deleteSendData(cObj);
//                        }
//                    });
//        }
//
//        private void setOnAmendBtn(View view) {
//            view.findViewById(R.id.server_list_amendBtn).setOnClickListener(
//                    new OnClickListener() {
//
//                        @Override
//                        public void onClick(View arg0) {
//                            ServerBox.saveServerObj(cObj);
//                            Passageway.jumpActivity(context,
//                                    AmendActivity.class);
//                        }
//                    });
//        }
//
//        private void setOnClick(View view, final ServerObj obj) {
//            if (now_tap != DELETE) {
//                view.setOnClickListener(new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        if (cObj != obj) {
//                            cObj = obj;
//                        } else {
//                            cObj = null;
//                        }
//                        notifyDataSetChanged();
//                    }
//                });
//            }
//        }
//
//        private void setToolBox(View view, ServerObj obj) {
//            ImageView switchIcon = (ImageView) view
//                    .findViewById(R.id.server_list_switch);
//            LinearLayout toolBox = (LinearLayout) view
//                    .findViewById(R.id.server_list_toolBox);
//
//            toolBox.setVisibility(View.GONE);
//            if (now_tap == DELETE) {
//                switchIcon.setImageBitmap(null);
//            } else {
//                if (cObj != null && cObj == obj) {
//                    toolBox.setVisibility(View.VISIBLE);
//                    switchIcon.setImageResource(R.drawable.server_on_icon);
//                } else {
//                    switchIcon.setImageResource(R.drawable.server_off_icon);
//                }
//            }
//        }
//
//        private void setContent(View view, ServerObj obj) {
//            TextView title = (TextView) view
//                    .findViewById(R.id.server_list_title);
//            TextView time = (TextView) view.findViewById(R.id.server_list_time);
//
//            title.setText(obj.getContent());
//            time.setText(obj.getCreateTime());
//        }
//    }

}
