package com.zmyh.r.upload.services;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.camera.util.ImageUtil;
import com.zmyh.r.dao.DBHandler;
import com.zmyh.r.handler.JsonHandle;
import com.zmyh.r.handler.UserObjHandle;
import com.zmyh.r.http.HttpUtilsBox;
import com.zmyh.r.http.UrlHandle;
import com.zmyh.r.tool.ShowMessage;
import com.zmyh.r.upload.UploadBinder;

public class UploadService extends Service {

    private final static long ONE_SECOND = 1000;

    private Context context;

    private static UploadServiceListener mUploadServiceListener;
    private static Thread mThread;
    private static UploadRunnable mRunnable;

    private static List<TroopObj> uploadTroopObjList;
    private static List<CameraPicObj> uploadCameraPicObjList;

    private static boolean isUpload;
    private static boolean isUploadPic;
    private static boolean isServiceRuning;

    public interface UploadServiceListener {
        public void callback(UploadService service);
    }

    public void setCallbackListener(UploadServiceListener callback) {
        mUploadServiceListener = callback;
    }

    public void init() {
        Log.e("UploadService", "初始化UploadService");
        context = this;

        if (mRunnable == null) {
            Log.e("UploadService", "初始化mRunnable");
            mRunnable = new UploadRunnable();
        }

        if (mThread == null) {
            Log.e("UploadService", "初始化mThread");
            mThread = new Thread(mRunnable);
            mThread.start();
        }
    }

    public double getUploadPercent() {
        double o = 0;
        for (CameraPicObj obj : uploadCameraPicObjList) {
            if ((obj.getMu_photo_type().equals("o") && (obj.getMax_state() == CameraPicObj.FINISH || obj
                    .getMax_state() == CameraPicObj.DEFEATED))
                    || (obj.getMu_photo_type().equals("m") && (obj.getState() == CameraPicObj.FINISH || obj
                    .getState() == CameraPicObj.DEFEATED))) {
                o += 1;
            }
        }

        Log.e("getUploadPercent", o + " / " + uploadCameraPicObjList.size());

        try {
            return o / uploadCameraPicObjList.size();
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new UploadBinder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("UploadService", "开启UploadService");
        context = this;
        if (mRunnable == null) {
            mRunnable = new UploadRunnable();
        }
        if (mThread == null) {
            mThread = new Thread(mRunnable);
            mThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 添加苗木组
     *
     * @param mTroopObj
     */
    public void addTroopObj(Context context, TroopObj mTroopObj,
                            String mu_photo_type) {
        ShowMessage.showToast(context, "将自动跳过已上传的图片!~");
        isServiceRuning = true;
        if (UserObjHandle.isLogin(context, true)) {
            if (uploadTroopObjList == null) {
                uploadTroopObjList = new ArrayList<TroopObj>();
            }
            uploadTroopObjList.add(mTroopObj);
            addUploadCameraPicObjList(mTroopObj, mu_photo_type);
        }

    }

    private void addUploadCameraPicObjList(TroopObj mTroopObj,
                                           String mu_photo_type) {
        if (uploadCameraPicObjList == null) {
            uploadCameraPicObjList = new ArrayList<CameraPicObj>();
        } else {
            uploadCameraPicObjList.removeAll(uploadCameraPicObjList);
        }
        for (CameraPicObj obj : mTroopObj.getCameraPicObjList()) {
            obj.setMu_photo_type(mu_photo_type);
            if (obj.getMu_photo_type().equals("o")) {
                if (obj.getMax_state() != CameraPicObj.FINISH) {
                    obj.setMax_state(CameraPicObj.WAIT);
                }
            } else {
                if (obj.getState() != CameraPicObj.FINISH) {
                    obj.setState(CameraPicObj.WAIT);
                }
            }
            uploadCameraPicObjList.add(obj);
        }
        Message.obtain(handler).sendToTarget();
    }

    /**
     * 添加多个苗木组
     *
     * @param uploadList
     */
    public void addTroopObj(Context context, List<TroopObj> uploadList,
                            String mu_photo_type) {
//		ShowMessage.showToast(context, "将自动跳过已上传的图片!~");
        isServiceRuning = true;
        if (UserObjHandle.isLogin(context, true)) {
            if (uploadTroopObjList == null) {
                uploadTroopObjList = new ArrayList<TroopObj>();
            }
            uploadTroopObjList.addAll(uploadList);
            // ********************************
            if (uploadCameraPicObjList == null) {
                uploadCameraPicObjList = new ArrayList<CameraPicObj>();
            } else {
                uploadCameraPicObjList.removeAll(uploadCameraPicObjList);
            }
            for (TroopObj mTroopObj : uploadList) {
                for (CameraPicObj obj : mTroopObj.getCameraPicObjList()) {
                    obj.setMu_photo_type(mu_photo_type);
                    if (obj.getMu_photo_type().equals("o")) {
                        if (obj.getMax_state() != CameraPicObj.FINISH) {
                            obj.setMax_state(CameraPicObj.WAIT);
                        }
                    } else {
                        obj.setState(CameraPicObj.NOTSTARTES);
                        if (obj.getState() != CameraPicObj.FINISH) {
                            obj.setState(CameraPicObj.WAIT);
                        }
                    }
                    uploadCameraPicObjList.add(obj);
                }
            }
            Message.obtain(handler).sendToTarget();
        }

    }

    public List<CameraPicObj> getUploadCameraPicObjList() {
        return uploadCameraPicObjList;
    }

    /**
     * 获取准备上传苗木组
     *
     * @return
     */
    private TroopObj getUploadTroopObj() {
        TroopObj mTroopObj = uploadTroopObjList.get(0);
        uploadTroopObjList.remove(mTroopObj);
        return mTroopObj;
    }

    public synchronized void stop() {
        isServiceRuning = false;
        sleep(2);
        if (uploadCameraPicObjList != null) {
            uploadCameraPicObjList.removeAll(uploadCameraPicObjList);
        }

        if (uploadTroopObjList != null) {
            uploadTroopObjList.removeAll(uploadTroopObjList);
        }
    }

    private static void sleep(int t) {
        try {
            Thread.sleep(t * ONE_SECOND);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addRequestParams(RequestParams params, String k, String v) {
        if (v != null && !v.equals("null") && !v.equals("0") && !v.equals("")) {
            params.addBodyParameter(k, v);
        }
    }

    /**
     * 获取准备上传苗木组token
     *
     * @param uploadTroopObj
     */
    private void getToken(final TroopObj uploadTroopObj) {
        String url = UrlHandle.getMmTree();

        RequestParams params = HttpUtilsBox.getRequestParams();
        addRequestParams(params, "mu_id", uploadTroopObj.getMu_id());
        addRequestParams(params, "user_id", UserObjHandle.getUsetId(context));
        addRequestParams(params, "mu_name", uploadTroopObj.getMu_name());
        addRequestParams(params, "mu_desc", uploadTroopObj.getMu_desc());
        addRequestParams(params, "mu_contact", uploadTroopObj.getMu_contact());
        addRequestParams(params, "mu_phone_1", uploadTroopObj.getMu_phone_1());
        addRequestParams(params, "mu_phone_2", uploadTroopObj.getMu_phone_2());
        addRequestParams(params, "mu_sz_type", uploadTroopObj.getMu_sz_type());
        addRequestParams(params, "mu_j_min", uploadTroopObj.getMu_j_min());
        addRequestParams(params, "mu_j_max", uploadTroopObj.getMu_j_max());
        addRequestParams(params, "mu_zg_min", uploadTroopObj.getMu_zg_min());
        addRequestParams(params, "mu_zg_max", uploadTroopObj.getMu_zg_max());
        addRequestParams(params, "mu_gf_min", uploadTroopObj.getMu_gf_min());
        addRequestParams(params, "mu_gf_max", uploadTroopObj.getMu_gf_max());
        addRequestParams(params, "mu_type", uploadTroopObj.getMu_type());
        addRequestParams(params, "mu_jz_time", uploadTroopObj.getMu_jz_time());
        addRequestParams(params, "mu_total", uploadTroopObj.getMu_total());
        addRequestParams(params, "mu_price", uploadTroopObj.getMu_price());
        addRequestParams(params, "mu_zb", uploadTroopObj.getMu_zb());
        addRequestParams(params, "mu_createTime",
                String.valueOf(uploadTroopObj.getMu_createTime()));
        addRequestParams(params, "mu_coordinate_long",
                String.valueOf(uploadTroopObj.getMu_longitude()));
        addRequestParams(params, "mu_coordinate_lat",
                String.valueOf(uploadTroopObj.getMu_latitude()));
        addRequestParams(params, "mu_gs", uploadTroopObj.getMu_gs());

        HttpUtilsBox.getHttpUtil().send(HttpMethod.POST, url, params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException exception, String msg) {
                        isUpload = true;
                        exception.printStackTrace();
                        ShowMessage.showFailure(context);
                        for (CameraPicObj obj : uploadTroopObj
                                .getCameraPicObjList()) {
                            if (obj.getMu_photo_type().equals("o")) {
                                obj.setMax_state(CameraPicObj.DEFEATED);
                            } else {
                                obj.setState(CameraPicObj.DEFEATED);
                            }

                            Message.obtain(handler).sendToTarget();
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        String result = responseInfo.result;
                        Log.d("", result);

                        JSONObject json = JsonHandle.getJSON(result);
                        if (json != null) {
                            String token = JsonHandle.getString(json, "token");
                            Log.d("token", token);
                            uploadPic(token, uploadTroopObj);
                        }
                    }

                });
    }

    /**
     * 上传图片线程
     *
     * @param token
     * @param uploadTroopObj
     */
    private void uploadPic(final String token, final TroopObj uploadTroopObj) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                List<CameraPicObj> uploadList = uploadTroopObj
                        .getCameraPicObjList();
                isUploadPic = true;
                boolean isRun = true;
                int i = 0;
                int max = uploadList.size();
                while (isRun) {
                    if (isUploadPic) {
                        if (isServiceRuning) {
                            if (i < max) {
                                CameraPicObj obj = uploadList.get(i);
                                Log.e("qiniu:", obj.getId());
                                i += 1;
                                if ((obj.getMu_photo_type().equals("o") && obj
                                        .getMax_state() != CameraPicObj.FINISH) || (obj.getMu_photo_type().equals("m") && obj.getState() != CameraPicObj.FINISH)) {
                                    isUploadPic = false;
                                    Log.e("qiniu:", obj.getId());
                                    obj.initStart(context, token,
                                            uploadTroopObj.getMu_id());
                                    if (obj.getMu_photo_type().equals("o")) {
                                        obj.setMax_state(CameraPicObj.HAVE_IN_HAND);
                                    } else {
                                        obj.setState(CameraPicObj.HAVE_IN_HAND);
                                    }
                                    uploadPic(obj);
                                }
                            } else {
                                isRun = false;
                                isUpload = true;
                                if (uploadList.get(0).getMu_photo_type()
                                        .equals("m")) {
                                    uploadTroopObj.setOnline(false);
                                    uploadTroopObj.setUpload(false);
                                    // TroopObj.save(context, uploadTroopObj);
                                    try {
                                        DBHandler.getDbUtils(context)
                                                .saveOrUpdate(uploadTroopObj);
                                    } catch (DbException e) {
                                        e.printStackTrace();
                                    }
                                }

                                Message.obtain(handler).sendToTarget();
                            }
                        }
                    }
                    sleep(1);
                }
            }
        }).start();
    }

    /**
     * 上传图片
     *
     * @param obj
     */
    private void uploadPic(final CameraPicObj obj) {
        if ((obj.getMu_photo_type().equals("o") && obj.getMax_state() != CameraPicObj.FINISH)
                || (obj.getMu_photo_type().equals("m") && obj.getState() != CameraPicObj.FINISH)) {
            UploadManager uploadManager = new UploadManager();
            Log.e("", "filename : " + obj.getId());
            File file;
            String name;
            if (obj.getMu_photo_type().equals("o")) {
                file = new File(obj.getOriginalFilePath());
                name = obj.getOriginalFileName();
            } else {
                file = new File(obj.getMediumFilePath());
                name = obj.getMediumFileName();
            }
            uploadManager.put(ImageUtil.getBytesFromFile(file), name,
                    obj.getToken(), new UpCompletionHandler() {

                        @Override
                        public void complete(String key,
                                             com.qiniu.android.http.ResponseInfo info,
                                             JSONObject response) {
                            isUploadPic = true;
                            Log.e("qiniu-info=" + key, info.toString());
                            if (response != null) {
                                Log.e(key, response.toString());
                                if (obj.getMu_photo_type().equals("o")) {
                                    obj.setMax_state(CameraPicObj.FINISH);
                                } else {
                                    obj.setState(CameraPicObj.FINISH);
                                }
                                JSONObject photoJson = JsonHandle.getJSON(
                                        response, "photo");
                                if (photoJson != null) {
                                    obj.setMu_photo_key(JsonHandle.getString(
                                            photoJson, "mu_photo_key"));
                                }
                                // if (obj.getMu_photo_type().equals("o")) {
                                // obj.setO_pic(CameraPicObj.FINISH);
                                // } else {
                                // obj.setM_pic(CameraPicObj.FINISH);
                                // }
                                // CameraPicObj.save(context, obj);
                                try {
                                    DBHandler.getDbUtils(context).saveOrUpdate(
                                            obj);
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (obj.getMu_photo_type().equals("o")) {
                                    obj.setMax_state(CameraPicObj.DEFEATED);
                                } else {
                                    obj.setState(CameraPicObj.DEFEATED);
                                }
                            }
                            Message.obtain(handler).sendToTarget();
                        }
                    }, new UploadOptions(obj.getMap(), "", false,
                            new UpProgressHandler() {

                                @Override
                                public void progress(String key, double percent) {
                                    Log.e("qiniu:", key + " = = = " + percent);
                                    if (obj.getMu_photo_type().equals("o")) {
                                        obj.setMax_state(CameraPicObj.HAVE_IN_HAND);
                                    } else {
                                        obj.setState(CameraPicObj.HAVE_IN_HAND);
                                    }
                                    obj.setPercent(percent);
                                    Message.obtain(handler).sendToTarget();
                                }
                            }, null));
        } else {
            isUploadPic = true;
            Message.obtain(handler).sendToTarget();
        }

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            if (mUploadServiceListener != null) {
                mUploadServiceListener.callback(UploadService.this);
            }
        }

    };

    /**
     * 检测是否存在上传苗木组线程
     *
     * @author Administrator
     */
    class UploadRunnable implements Runnable {

        public UploadRunnable() {
            isUpload = true;
        }

        @Override
        public void run() {

            while (true) {
                if (isServiceRuning) {
                    if (uploadTroopObjList != null
                            && !uploadTroopObjList.isEmpty()) {
                        Log.e("", uploadTroopObjList.get(0).getMu_id());
                        if (isUpload) {
                            isUpload = false;
                            getToken(getUploadTroopObj());
                        }
                    }
                }

                sleep(1);
            }
        }
    }

}
