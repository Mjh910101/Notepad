package com.zmyh.r.baidumap;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.server.ServerContentActivity;
import com.zmyh.r.main.server.ServerContentNoLoginActivity;
import com.zmyh.r.main.server.ServerListActivity;
import com.zmyh.r.main.server.ServerObjBox;
import com.zmyh.r.seek.SeekActivity;
import com.zmyh.r.seek.SeekDetailActivity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TreeMapActivityV2 extends Activity {

    private final static int MAP_ZOOM = 14;
    public final static int RC = 100;
    public final static String MM_CHANNEL = "mm_channel";
    public final static String IS_ONE = "is_one";

    //    private List<MapContentView> mListViews;
    private List<Overlay> mOverlayList;
    private List<ServerObj> mServiceObjList;
    private Marker onClickMarker;
    private int lastPosition = -1;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.treeMap_mapContext)
    private Gallery mapGallery;
    @ViewInject(R.id.treeMap_mapView)
    private MapView mMapView;
    @ViewInject(R.id.treeMap_progress)
    private ProgressBar progress;
    @ViewInject(R.id.treeMap_cityText)
    private TextView cityText;

    private BaiduMap mBaiduMap;
    private String mmArea = "", mmChannel = "";
    private Context context;
    private double lat = 0, lon = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_tree_map_v2);
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
                                b.getDouble("Area_lng"), false);
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

    @OnClick({R.id.title_back, R.id.title_confirm, R.id.treeMap_muText,
            R.id.treeMap_cityText})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_confirm:
                jumpServerListActivity();
                break;
            case R.id.treeMap_muText:
                jumpSeekActivity();
                break;
            case R.id.treeMap_cityText:
                setCityData();
                break;
        }
    }

    private void uploadMapMessage(String url) {
//        mapGallery.setAdapter(null);
//        mListViews.removeAll(mListViews);

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

//        mListViews = new ArrayList<MapContentView>();
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
        cityText.setText(name);
        mmArea = id;
    }

    private void initTitle() {
        titleName.setText("苗木地图");
        setCityMessage(CityObjHandler.getCityName(context),
                CityObjHandler.getCityId(context));
    }

    private void setContentBoxListener() {
        mapGallery.setCallbackDuringFling(false);//停止时返回位置
        mapGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, long id) {
                System.out.println("item = " + id);
                Marker marker = setOnClickIcon(position);
                if (marker != null) {
                    setMapStatus(marker.getPosition());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

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
                                mapGallery.setSelection(i);
                                return false;
                            }
                        }
                        return false;
                    }

                });
    }

    private void setMapMessage() {
        setMapMessage(lat, lon, true);
    }

    private void setMapMessage(double lat, double lon, boolean isRemove) {
        Log.e("Baidu", "lat: : " + lat + "   lon : " + lon);
        LatLng point = new LatLng(lat, lon);
        initMapStart(point, isRemove);
    }

    private void initMapStart(LatLng point, boolean isRemove) {
        MapStatus mMapStatus = new MapStatus.Builder().target(point)
                .zoom(MAP_ZOOM).build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
                .newMapStatus(mMapStatus);
        mBaiduMap.animateMapStatus(mMapStatusUpdate, 500);
        if (isRemove) {
            setGoneZoom();
            mMapView.removeViewAt(1);
            mMapView.removeViewAt(2);
        }
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

    private void setGoneScale() {
        int count = mMapView.getChildCount();
        View scale = null;
        for (int i = 0; i < count; i++) {
            View child = mMapView.getChildAt(i);
            if (child instanceof ZoomControls) {
                scale = child;
                break;
            }

        }
        scale.setVisibility(View.GONE);
    }

    private void setMapOverlay(ServerObj serverObj) {
        List<ServerObj> list = new ArrayList<ServerObj>();
        list.add(serverObj);
        setMapOverlay(list);
    }

    private void setMapOverlay(List<ServerObj> list) {
        addOverlay(list);
        setContent(list);
    }

    private void setContent(List<ServerObj> list) {
        mapGallery.setAdapter(new ContentBaseAdapter(list));
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
//        setContent(mServiceObj, isOnClick);
        return addOverlay(point, icon);
    }

//    private void setContent(ServerObj mServiceObj, boolean isOnClick) {
//        mListViews.add(new MapContentView(context, mServiceObj, isOnClick));
//    }

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
                mmChannel, mmArea}) + "&sort=-createAt&p=1&l=99";

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

                        JSONObject json = JsonHandle.getJSON(JsonHandle.getJSON(result), "result");
                        if (!ShowMessage.showException(context, json)) {
                            JSONArray array = JsonHandle.getArray(json,
                                    "data");
                            if (array != null) {
                                List<ServerObj> list = ServerObjHandler
                                        .getServerObjList(array);
                                setMapOverlay(list);
                            }
                        }
                    }

                });
    }

    class ContentBaseAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<ServerObj> list;

        public ContentBaseAdapter(List<ServerObj> list) {
            this.list = list;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.map_content, null);
            }

            ServerObj obj = list.get(i);
            setContentView(view, obj);
            setContentWidth(view);
            setOnClick(view, obj);

            return view;
        }

        private void setOnClick(View view, final ServerObj obj) {
            TextView content = (TextView) view.findViewById(R.id.mapContent_content);
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putString("id", obj.getId());
                    if (UserObjHandle.isLogin(context)) {
                        Passageway
                                .jumpActivity(context, ServerContentActivity.class, b);
                    } else {
                        Passageway
                                .jumpActivity(context, ServerContentNoLoginActivity.class, b);
                    }
//                    Passageway.jumpActivity(context, ServerContentActivity.class, b);
                }
            });
        }

        private void setContentWidth(View view) {
            LinearLayout contentBox = (LinearLayout) view.findViewById(R.id.mapContent_box);
            int width = WinTool.getWinWidth(context) / 10 * 8;
            contentBox.setLayoutParams(new Gallery.LayoutParams(width, Gallery.LayoutParams.WRAP_CONTENT));
        }

        private void setContentView(View view, ServerObj obj) {
            TextView name = (TextView) view.findViewById(R.id.mapContent_name);
            TextView people = (TextView) view.findViewById(R.id.mapContent_people);
            TextView intro = (TextView) view.findViewById(R.id.mapContent_intro);

            name.setText(obj.getTitle());
            people.setText("联系人:" + obj.getName() + "("
                    + obj.getPhone() + ")");
            intro.setText(obj.getInfo(false));
        }
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
            return mListViews.size() - 1;
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
