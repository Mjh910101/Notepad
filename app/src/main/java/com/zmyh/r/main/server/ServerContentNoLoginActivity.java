package com.zmyh.r.main.server;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.zmyh.r.box.ServerObj;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Created by Hua on 15/8/31.
 */
public class ServerContentNoLoginActivity extends Activity {

    private Context context;

    private int pageIndex = 1, totalPage = 1;
    private ServerObj mServerObj;

    private List<ImageView> ballList;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.server_content_progress)
    private ProgressBar progress;
    @ViewInject(R.id.server_content_viewPagerBox)
    private RelativeLayout viewPagerBox;
    @ViewInject(R.id.server_content_viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.server_content_sizeBox)
    private LinearLayout sizeBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_content_logout);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        setContentBoxListener();
    }

    @OnClick({R.id.title_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
        }
    }

    private void setContentBoxListener() {
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                setOnBall(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    private void initActivity() {
        titleName.setText("详细");
        Bundle b = getIntent().getExtras();
        if (b != null) {
            downloadData(b.getString("id"));
        }
    }

    private void setContent(ServerObj obj) {
        setImageList(obj.getPic());
    }

    private void setImageList(List<String> list) {
        if (list != null && !list.isEmpty()) {
            viewPagerBox.setVisibility(View.VISIBLE);
            viewPager.setAdapter(new ContentPagerAdapter(list));
            viewPager.setOffscreenPageLimit(1);
            viewPager.setCurrentItem(0);
            setBallList(list.size(), 0);
        } else {
            viewPagerBox.setVisibility(View.GONE);
        }

    }

    private void setBallList(int size, int position) {
        if (size > 1) {
            int w = WinTool.pxToDip(context, 40);
            ballList = new ArrayList<ImageView>(size);
            for (int i = 0; i < size; i++) {
                ImageView v = new ImageView(context);
                v.setImageResource(R.drawable.ppt_off);
                v.setLayoutParams(new LinearLayout.LayoutParams(w, w));

                View l = new View(context);
                l.setLayoutParams(new LinearLayout.LayoutParams(w, w));

                sizeBox.addView(v);
                sizeBox.addView(l);

                ballList.add(v);
            }
            setOnBall(position);
        }
    }

    private void setOnBall(int p) {
        for (ImageView v : ballList) {
            v.setImageResource(R.drawable.ppt_off);
        }
        ballList.get(p).setImageResource(R.drawable.ppt_on);
    }

    private void downloadData(String id) {
        progress.setVisibility(View.VISIBLE);
        String url = UrlHandle.getMmPost(id) + "?user_id="
                + UserObjHandle.getUsetId(context);
        RequestParams params = HttpUtilsBox.getRequestParams(context);

        HttpUtilsBox.getHttpUtil().send(HttpRequest.HttpMethod.PUT, url, params,
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

                        JSONObject json = JsonHandle.getJSON(result);
                        if (!ShowMessage.showException(context, json)) {
                            JSONObject serverJson = JsonHandle.getJSON(json,
                                    "o");
                            if (serverJson != null) {
                                mServerObj = ServerObjHandler
                                        .getServerObj(serverJson);
                                mServerObj.setFavor(JsonHandle.getInt(json,
                                        ServerObj.IS_FAVOR) == 1);
                                setContent(mServerObj);
                            }
                        }

                    }

                });
    }

    class ContentPagerAdapter extends PagerAdapter {

        private List<String> iamgePhatList;
        private Map<Integer, ImageView> imageMap;

        public ContentPagerAdapter(List<String> iamgePhatList) {
            this.iamgePhatList = iamgePhatList;
            imageMap = new HashMap<Integer, ImageView>();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                container.removeView(imageMap.get(position));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getCount() {
            return iamgePhatList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView v;
            if (imageMap.containsKey(iamgePhatList.get(position))) {
                v = imageMap.get(iamgePhatList.get(position));
            } else {
                v = getImageView(iamgePhatList.get(position), position);
                imageMap.put(position, v);
            }
            Log.e("", iamgePhatList.get(position));
            container.addView(v, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            return v;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        private ImageView getImageView(String pic, final int position) {
            ImageView photoView = new ImageView(context);
            DownloadImageLoader.loadImage(photoView, pic);
            photoView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    Bundle b = new Bundle();
                    b.putStringArrayList("iamge_list",
                            (ArrayList<String>) iamgePhatList);
                    b.putInt("position", position);
                    b.putBoolean("isOnline", true);
                    Passageway
                            .jumpActivity(context, ImageListAcitvity.class, b);
                }
            });
            return photoView;
        }

    }

}
