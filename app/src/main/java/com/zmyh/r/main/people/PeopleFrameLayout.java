package com.zmyh.r.main.people;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import com.zmyh.r.box.UserObj;
import com.zmyh.r.city.AreaActivity;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class PeopleFrameLayout extends Fragment {

    private final static int EVERY = 0;
    private final static int PROPLE = 1;
    private final static int FRIEND = 2;
    private final static int BLACK = 3;
    private final static int FLOCK = 4;

    private Context context;

    private String mmArea = "", m_tag = "";
    private int pageIndex = 1, totalPage = 1;
    private int now_tag = 0;

    private PeopleBaseAdapter pba;

    @ViewInject(R.id.people_cityText)
    private TextView cityText;
    @ViewInject(R.id.people_tapText)
    private TextView tagText;
    @ViewInject(R.id.people_peopleIcon)
    private ImageView peopleIcon;
    @ViewInject(R.id.people_friendIcon)
    private ImageView friendIcon;
    @ViewInject(R.id.people_blackIcon)
    private ImageView blackIcon;
    @ViewInject(R.id.people_flockIcon)
    private ImageView flockIcon;
    @ViewInject(R.id.people_dataList)
    private ListView dataList;
    @ViewInject(R.id.people_progress)
    private ProgressBar progress;
    @ViewInject(R.id.people_dataListRefresh)
    private SwipeRefreshLayout dataListRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View contactsLayout = inflater.inflate(R.layout.layout_people,
                container, false);
        ViewUtils.inject(this, contactsLayout);

        initCity(false);
        setTagText(TagActivity.ALL_TAG, false);
        setIconLayoutParams();
        setDataListScrollListener();
        setOnRefreshListener();
        setdataList(EVERY);
        return contactsLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick({R.id.people_cityText, R.id.people_peopleIcon,
            R.id.people_friendIcon, R.id.people_blackIcon,
            R.id.people_flockIcon, R.id.people_tapText,
            R.id.people_seekIcon})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.people_cityText:
                jumpCityActivity();
                break;
            case R.id.people_tapText:
                jumpTagActivity();
                break;
            case R.id.people_peopleIcon:
                setdataList(PROPLE);
                break;
            case R.id.people_friendIcon:
                setdataList(FRIEND);
                break;
            case R.id.people_blackIcon:
                setdataList(BLACK);
                break;
            case R.id.people_flockIcon:
                setdataList(FLOCK);
                break;
            case R.id.people_seekIcon:
                jumpSeekActivity();
                break;
        }
    }

    private void jumpSeekActivity() {
        Passageway.jumpActivity(context, SeekPeopleV2Activity.class);
    }

    private void jumpTagActivity() {
        Bundle b = new Bundle();
        b.putBoolean(TagActivity.IS_ALL, true);
        b.putString(TagActivity.NOW_TAG, getTextString(tagText));
        Passageway.jumpActivity(context, TagActivity.class, TagActivity.RC, b);
    }

    private String getTextString(TextView view) {
        return view.getText().toString();
    }

    private void jumpCityActivity() {
        Bundle b = new Bundle();
        b.putBoolean(AreaActivity.COMMON, true);
        Passageway.jumpActivity(context, AreaActivity.class,
                AreaActivity.RequestCode, b);
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


    private void setOnRefreshListener() {
        dataListRefresh.setColorScheme(R.color.holo_blue_bright,
                R.color.holo_green_light, R.color.holo_orange_light,
                R.color.holo_red_light);
        dataListRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                uploadDataList(now_tag);
            }
        });
    }

    private void uploadDataList(int i) {
        setdataList(i, true);
    }

    private void setdataList(int i) {
        setdataList(i, false);
    }

    private void setdataList(int i, boolean isUpload) {
        if (i == now_tag && !isUpload) {
            now_tag = EVERY;
        } else {
            now_tag = i;
        }
        initIcon();
        switch (now_tag) {
            case EVERY:
                downloadData();
                break;
            case PROPLE:
                peopleIcon.setImageResource(R.drawable.people_list_on_icon);
                downloadData();
                break;
            case FRIEND:
                friendIcon.setImageResource(R.drawable.friend_list_on_icon);
                downloadFriendList();
                break;
            case BLACK:
                blackIcon.setImageResource(R.drawable.black_list_on_icon);
                downloadBlockList();
                break;
            case FLOCK:
                flockIcon.setImageResource(R.drawable.flock_list_on_icon);
                break;
        }
    }

    private void initIcon() {
        pageIndex = 1;
        totalPage = 1;

        peopleIcon.setImageResource(R.drawable.people_list_off_icon);
        friendIcon.setImageResource(R.drawable.friend_list_off_icon);
        blackIcon.setImageResource(R.drawable.black_list_off_icon);
        flockIcon.setImageResource(R.drawable.flock_list_off_icon);

        pba = null;
        dataList.setAdapter(null);
    }

    private void initCity(boolean isDownload) {
        setCityData(CityObjHandler.getCityName(context),
                CityObjHandler.getCityId(context), isDownload);
    }

    public void setTagText(String tag) {
        setTagText(tag, true);
    }

    public void setTagText(String tag, boolean isDownload) {
        tagText.setText(tag);
        if (tag.equals(TagActivity.ALL_TAG)) {
            m_tag = "";
        } else {
            m_tag = tag;
        }
        if (isDownload) {
            setdataList(now_tag);
        }
    }

    public void setCityData(String name, String id) {
        setCityData(name, id, true);
    }

    public void setCityData(String name, String id, boolean isDownload) {
        cityText.setText(name + "苗木人");
        mmArea = id;
        if (isDownload) {
            setdataList(now_tag);
        }
    }

    private void setIconLayoutParams() {
        setIconLayoutParams(peopleIcon);
        setIconLayoutParams(friendIcon);
        setIconLayoutParams(blackIcon);
        setIconLayoutParams(flockIcon);
    }

    private void setIconLayoutParams(ImageView iv) {
        double w = WinTool.getWinWidth(context) / 4;
        double h = w / 135 * 124;
        iv.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
    }

    private Map<String, List<String>> getQuery() {
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> keys = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        map.put("keys", keys);
        map.put("values", values);

        if (!mmArea.equals("")) {
            keys.add("mmArea");
            values.add(mmArea);
        }

        if (!m_tag.equals("")) {
            keys.add("m_tag");
            values.add(m_tag);
        }

        switch (now_tag) {
            case PROPLE:
                keys.add("is_auth");
                values.add("1");
                break;
            case BLACK:
                keys.add("isBlock");
                values.add("1");
                break;
            case FRIEND:
                keys.add("isBlock");
                values.add("0");
                break;
        }

        return map;
    }

    private String[] getQuery(List<String> list) {
        String[] s = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            s[i] = list.get(i);
        }
        return s;
    }

    private void setDataList(List<UserObj> list) {
        if (pba == null) {
            pba = new PeopleBaseAdapter(context, list);
            dataList.setAdapter(pba);
        } else {
            pba.addItems(list);
        }

    }

    private void closeRefresh() {
        if (dataListRefresh.isRefreshing()) {
            dataListRefresh.setRefreshing(false);
        }
    }

    private void downloadBlockList() {
        Map<String, List<String>> query = getQuery();
        String[] keys = getQuery(query.get("keys"));
        String[] values = getQuery(query.get("values"));

        String url = UrlHandle.getMmFriend() + "?query="
                + JsonHandle.getHttpJsonToString(keys, values)
                + "&sort=-createAt" + "&p=" + pageIndex + "&user_id="
                + UserObjHandle.getUsetId(context);

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        downloadData(url, params);
    }

    private void downloadFriendList() {
        Map<String, List<String>> query = getQuery();
        String[] keys = getQuery(query.get("keys"));
        String[] values = getQuery(query.get("values"));

        String url = UrlHandle.getMmFriend() + "?query="
                + JsonHandle.getHttpJsonToString(keys, values)
                + "&sort=m_nick_name" + "&p=" + pageIndex + "&user_id="
                + UserObjHandle.getUsetId(context);

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        downloadData(url, params);
    }

    private void downloadData() {
        Map<String, List<String>> query = getQuery();
        String[] keys = getQuery(query.get("keys"));
        String[] values = getQuery(query.get("values"));

        String url = UrlHandle.getMmUser() + "?query="
                + JsonHandle.getHttpJsonToString(keys, values)
                + "&sort=m_nick_name" + "&p=" + pageIndex + "&user_id="
                + UserObjHandle.getUsetId(context);

        RequestParams params = HttpUtilsBox.getRequestParams(context);
        downloadData(url, params);
    }

    private void downloadData(String url, RequestParams params) {
        progress.setVisibility(View.VISIBLE);

        HttpUtilsBox.getHttpUtil().send(HttpMethod.GET, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        progress.setVisibility(View.GONE);
                        closeRefresh();
                        ShowMessage.showFailure(context);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        progress.setVisibility(View.GONE);
                        closeRefresh();
                        String result = responseInfo.result;
                        Log.d("", result);
                        JSONObject json = JsonHandle.getJSON(
                                JsonHandle.getJSON(result), "result");
                        if (json != null) {
                            JSONArray array = JsonHandle.getArray(json, "data");
                            if (array != null) {
                                List<UserObj> list = UserObjHandle
                                        .getUserObjList(array);
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
