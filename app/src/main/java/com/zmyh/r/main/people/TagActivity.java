package com.zmyh.r.main.people;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
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
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.ShowMessage;

public class TagActivity extends Activity {

    private final static int MAX = 2;

    public static final int RC = 1212;
    public static final String ALL_TAG = "行业筛选";
    public static final String NOW_TAG = "NOW_TAG";
    public static final String IS_ALL = "IS_ALL";

    private Context context;

    private boolean isAll;
    private List<String> nowTag;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_next)
    private TextView titleNext;
    @ViewInject(R.id.tag_dataGrid)
    private GridView dataGrid;
    @ViewInject(R.id.tag_progress)
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        downloadData();
    }

    @OnClick({R.id.title_back, R.id.title_next})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_next:
                confirmBtn();
                break;
        }
    }

    private void confirmBtn() {
        Intent i = new Intent();
        Bundle b = new Bundle();
        if (isAll) {
            b.putString(NOW_TAG, nowTag.get(0));
        } else {
            b.putStringArray(NOW_TAG, getStringArray(nowTag));
        }
        i.putExtras(b);
        setResult(RC, i);
        finish();
    }

    private String[] getStringArray(List<String> list) {
        String[] tags = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            tags[i] = list.get(i);
        }
        return tags;
    }

    private void setTagList(List<String> list, String[] tags) {
        for (int i = 0; i < tags.length && i < MAX; i++) {
            String s = tags[i];
            if (s != null && !s.equals("")) {
                list.add(s);
            }
        }
    }

    private void initActivity() {
        titleName.setText("行业筛选");
        titleNext.setText("确定");
        titleNext.setVisibility(View.VISIBLE);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            isAll = b.getBoolean(IS_ALL);
            if (isAll) {
                nowTag = new ArrayList<String>(1);
                nowTag.add(b.getString(NOW_TAG));
            } else {
                nowTag = new ArrayList<String>(MAX);
                setTagList(nowTag, b.getStringArray(NOW_TAG));
            }
        }
    }

    private String getTextString(TextView view) {
        return view.getText().toString();
    }

    private void getTagList(JSONArray array) {
        List<String> list = new ArrayList<String>();
        if (isAll) {
            list.add(ALL_TAG);
        }
        for (int i = 0; i < array.length(); i++) {
            list.add(JsonHandle.getString(array, i));
        }
        dataGrid.setAdapter(new GridBaseAdapter(list));
    }

    private void downloadData() {

        progress.setVisibility(View.VISIBLE);

        String url = UrlHandle.getMmUserTag();
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
                        if (json != null) {
                            JSONArray array = JsonHandle.getArray(json, "userTag");
                            if (array != null) {
                                getTagList(array);
                            }
                        }
                    }

                });

    }

    class GridBaseAdapter extends BaseAdapter {

        final int PAD = 8;
        private List<String> list;
        private LayoutInflater inflater;

        public GridBaseAdapter(List<String> list) {
            this.list = list;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int p) {
            return list.get(p);
        }

        @Override
        public long getItemId(int p) {
            return p;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView view = getItemView(list.get(position));
            return view;
        }

        private TextView getItemView(String str) {

            TextView view = new TextView(context);
            view.setText(str);
            view.setTextSize(16);
            view.setGravity(Gravity.CENTER);
            if (!isOnView(view)) {
                view.setTextColor(ColorBox
                        .getColorForID(context, R.color.black));
                view.setBackgroundResource(R.drawable.white_background_white_frame_5);
            } else {
                view.setTextColor(ColorBox
                        .getColorForID(context, R.color.white));
                view.setBackgroundResource(R.drawable.green_background_green_frame_5);
            }
            view.setPadding(PAD, PAD, PAD, PAD);
            setOnClick(view);
            return view;
        }

        private boolean isOnView(TextView view) {
            String s = getTextString(view);
            for (int i = 0; i < nowTag.size(); i++) {
                if (s.equals(nowTag.get(i))) {
                    return true;
                }
            }
            return false;
        }

        private void setOnClick(TextView view) {
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isAll) {
                        nowTag.removeAll(nowTag);
                        nowTag.add(getTextString((TextView) v));
                        notifyDataSetChanged();
                    } else {
                        if (isOnView((TextView) v)) {
                            nowTag.remove(getTextString((TextView) v));
                            notifyDataSetChanged();
                        } else {
                            if (nowTag.size() < MAX) {
                                nowTag.add(getTextString((TextView) v));
                                notifyDataSetChanged();
                            } else {
                                ShowMessage.showToast(context, "最多只能选择"+MAX+"个行业");
                            }
                        }

                    }

                }

            });
        }
    }
}
