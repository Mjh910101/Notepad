package com.zmyh.r.baidumap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
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
import com.zmyh.r.city.AreaActivity;
import com.zmyh.r.handler.CityObjHandler;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.ServerObjHandler;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.server.ServerListActivity;
import com.zmyh.r.main.server.ServerObjBox;
import com.zmyh.r.seek.SeekActivity;
import com.zmyh.r.seek.SeekDetailActivity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class TreeMapActivity extends Activity {

    private final static int MAP_ZOOM = 14;
    public final static int RC = 100;
    public final static String MM_CHANNEL = "mm_channel";
    public final static String IS_ONE = "is_one";

    private List<MapContentView> mListViews;
    private List<Overlay> mOverlayList;
    private List<ServerObj> mServiceObjList;
    private Marker onClickMarker;
    private int lastPosition = -1;

    @ViewInject(R.id.title_bg)
    private RelativeLayout titleBg;
    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_city)
    private TextView cityText;
    @ViewInject(R.id.title_back)
    private ImageView backIcon;
    @ViewInject(R.id.title_confirm)
    private TextView confirm;
    @ViewInject(R.id.treeMap_mapContext)
    private ViewPager mapContent;
    @ViewInject(R.id.treeMap_mapView)
    private MapView mMapView;
    @ViewInject(R.id.treeMap_progress)
    private ProgressBar progress;

    private BaiduMap mBaiduMap;
    private String mmArea = "", mmChannel = "";
    private Context context;
    private double lat = 0, lon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_tree_map);
        ViewUtils.inject(this);
        context = this;
        initActivity();
        setOnMarkerClickListener();
        setContentBoxListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        // ServerObjBox.deleteServerObj();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle b = data.getExtras();
            switch (requestCode) {
                case AreaActivity.RequestCode:
                    if (b.getBoolean("ok")) {
                        setCityMessage(b.getString("Area_name"),
                                b.getString("Area_id"));
                        setMapMessage(b.getDouble("Area_lat"),
                                b.getDouble("Area_lng"));
                        uploadMapMessage();
                    }
                    break;
                case RC:
                    if (b.getBoolean("ok")) {
                        uploadMapMessage(b.getString("url"));
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.title_back, R.id.title_confirm, R.id.title_titleName,
            R.id.title_city})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_confirm:
                jumpServerListActivity();
                break;
            case R.id.title_titleName:
                jumpSeekActivity();
                break;
            case R.id.title_city:
                setCityData();
                break;
        }
    }

    private void uploadMapMessage(String url) {
        mapContent.setAdapter(null);
        mListViews.removeAll(mListViews);

        for (Overlay mOverlay : mOverlayList) {
            Marker marker = (Marker) mOverlay;
            marker.remove();
        }
        mOverlayList.removeAll(mOverlayList);
        mBaiduMap.hideInfoWindow();

        mServiceObjList.removeAll(mServiceObjList);
        if (url != null) {
            downloadData(url);
        } else {
            downloadData();
        }
    }

    private void uploadMapMessage() {
        uploadMapMessage(null);
    }

    private void setCityData() {
        Bundle b = new Bundle();
        b.putBoolean(AreaActivity.COMMON, true);
        Passageway.jumpActivity(context, AreaActivity.class,
                AreaActivity.RequestCode, b);
    }

    private void jumpServerListActivity() {
        Bundle bundle = new Bundle();
        bundle.putString(ServerListActivity.MM_CHANNEL, mmChannel);
        bundle.putString(ServerListActivity.MM_TITLE, "苗木大全");
        Passageway.jumpActivity(context, ServerListActivity.class, bundle);
    }

    private void jumpSeekActivity() {
        Bundle b = new Bundle();
        b.putBoolean(SeekDetailActivity.IS_MAP, true);
        b.putString(SeekActivity.MM_AREA, mmArea);
        b.putString(SeekActivity.MM_CHANNEL, mmChannel);
        Passageway.jumpActivity(context, SeekDetailActivity.class, RC, b);
    }

    private void close() {
        ShowMessage.showToast(context, "地图信息不完整");
        finish();
    }

    private void initActivity() {
        initTitle();
        lat = CityObjHandler.getCityLat(context);
        lon = CityObjHandler.getCityLng(context);
        Log.e("", "lat : " + lat + " lon : " + lon);
        mBaiduMap = mMapView.getMap();

        mListViews = new ArrayList<MapContentView>();
        setMapMessage();
        Bundle b = getIntent().getExtras();
        if (lat <= 0 || lon <= 0 || b == null) {
            close();
        } else {
            mmChannel = b.getString(MM_CHANNEL);
            if (b.getBoolean(IS_ONE)) {
                setMapOverlay(ServerObjBox.getServerObj());
            } else {
                downloadData();
            }

        }
    }

    private void setCityMessage(String name, String id) {
        cityText.setText(name + "∨");
        mmArea = id;
    }

    private void initTitle() {
        int size = 16;
        int titleSize = 19;
        backIcon.setImageResource(R.drawable.back_green_icon);
        titleName.setText("树种∨");
        titleName.setTextColor(ColorBox.getColorForID(context,
                R.color.text_green));
        titleName.setTextSize(titleSize);
        titleBg.setBackgroundResource(R.color.white);
        confirm.setVisibility(View.VISIBLE);
        confirm.setText("列表");
        confirm.setTextColor(ColorBox
                .getColorForID(context, R.color.text_green));
        confirm.setBackgroundResource(R.drawable.white_background_green_frame_5);
        confirm.setPadding(20, 10, 20, 10);
        cityText.setVisibility(View.VISIBLE);
        cityText.setTextSize(size);
        setCityMessage(CityObjHandler.getCityName(context),
                CityObjHandler.getCityId(context));
    }

    private void setContentBoxListener() {
        mapContent.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Marker marker = setOnClickIcon(position);
                if (marker != null) {
                    setMapStatus(marker.getPosition());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void setOnMarkerClickListener() {
        mBaiduMap
                .setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        for (int i = 0; i < mOverlayList.size(); i++) {
                            Marker mMarker = (Marker) mOverlayList.get(i);
                            if (marker == mMarker) {
                                setOnClickIcon(i);
                                mapContent.setCurrentItem(i);
                                return false;
                            }
                        }
                        return false;
                    }

                });
    }

    private void setMapMessage() {
        setMapMessage(lat, lon);
    }

    private void setMapMessage(double lat, double lon) {
        Log.e("Baidu", "lat: : " + lat + "   lon : " + lon);
        LatLng point = new LatLng(lat, lon);
        initMapStart(point);
    }

    private void initMapStart(LatLng point) {
        MapStatus mMapStatus = new MapStatus.Builder().target(point)
                .zoom(MAP_ZOOM).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        mBaiduMap.animateMapStatus(mMapStatusUpdate, 500);
        setGoneZoom();
    }

    private void setGoneZoom() {
        int childCount = mMapView.getChildCount();
        View zoom = null;
        for (int i = 0; i < childCount; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                zoom = child;
                break;
            }
        }
        zoom.setVisibility(View.GONE);
    }

    private void setMapOverlay(ServerObj serverObj) {
        List<ServerObj> list = new ArrayList<ServerObj>();
        list.add(serverObj);
        setMapOverlay(list);
    }

    private void setMapOverlay(List<ServerObj> list) {
        addOverlay(list);
        setContent();
    }

    private void setContent() {
        mapContent.setAdapter(new ContentPagerAdapter(mListViews));
        mapContent.setCurrentItem(0);
        if (mOverlayList != null) {
            Marker marker = setOnClickIcon(0);
            if (marker != null) {
                setMapStatus(marker.getPosition(), MAP_ZOOM);
            }
        } else {
        }
    }

    private void setMapStatus(ServerObj obj) {
        LatLng point = new LatLng(obj.getMu_coordinate_lat(),
                obj.getMu_coordinate_long());
        setMapStatus(point, 0);
    }

    private void setMapStatus(LatLng point) {
        setMapStatus(point, 0);
    }

    private void setMapStatus(LatLng point, float zoom) {
        Log.e("zuobiao", point.latitude + " " + point.longitude);
        MapStatus mMapStatus;
        if (zoom > 0) {
            mMapStatus = new MapStatus.Builder().target(point).zoom(zoom)
                    .build();
        } else {
            mMapStatus = new MapStatus.Builder().target(point).build();
        }
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        mBaiduMap.animateMapStatus(mMapStatusUpdate, 500);
    }

    private Marker setOnClickIcon(int position) {
        if (!mOverlayList.isEmpty()) {
            BitmapDescriptor icon_marka_r = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_marka_r_v2);
            BitmapDescriptor icon_marka_b = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_marka_b_v2);
//			for (Overlay mOverlay : mOverlayList) {
//				Marker marker = (Marker) mOverlay;
//				marker.setIcon(icon_marka_r);
//			}
//
            Marker marker = (Marker) mOverlayList.get(position);
//			marker.setIcon(icon_marka_b);
//			showInfoWindow(marker, mServiceObjList.get(position));
            if (lastPosition > 0 && lastPosition < mOverlayList.size()) {

            }
            if (onClickMarker != null) {
                onClickMarker.remove();
                onClickMarker = null;
            }
            OverlayOptions option = new MarkerOptions().position(marker.getPosition()).icon(icon_marka_b);
            onClickMarker = (Marker) mBaiduMap.addOverlay(option);
            showInfoWindow(onClickMarker, mServiceObjList.get(position));
            return onClickMarker;
        }
        return null;
    }

    private void showInfoWindow(Marker marker, ServerObj obj) {
        MapImageView msg = new MapImageView(context, obj.getPost_thumbnail());
        InfoWindow mInfoWindow = new InfoWindow(msg, marker.getPosition(), -80);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    private void addOverlay(List<ServerObj> list) {
        mOverlayList = new ArrayList<Overlay>();
        mServiceObjList = new ArrayList<ServerObj>();
        for (ServerObj obj : list) {
            if (obj != null && obj.isHaveCoordinate()) {
                mServiceObjList.add(obj);
                mOverlayList
                        .add(addOverlay(obj, R.drawable.icon_marka_r_v2, true));
            }
        }
    }

    private Overlay addOverlay(ServerObj mServiceObj, int icon,
                               boolean isOnClick) {
        LatLng point = new LatLng(mServiceObj.getMu_coordinate_lat(),
                mServiceObj.getMu_coordinate_long());
        setContent(mServiceObj, isOnClick);
        return addOverlay(point, icon);
    }

    private void setContent(ServerObj mServiceObj, boolean isOnClick) {
        mListViews.add(new MapContentView(context, mServiceObj, isOnClick));
    }

    private Overlay addOverlay(LatLng point, int icon) {
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(icon);
        OverlayOptions option = new MarkerOptions().position(point)
                .icon(bitmap);
        return mBaiduMap.addOverlay(option);
    }

    private void downloadData() {
        String url = UrlHandle.getMmPost()
                + "?query="
                + JsonHandle.getHttpJsonToString(new String[]{"type",
                "mmChannel", "mmArea"}, new String[]{"services",
                mmChannel, mmArea}) + "&sort=-createAt";

        downloadData(url);
    }

    private void downloadData(String url) {
        progress.setVisibility(View.VISIBLE);

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

                        JSONObject json = JsonHandle.getJSON(result);
                        if (!ShowMessage.showException(context, json)) {
                            JSONArray array = JsonHandle.getArray(json,
                                    "result");
                            if (array != null) {
                                List<ServerObj> list = ServerObjHandler
                                        .getServerObjList(array);
                                setMapOverlay(list);
                            }
                        }
                    }

                });
    }

    class ContentPagerAdapter extends PagerAdapter {

        private List<MapContentView> mListViews;

        public ContentPagerAdapter(List<MapContentView> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

    }

}
