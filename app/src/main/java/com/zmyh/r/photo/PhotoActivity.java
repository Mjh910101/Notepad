package com.zmyh.r.photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.dailog.InputDialog;
import com.zmyh.r.dailog.ListDialog;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.dailog.MessageDialog.CallBackListener;
import com.zmyh.r.dao.DBHandler;
import com.zmyh.r.handler.CameraPicObjHandler;
import com.zmyh.r.handler.CameraPicObjHandler.CamerahandlerListener;
import com.zmyh.r.handler.ColorBox;
import com.zmyh.r.handler.TroopObjHandler;
import com.zmyh.r.handler.TroopObjHandler.TroophandleListener;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.interfaces.CallbackForString;
import com.zmyh.r.main.SandEmphasisActivity;
import com.zmyh.r.main.SandMuActivity;
import com.zmyh.r.main.forum.ShareForumActivity;
import com.zmyh.r.main.user.LoginActivity;
import com.zmyh.r.main.user.RegisterActivity;
import com.zmyh.r.main.user.RegisterActivityV2;
import com.zmyh.r.photo.adapter.MntPicBaseAdapter;
import com.zmyh.r.photo.adapter.MntTroopBaseAdapter;
import com.zmyh.r.photo.adapter.OnlinePicBaseAdapter;
import com.zmyh.r.photo.adapter.OnlineTroopBaseAdapter;
import com.zmyh.r.photo.adapter.PicBaseAdapter;
import com.zmyh.r.photo.adapter.TroopBaseAdapter;
import com.zmyh.r.seek.SeekPhoneActivity;
import com.zmyh.r.seek.SeekPhotoActivityV2;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.upload.UploadBinder;
import com.zmyh.r.upload.services.UploadService;
import com.zmyh.r.upload.services.UploadService.UploadServiceListener;

public class PhotoActivity extends Activity {

    public final static int ResultCode = 1024;

    private final static int MNT_TROOP = 0;
    private final static int ONLINE_TROOP = 1;
    private final static int MNT_IMAGE = 2;
    private final static int ONLINE_IMAGE = 3;

    private final static String[] DATA_TAP_LIST = new String[]{"本地苗木分组",
            "云端苗木分组", "本地高清大图", "云端高清大图"};

    private static boolean isShow = true;

    private Context context;
    private int now_tap, pageIndex = 0;
    private String gsNameText = "";
    private boolean isUpload = true;

    private DbUtils dbHandler;
    private UploadService mUploadService;

    private TroopBaseAdapter troopBaseAdapter;
    private PicBaseAdapter picBaseAdapter;

    private MessageDialog messageDialog;

    @ViewInject(R.id.title_titleName)
    private TextView titleName;
    @ViewInject(R.id.title_seek)
    private ImageView seekIcon;
    @ViewInject(R.id.title_addTroop)
    private ImageView addTroop;
    @ViewInject(R.id.photo_tapName)
    private TextView tapName;
    @ViewInject(R.id.photo_detaList)
    private ListView dataList;
    @ViewInject(R.id.photo_upload)
    private TextView uploadBtn;
    @ViewInject(R.id.photo_delete)
    private TextView deleteBtn;
    @ViewInject(R.id.photo_form)
    private TextView formBtn;
    @ViewInject(R.id.photo_share)
    private TextView shateBtn;
    @ViewInject(R.id.photo_allChoice)
    private ImageView allChoice;
    @ViewInject(R.id.photo_progress)
    private ProgressBar progress;
    @ViewInject(R.id.photo_gsName)
    private TextView gsName;
    @ViewInject(R.id.photo_loginBox)
    private RelativeLayout loginBox;
    @ViewInject(R.id.photo_line_h)
    private View lineH;
    @ViewInject(R.id.photo_line_w)
    private View lineW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        initToolBtn();
        isLogin();
        setDataListScrollListener();
        setDataTap(MNT_TROOP);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TroopObjBox.removeObj();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ResultCode:
                if (data != null) {
                    if (data.getExtras().getBoolean("isUpload")) {
                        setDataTap(MNT_TROOP);
                    }
                } else {
                    if (troopBaseAdapter != null) {
                        troopBaseAdapter.notifyDataSetChanged();
                    }
                    if (picBaseAdapter != null) {
                        picBaseAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
        switch (requestCode) {
            case Passageway.VIDEO_REQUEST_CODE:
                if (troopBaseAdapter != null) {
                    troopBaseAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @OnClick({R.id.title_back, R.id.title_seek, R.id.title_addTroop,
            R.id.photo_tapName, R.id.photo_allChoice, R.id.photo_toolTitle,
            R.id.photo_upload, R.id.photo_delete, R.id.photo_form,
            R.id.photo_gsName, R.id.photo_loginColse, R.id.photo_login,
            R.id.photo_register, R.id.photo_share})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_addTroop:
                Passageway.jumpActivity(context, NewTroopActivity.class,
                        PhotoActivity.ResultCode);
                break;
            case R.id.title_seek:
                Passageway.jumpActivity(context, SeekPhotoActivityV2.class);
                break;
            case R.id.photo_tapName:
                showTapList();
                break;
            case R.id.photo_allChoice:
            case R.id.photo_toolTitle:
                setAllChoice();
                break;
            case R.id.photo_upload:
                uploadBtn();
                break;
            case R.id.photo_delete:
                deleteBtn();
                break;
            case R.id.photo_form:
                formBtn();
                break;
            case R.id.photo_gsName:
                showGsList();
                break;
            case R.id.photo_loginColse:
                closeLoginBox();
                break;
            case R.id.photo_login:
                Passageway.jumpActivity(context, LoginActivity.class);
                break;
            case R.id.photo_register:
                Passageway.jumpActivity(context, RegisterActivityV2.class);
                break;
            case R.id.photo_share:
                shareBtn();
                break;
        }
    }

    private void showGsList() {
        final List<String> list = TroopObjHandler.getTroopMugsList(context);
        list.add(0, "苗木归属");
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(list);
        dialog.setLayout();
        dialog.setItemListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int p,
                                    long arg3) {
                dialog.dismiss();
                if (p == 0) {
                    seekGs("");
                } else {
                    seekGs(list.get(p));
                }
            }
        });
    }

    private void showInputGsName() {
        InputDialog dialog = new InputDialog(context);
        dialog.setHint("请输入你要查找的苗木归属");
        dialog.setListener(new CallbackForString() {

            @Override
            public void callback(String result) {
                if (result != null) {
                    seekGs(result);
                }
            }

        });
    }

    private void seekGs(String result) {
        pageIndex = 0;
        picBaseAdapter = null;
        troopBaseAdapter = null;
        dataList.setAdapter(null);
        if (result.equals("")) {
            gsName.setText("苗木归属");
            setMntTroopDataList();
        } else {
            gsName.setText(result);
            setMntTroopDataList(result);
        }
    }

    private void setDataListScrollListener() {
        dataList.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= (view.getCount() - 1)) {
                        if (progress.getVisibility() == View.GONE && isUpload) {
                            uploadDataList();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

            }
        });
    }

    private void setAllChoice() {
        switch (now_tap) {
            case MNT_TROOP:
            case ONLINE_TROOP:
                if (troopBaseAdapter != null) {
                    troopBaseAdapter.setAllChoice(!troopBaseAdapter.isAllChoice());
                }
                break;
            case MNT_IMAGE:
            case ONLINE_IMAGE:
                if (picBaseAdapter != null) {
                    picBaseAdapter.setAllChoice(!picBaseAdapter.isAllChoice());
                }
                break;
        }
    }

    private void shareBtn() {
        if (troopBaseAdapter != null) {
            if (troopBaseAdapter.isChoiceOne()) {
                if (troopBaseAdapter.getChoiceList().size() > 1) {
                    ShowMessage.showToast(context, "一次只能分享一组");
                } else {
                    if (UserObjHandle.isLogin(context, true)) {
                        showShareDialog();
                    }
                }

            } else {
                ShowMessage.showToast(context, "请先选择");
            }
        }
    }

    private void showShareDialog() {
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(new String[]{"分享到微信", "分享到朋友圈", "分享到供应", "分享到苗木圈"});
        dialog.setItemListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int p,
                                    long arg3) {
                TroopObjBox.saveTroopObj(troopBaseAdapter.getChoiceList()
                        .get(0));
                Bundle b = new Bundle();
                switch (p) {
                    case 0:
                        b.putBoolean(ShareTroopActivity.IS_FRIEND, false);
                        Passageway.jumpActivity(context, ShareTroopActivity.class,
                                b);
                        break;
                    case 1:
                        b.putBoolean(ShareTroopActivity.IS_FRIEND, true);
                        Passageway.jumpActivity(context, ShareTroopActivity.class,
                                b);
                        break;
                    case 2:
                        b.putString(SandEmphasisActivity.MM_CHANNEL, "00002");
                        b.putBoolean(SandEmphasisActivity.IS_EMPHASIS, true);
                        Passageway.jumpActivity(context,
                                SandEmphasisActivity.class, b);
                        break;
                    case 3:
                        b.putBoolean(ShareForumActivity.IS_EMPHASIS, true);
                        Passageway.jumpActivity(context,
                                ShareForumActivity.class, b);
                        break;
//					case 4:
//						b.putString(SandEmphasisActivity.MM_CHANNEL, "00022");
//						b.putBoolean(SandEmphasisActivity.IS_EMPHASIS, true);
//						Passageway.jumpActivity(context,
//								SandMuActivity.class, b);
//						break;
                }
                dialog.dismiss();
            }
        });
    }

    private void formBtn() {
        if (troopBaseAdapter != null) {
            if (troopBaseAdapter.isChoiceOne()) {
                createForm(troopBaseAdapter.getChoiceList());
            } else {
                ShowMessage.showToast(context, "请先选择");
            }
        }
    }

    private void createForm(List<TroopObj> choiceList) {
        TroopObjBox.savaTroopList(choiceList);
        Passageway.jumpActivity(context, FormActivity.class);
    }

    private void deleteBtn() {
        if (!UserObjHandle.isLogin(context, true)) {
            return;
        }
        switch (now_tap) {
            case MNT_TROOP:
                if (troopBaseAdapter != null) {
                    if (troopBaseAdapter.isChoiceOne()) {
                        deleteTroop(troopBaseAdapter.getChoiceList());
                    } else {
                        ShowMessage.showToast(context, "请先选择");
                    }
                }
                break;
            case ONLINE_TROOP:
                if (troopBaseAdapter != null) {
                    if (troopBaseAdapter.isChoiceOne()) {
                        deleteOnlineTroop(troopBaseAdapter.getChoiceList());
                    } else {
                        ShowMessage.showToast(context, "请先选择");
                    }
                }
                break;
            case MNT_IMAGE:
                if (picBaseAdapter != null) {
                    if (picBaseAdapter.isChoiceOne()) {
                        deleteCameraPic(picBaseAdapter.getChoiceList());
                    } else {
                        ShowMessage.showToast(context, "请先选择");
                    }
                }
                break;
            case ONLINE_IMAGE:
                if (picBaseAdapter != null) {
                    if (picBaseAdapter.isChoiceOne()) {
                        deleteOnlineCameraPic(picBaseAdapter.getChoiceList());
                    } else {
                        ShowMessage.showToast(context, "请先选择");
                    }
                }
                break;
        }
    }

    private void deleteOnlineCameraPic(final List<CameraPicObj> choiceList) {
        showDeleteDialog("是否删除选择的图片", new CallBackListener() {

            @Override
            public void callback() {
                deleteOnlinePic(choiceList);
            }

        });
    }

    private void deleteOnlinePic(List<CameraPicObj> choiceList) {
        progress.setVisibility(View.VISIBLE);
        CameraPicObjHandler.deleteOnlineMaxPic(context, choiceList,
                new CamerahandlerListener() {

                    @Override
                    public void onSuccess(List<CameraPicObj> dataList) {
                        progress.setVisibility(View.GONE);
                        setDataTap(now_tap);
                    }

                    @Override
                    public void onFailure(HttpException exception) {
                        progress.setVisibility(View.GONE);
                    }
                });
    }

    private void deleteCameraPic(final List<CameraPicObj> choiceList) {
        showDeleteDialog("是否删除选择的图片", new CallBackListener() {

            @Override
            public void callback() {
                for (CameraPicObj obj : choiceList) {
                    obj.setShow_max_pic(false);
                    CameraPicObjHandler.saveCameraPicObj(context, obj);
                }
                setDataTap(now_tap);
            }
        });
    }

    private void deleteOnlineTroop(final List<TroopObj> choiceList) {
        showDeleteDialog("是否删除选择的分组", new CallBackListener() {

            @Override
            public void callback() {
                deleteOnlineTroopObj(choiceList);
            }

        });
    }

    private void deleteOnlineTroopObj(List<TroopObj> choiceList) {
        progress.setVisibility(View.VISIBLE);
        TroopObjHandler.deleteOnlineTroopObj(context, choiceList,
                new TroophandleListener() {

                    @Override
                    public void onSuccess(List<TroopObj> dataList) {
                        progress.setVisibility(View.GONE);
                        setDataTap(now_tap);
                    }

                    @Override
                    public void onFailure(HttpException exception) {
                        progress.setVisibility(View.GONE);
                    }
                });
    }

    private void deleteTroop(final List<TroopObj> choiceList) {
        showDeleteDialog("是否删除选择的分组", new CallBackListener() {

            @Override
            public void callback() {
                for (TroopObj obj : choiceList) {
                    obj.setShow(false);
                    TroopObjHandler.saveTroopObj(context, obj);
                }
                setDataTap(now_tap);
            }
        });
    }

    private void showDeleteDialog(String title, CallBackListener callback) {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage(title);
        dialog.setCancelStyle("取消");
        dialog.setCancelListener(null);
        dialog.setCommitStyle("确定");
        dialog.setCommitListener(callback);
    }

    private void uploadBtn() {
        if (!UserObjHandle.isLogin(context, true)) {
            return;
        }
        switch (now_tap) {
            case MNT_TROOP:
                if (troopBaseAdapter != null) {
                    if (troopBaseAdapter.isChoiceOne()) {
                        uploadTroop(troopBaseAdapter.getChoiceList());
                    } else {
                        ShowMessage.showToast(context, "请先选择");
                    }
                }
                break;
            case ONLINE_TROOP:
                if (troopBaseAdapter != null) {
                    if (troopBaseAdapter.isChoiceOne()) {
                        downloadTroop(troopBaseAdapter.getChoiceList());
                    } else {
                        ShowMessage.showToast(context, "请先选择");
                    }
                }
                break;
            case MNT_IMAGE:
                if (picBaseAdapter != null) {
                    if (picBaseAdapter.isChoiceOne()) {
                        childSeekTroop(picBaseAdapter.getChoiceList());
                    } else {
                        ShowMessage.showToast(context, "请先选择");
                    }
                }
                break;
            case ONLINE_IMAGE:
                if (picBaseAdapter != null) {
                    if (picBaseAdapter.isChoiceOne()) {
                        downloadCameraPic(picBaseAdapter.getChoiceList());
                    } else {
                        ShowMessage.showToast(context, "请先选择");
                    }
                }
                break;
        }
    }

    private void downloadCameraPic(List<CameraPicObj> choiceList) {
        for (CameraPicObj obj : choiceList) {
            ((OnlinePicBaseAdapter) picBaseAdapter).downloadPic(obj);
        }
    }

    private void childSeekTroop(List<CameraPicObj> choiceList) {
        boolean isShow = false;
        List<TroopObj> uploadList = new ArrayList<TroopObj>();
        for (CameraPicObj obj : choiceList) {
            if (obj.getMax_state() != CameraPicObj.FINISH) {
                TroopObj fObj = isHaveChild(uploadList, obj);
                if (fObj != null) {
                    fObj.addChild(obj);
                } else {
                    TroopObj newFobj = new TroopObj();
                    newFobj.setMu_id(obj.getMu_id());
                    newFobj.addChild(obj);
                    uploadList.add(newFobj);
                }
            } else {
                isShow = true;
            }
        }
        mUploadService.addTroopObj(context, uploadList, "o");
    }

    private TroopObj isHaveChild(List<TroopObj> uploadList, CameraPicObj obj) {
        if (uploadList.isEmpty()) {
            return null;
        }
        for (TroopObj mTroopObj : uploadList) {
            if (mTroopObj.getMu_id().equals(obj.getMu_id())) {
                return mTroopObj;
            }
        }
        return null;
    }

    private void downloadTroop(List<TroopObj> choiceList) {
        boolean isOld = false;
        for (TroopObj obj : choiceList) {
            try {
                if (TroopObjHandler.getTroopObj(dbHandler, obj.getMu_id()) != null) {
                    isOld = true;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        if (isOld) {
            showOldMessage(choiceList);
        } else {
            saveOnlineTroop(choiceList);
        }
    }

    private void saveOnlineTroop(List<TroopObj> list) {
        for (TroopObj obj : list) {
            obj.setUpload(false);
            obj.setOnline(true);
            try {
                dbHandler.saveOrUpdate(obj);
                for (CameraPicObj c : obj.getCameraPicObjList()) {
                    dbHandler.saveOrUpdate(c);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        setDataTap(now_tap);
    }

    private void showOldMessage(final List<TroopObj> list) {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("本地存在，是否覆盖");
        dialog.setCancelStyle("取消");
        dialog.setCancelListener(null);
        dialog.setCommitStyle("覆盖");
        dialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                saveOnlineTroop(list);
            }

        });
    }

    private void uploadTroop(List<TroopObj> choiceList) {
        showMessageDialog();
        mUploadService.addTroopObj(context, choiceList, "m");
    }

    private void setSynchronization(int i) {
        if (i >= 100) {
            String keys = CameraPicObjHandler.getDeleteListString(context);
            if (keys.equals("")) {
                closeMessageDialog();
            } else {
                deletePic(keys);
            }
        }
    }

    private void deletePic(String keys) {
        CameraPicObjHandler.deleteOnlinePic(context, keys,
                new CamerahandlerListener() {

                    @Override
                    public void onSuccess(List<CameraPicObj> dataList) {
                        closeMessageDialog();
                    }

                    @Override
                    public void onFailure(HttpException exception) {
                        closeMessageDialog();
                        ShowMessage.showToast(context, "同步图片出错");
                    }
                });
    }

    private void showMessageDialog() {
        messageDialog = new MessageDialog(context);
        messageDialog.setMessage("操作进行中，请耐心等待" + "\n" + "将自动跳过已上传的图片!~");
        messageDialog.setCommitStyle("停止");
        messageDialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                mUploadService.stop();
                setDataTap(now_tap);
            }
        });
    }

    private void closeMessageDialog() {
        if (messageDialog != null) {
            messageDialog.dismiss();
            setDataTap(now_tap);
        }
    }

    private void isAllChoice() {
        if (troopBaseAdapter != null) {
            if (troopBaseAdapter.isAllChoice()) {
                allChoice.setImageResource(R.drawable.on_click);
            } else {
                allChoice.setImageResource(R.drawable.on_click_off);
            }
        }
        if (picBaseAdapter != null) {
            if (picBaseAdapter.isAllChoice()) {
                allChoice.setImageResource(R.drawable.on_click);
            } else {
                allChoice.setImageResource(R.drawable.on_click_off);
            }
        }
    }

    private void initActivity() {
        titleName.setText("相册");
        seekIcon.setVisibility(View.VISIBLE);
        addTroop.setImageResource(R.drawable.add_troop_icon);
        dbHandler = DBHandler.getDbUtils(context);

        Intent intent = new Intent("com.zmyh.r.UploadService");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    private void closeLoginBox() {
        isShow = false;
        loginBox.setVisibility(View.GONE);
    }

    private void isLogin() {
        if (!UserObjHandle.isLogin(context)) {
            if (isShow) {
                loginBox.setVisibility(View.VISIBLE);
            }
        } else {
            closeLoginBox();
        }

    }

    private void showTapList() {
        final ListDialog dialog = new ListDialog(context);
        dialog.setTitleGone();
        dialog.setList(DATA_TAP_LIST);
        dialog.setItemListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int p,
                                    long arg3) {
                dialog.dismiss();
                setDataTap(p);
            }
        });
    }

    private void uploadDataList() {
        switch (now_tap) {
            case MNT_TROOP:
                setMntTroopDataList();
                break;
            case ONLINE_TROOP:
                // downloadTroopData();
                break;
            case MNT_IMAGE:
                // setMntPicDataList();
                break;
            case ONLINE_IMAGE:
                // downloadPicData();
                break;
        }
    }

    private void setDataTap(int i) {
        now_tap = i;
        tapName.setText(DATA_TAP_LIST[i]);
        emptyData();
        switch (i) {
            case MNT_TROOP:
                uploadBtn.setText("上传");
                gsName.setVisibility(View.VISIBLE);
                formBtn.setVisibility(View.VISIBLE);
                shateBtn.setVisibility(View.VISIBLE);
                lineH.setVisibility(View.VISIBLE);
                setMntTroopDataList();
                break;
            case ONLINE_TROOP:
                uploadBtn.setText("下载");
                shateBtn.setVisibility(View.VISIBLE);
                lineH.setVisibility(View.VISIBLE);
                gsName.setVisibility(View.VISIBLE);
                formBtn.setVisibility(View.VISIBLE);
                downloadTroopData();
                break;
            case MNT_IMAGE:
                uploadBtn.setText("上传");
                lineW.setVisibility(View.VISIBLE);
                setMntPicDataList();
                break;
            case ONLINE_IMAGE:
                uploadBtn.setText("下载");
                lineW.setVisibility(View.VISIBLE);
                downloadPicData();
                break;
        }

    }

    private void emptyData() {
        pageIndex = 0;
        picBaseAdapter = null;
        troopBaseAdapter = null;
        dataList.setAdapter(null);
        allChoice.setImageResource(R.drawable.on_click_off);
        formBtn.setVisibility(View.GONE);
        shateBtn.setVisibility(View.GONE);
        gsName.setVisibility(View.INVISIBLE);
        lineH.setVisibility(View.GONE);
        lineW.setVisibility(View.GONE);
    }

    private void setMntPicDataList() {
        progress.setVisibility(View.VISIBLE);
        // List<CameraPicObj> list = CameraPicObjHandler.getCameraPicObjList(
        // dbHandler, pageIndex);
        ArrayList<CameraPicObj> list = new ArrayList<CameraPicObj>();
        File[] files = new File(FileUtil.getOriginalPath()).listFiles();
        if (files != null) {
            for (int i = files.length - 1; i >= 0; i--) {
                File f = files[i];
                if (!f.isDirectory()) {
                    try {
                        String name = f.getName().replace("o_", "")
                                .replace(".jpg", "").replace(".png", "");
                        CameraPicObj obj = CameraPicObjHandler
                                .getMaxCameraPicObj(dbHandler, name);
                        if (obj != null) {
                            list.add(obj);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        setPicDataList(list, false);
    }

    private void setPicDataList(List<CameraPicObj> list, boolean isOnline) {
        if (list != null && !list.isEmpty()) {
            if (picBaseAdapter == null) {
                if (isOnline) {
                    picBaseAdapter = new OnlinePicBaseAdapter(context, list);
                } else {
                    picBaseAdapter = new MntPicBaseAdapter(context, list);
                    ((MntPicBaseAdapter) picBaseAdapter)
                            .setUploadService(mUploadService);
                }
                picBaseAdapter.setIsClickListener(uploadListener);
                dataList.setAdapter(picBaseAdapter);
            } else {
                picBaseAdapter.addItem(list);
            }
            pageIndex += 1;
        } else {
            isUpload = false;
        }
        progress.setVisibility(View.GONE);
    }

    private void downloadPicData() {
        if (UserObjHandle.isLogin(context, true)) {
            progress.setVisibility(View.VISIBLE);
            CameraPicObjHandler.downloadCameraPicObj(context,
                    new CamerahandlerListener() {

                        @Override
                        public void onSuccess(List<CameraPicObj> dataList) {
                            setPicDataList(dataList, true);
                        }

                        @Override
                        public void onFailure(HttpException exception) {
                            exception.printStackTrace();
                            progress.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private void setMntTroopDataList(String gs) {
        progress.setVisibility(View.VISIBLE);
        List<TroopObj> list = TroopObjHandler.getTroopObjList(dbHandler, gs,
                pageIndex);
        setTroopDataList(list, false);
    }

    private void setMntTroopDataList() {
        progress.setVisibility(View.VISIBLE);
        List<TroopObj> list = TroopObjHandler.getTroopObjList(dbHandler,
                pageIndex);
        setTroopDataList(list, false);
    }

    private void setTroopDataList(List<TroopObj> list, boolean isOnline) {
        if (list != null && !list.isEmpty()) {
            if (troopBaseAdapter == null) {
                if (isOnline) {
                    troopBaseAdapter = new OnlineTroopBaseAdapter(context, list);
                } else {
                    troopBaseAdapter = new MntTroopBaseAdapter(context, list);
                }
                troopBaseAdapter.setIsClickListener(uploadListener);
                dataList.setAdapter(troopBaseAdapter);
            } else {
                troopBaseAdapter.addItem(list);
            }
            pageIndex += 1;
        } else {
            isUpload = false;
        }
        progress.setVisibility(View.GONE);
    }

    private void downloadTroopData() {
        if (UserObjHandle.isLogin(context, true)) {
            progress.setVisibility(View.VISIBLE);
            TroopObjHandler.downloadTroopObj(context,
                    new TroophandleListener() {

                        @Override
                        public void onSuccess(List<TroopObj> dataList) {
                            setTroopDataList(dataList, true);
                        }

                        @Override
                        public void onFailure(HttpException exception) {
                            progress.setVisibility(View.GONE);
                            exception.printStackTrace();
                        }
                    });
        }
    }

    private void initToolBtn() {
        uploadBtn.setBackgroundResource(R.drawable.download_botton);
        uploadBtn.setTextColor(ColorBox.getColorForID(context,
                R.color.text_green));
        deleteBtn.setBackgroundResource(R.drawable.delete_botton);
        deleteBtn.setTextColor(ColorBox.getColorForID(context, R.color.red));
        formBtn.setBackgroundResource(R.drawable.download_botton);
        formBtn.setTextColor(ColorBox
                .getColorForID(context, R.color.text_green));
        shateBtn.setBackgroundResource(R.drawable.download_botton);
        shateBtn.setTextColor(ColorBox.getColorForID(context,
                R.color.text_green));
    }

    private CallbackForBoolean uploadListener = new CallbackForBoolean() {

        @Override
        public void callback(boolean b) {
            // if (b) {
            // uploadBtn.setBackgroundResource(R.drawable.download_botton);
            // uploadBtn.setTextColor(ColorBox.getColorForID(context,
            // R.color.text_green));
            // deleteBtn.setBackgroundResource(R.drawable.delete_botton);
            // deleteBtn.setTextColor(ColorBox.getColorForID(context,
            // R.color.red));
            // formBtn.setBackgroundResource(R.drawable.download_botton);
            // formBtn.setTextColor(ColorBox.getColorForID(context,
            // R.color.text_green));
            // shateBtn.setBackgroundResource(R.drawable.download_botton);
            // shateBtn.setTextColor(ColorBox.getColorForID(context,
            // R.color.text_green));
            //
            // } else {
            // uploadBtn.setBackgroundResource(R.drawable.gone_botton);
            // uploadBtn.setTextColor(ColorBox.getColorForID(context,
            // R.color.text_gray_02));
            // deleteBtn.setBackgroundResource(R.drawable.gone_botton);
            // deleteBtn.setTextColor(ColorBox.getColorForID(context,
            // R.color.text_gray_02));
            // formBtn.setBackgroundResource(R.drawable.gone_botton);
            // formBtn.setTextColor(ColorBox.getColorForID(context,
            // R.color.text_gray_02));
            // shateBtn.setBackgroundResource(R.drawable.gone_botton);
            // shateBtn.setTextColor(ColorBox.getColorForID(context,
            // R.color.text_gray_02));
            // }
            isAllChoice();
        }

    };

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("", "返回一个UploadService对象");
            mUploadService = ((UploadBinder) service).getService();
            mUploadService.init();
            mUploadService.setCallbackListener(new UploadServiceListener() {

                @Override
                public void callback(UploadService service) {
                    if (picBaseAdapter != null) {
                        picBaseAdapter.notifyDataSetChanged();
                    }
                    if (troopBaseAdapter != null) {
                        setSynchronization((int) (service.getUploadPercent() * 100));
                    }
                }

            });
        }

    };
}
