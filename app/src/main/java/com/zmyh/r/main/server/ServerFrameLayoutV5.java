package com.zmyh.r.main.server;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.baidumap.TreeMapActivityV2;
import com.zmyh.r.camera.CameraActivity;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.handler.WinTool;
import com.zmyh.r.main.SandEmphasisActivity;
import com.zmyh.r.main.SandQGEmphasisActivity;
import com.zmyh.r.main.user.UserFavorActivity;
import com.zmyh.r.main.user.UserPublishActivity;
import com.zmyh.r.photo.PhotoActivity;
import com.zmyh.r.tool.Passageway;

public class ServerFrameLayoutV5 extends Fragment {

    private Context context;

    @ViewInject(R.id.server_progress)
    private ProgressBar progress;
    @ViewInject(R.id.server_mapBtn)
    private ImageView mapBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity();
        View contactsLayout = inflater.inflate(R.layout.layout_server_v5,
                container, false);
        ViewUtils.inject(this, contactsLayout);
        initActivity();
        return contactsLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick({R.id.server_takePic, R.id.server_photo, R.id.server_mapBtn, R.id.server_gy_icon,
            R.id.server_qg_icon, R.id.server_sxj_icon, R.id.server_sc_icon, R.id.server_send_gy,
            R.id.server_send_qg, R.id.server_shopping})
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
                if (UserObjHandle.isLogin(context, true)) {
                    Passageway.jumpActivity(context, UserPublishActivity.class);
                }
                break;
            case R.id.server_sc_icon:
                if (UserObjHandle.isLogin(context, true)) {
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
            case R.id.server_shopping:
                showDialog("购物车功能即将开通");
                break;
        }
    }

    private void showDialog(String s) {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage(s);
        dialog.setCommitStyle("好的");
        dialog.setCommitListener(null);
    }

    private void initActivity() {
        double w = WinTool.getWinWidth(context);
        double h = w / 128 * 71;
        mapBtn.setLayoutParams(new LinearLayout.LayoutParams((int) w, (int) h));
    }

}
