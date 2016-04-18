package com.zmyh.r.main.server;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.zmyh.r.baidumap.TreeMapActivityV2;
import com.zmyh.r.box.ChannelObj;
import com.zmyh.r.camera.CameraActivity;
import com.zmyh.r.download.DownloadImageLoader;
import com.zmyh.r.handler.ChannelObjHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.main.SandEmphasisActivity;
import com.zmyh.r.main.SandMuActivity;
import com.zmyh.r.main.SandQGEmphasisActivity;
import com.zmyh.r.main.user.UserFavorActivity;
import com.zmyh.r.main.user.UserPublishActivity;
import com.zmyh.r.photo.PhotoActivity;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.view.InsideGridView;

import org.json.JSONArray;

import java.util.List;

public class ServerFrameLayoutV3 extends Fragment {

    private Context context;

    @ViewInject(R.id.server_progress)
    private ProgressBar progress;
    @ViewInject(R.id.server_takePic)
    private ImageView takePic;
    @ViewInject(R.id.server_photo)
    private ImageView photo;
    @ViewInject(R.id.server_mapBtn)
    private ImageView mapBtn;
    @ViewInject(R.id.server_gy_icon)
    private ImageView gyIcon;
    @ViewInject(R.id.server_qg_icon)
    private ImageView qgIcon;
    @ViewInject(R.id.server_sxj_icon)
    private ImageView sxjIcon;
    @ViewInject(R.id.server_sc_icon)
    private ImageView scIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View contactsLayout = inflater.inflate(R.layout.layout_server_v3,
                container, false);
        ViewUtils.inject(this, contactsLayout);
        initActivity();
        return contactsLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick({R.id.server_takePic, R.id.server_photo, R.id.server_mapBtn, R.id.server_gy_icon, R.id.server_qg_icon, R.id.server_sxj_icon, R.id.server_sc_icon, R.id.server_send_gy, R.id.server_send_qg})
    public void onClick(View v) {
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.server_takePic:
                Passageway.jumpActivity(context, CameraActivity.class);
                break;
            case R.id.server_photo:
                Passageway.jumpActivity(context, PhotoActivity.class);
                break;
            case R.id.server_mapBtn:
                b.putString(TreeMapActivityV2.MM_CHANNEL, "00022");
                Passageway.jumpActivity(context, TreeMapActivityV2.class, b);
                break;
            case R.id.server_gy_icon:
                b.putString(ServerListActivity.MM_CHANNEL, "00002");
                b.putString(ServerListActivity.MM_TITLE, "供应信息");
                Passageway.jumpActivity(context, ServerListActivity.class,
                        b);
                break;
            case R.id.server_qg_icon:
                b.putString(ServerListActivity.MM_CHANNEL, "00001");
                b.putString(ServerListActivity.MM_TITLE, "求购信息");
                Passageway.jumpActivity(context, ServerListActivity.class,
                        b);
                break;
            case R.id.server_sxj_icon:
                if (UserObjHandle.isLogin(context,true)) {
                    Passageway.jumpActivity(context, UserPublishActivity.class);
                }
                break;
            case R.id.server_sc_icon:
                if (UserObjHandle.isLogin(context,true)) {
                    Passageway.jumpActivity(context, UserFavorActivity.class);
                }
                break;
            case R.id.server_send_gy:
                if (UserObjHandle.isLogin(context, true)) {
                    b.putString(SandEmphasisActivity.MM_CHANNEL, "00002");
                    b.putBoolean(SandEmphasisActivity.IS_EMPHASIS,
                            true);
                    Passageway.jumpActivity(context, SandEmphasisActivity.class,
                            SandEmphasisActivity.RC, b);
                }
                break;
            case R.id.server_send_qg:
                if (UserObjHandle.isLogin(context, true)) {
                    b.putString(SandEmphasisActivity.MM_CHANNEL, "00001");
                    b.putBoolean(SandEmphasisActivity.IS_EMPHASIS,
                            false);
                    Passageway.jumpActivity(context, SandQGEmphasisActivity.class,
                            SandEmphasisActivity.RC, b);
                }
                break;
        }
    }

    private void initActivity() {
        double w = WinTool.getWinWidth(context);
        double h = w / 128 * 71;
        mapBtn.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));

        double pw = w / 2;
        double ph = pw / 160 * 167;
        takePic.setLayoutParams(new LinearLayout.LayoutParams((int) pw,
                (int) ph));
        photo.setLayoutParams(new LinearLayout.LayoutParams((int) pw, (int) ph));

        double iw = w / 4;
        double ih = iw / 32 * 31;
        gyIcon.setLayoutParams(new LinearLayout.LayoutParams((int) iw, (int) ih));
        qgIcon.setLayoutParams(new LinearLayout.LayoutParams((int) iw, (int) ih));
        sxjIcon.setLayoutParams(new LinearLayout.LayoutParams((int) iw, (int) ih));
        scIcon.setLayoutParams(new LinearLayout.LayoutParams((int) iw, (int) ih));
    }

}
