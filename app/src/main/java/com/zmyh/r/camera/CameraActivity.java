package com.zmyh.r.camera;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zmyh.r.R;
import com.zmyh.r.box.CameraPicObj;
import com.zmyh.r.box.CommentObj;
import com.zmyh.r.box.TroopObj;
import com.zmyh.r.camera.interfaces.CameraCallbackLintener;
import com.zmyh.r.camera.interfaces.CameraInterface;
import com.zmyh.r.camera.interfaces.CameraInterface.CamOpenOverCallback;
import com.zmyh.r.camera.util.DisplayUtil;
import com.zmyh.r.camera.util.FileUtil;
import com.zmyh.r.camera.util.ImageUtil;
import com.zmyh.r.camera.view.CameraSurfaceView;
import com.zmyh.r.dailog.InputDialog;
import com.zmyh.r.dailog.MessageDialog;
import com.zmyh.r.dailog.MessageDialog.CallBackListener;
import com.zmyh.r.dao.DBHandler;
import com.zmyh.r.handler.DateHandle;
import com.zmyh.r.handler.MapHandler;
import com.zmyh.r.handler.MapHandler.MapListener;
import com.zmyh.r.handler.SystemHandle;
import com.zmyh.r.interfaces.CallbackForBoolean;
import com.zmyh.r.interfaces.CallbackForString;
import com.zmyh.r.photo.ImageListAcitvity;
import com.zmyh.r.photo.PhotoActivity;
import com.zmyh.r.tool.AddPicDialog;
import com.zmyh.r.tool.LineViewTool;
import com.zmyh.r.tool.Passageway;
import com.zmyh.r.tool.ShowMessage;

public class CameraActivity extends Activity {

    public static final String GET_ONR_KEY = "GET_ONE";
    public static final String NOT_SAVE_KEY = "NOT_MAX_KEY";
    private final static int SLEEP_TIME = 2500;

    private final static int CloseFlashlight = 101;
    private final static int FreeFlashlight = 102;
    private final static int OpenFlashlight = 103;

    private Context context;
    private float previewRate = -1f;
    private boolean isCameraFront = true, isGetOne = false, isNotSave = false;
    private int NOW_Flashlight;

    private DbUtils dbHandler;

    private SensorManager sensorManager = null;
    private Sensor aSensor;
    private Sensor mSensor;
    float[] accelerometerValues = new float[3];
    float[] magneticFieldValues = new float[3];

    private List<CameraPicObj> troopList = null;

    @ViewInject(R.id.camera_surfaceview)
    private CameraSurfaceView surfaceView;
    @ViewInject(R.id.camera_surfaceScreen)
    private ImageView surfaceScreen;
    @ViewInject(R.id.camera_flashlight)
    private ImageView flashlight;
    @ViewInject(R.id.camera_focus)
    private ImageView focus;
    @ViewInject(R.id.camera_flashlightBox)
    private LinearLayout flashlightBox;
    @ViewInject(R.id.camera_troopName)
    private ImageView troopIcon;
    @ViewInject(R.id.camera_photo)
    private ImageView photoIcon;
    @ViewInject(R.id.camera_takePic)
    private ImageView takePicIcon;
    @ViewInject(R.id.camera_minPicBox)
    private LinearLayout minPicBox;
    @ViewInject(R.id.camera_troopSize)
    private TextView troopSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ViewUtils.inject(this);
        context = this;

        initActivity();
        setonTouch();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i("--Main--", "onConfigurationChanged");
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraInterface.getInstance().doStopCamera();
        sensorManager.unregisterListener(myListener); // 解除监听器注册
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        doOpenCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            close();
        }
        return false;
    }

    @OnClick({R.id.camera_back, R.id.camera_takePic, R.id.camera_switchCamera,
            R.id.camera_flashlight, R.id.camera_closeFlashlight,
            R.id.camera_freeFlashlight, R.id.camera_openFlashlight,
            R.id.camera_troopName, R.id.camera_photo})
    public void onclick(View v) {
        switch (v.getId()) {
            case R.id.camera_back:
                close();
                break;
            case R.id.camera_takePic:
                doTakePicture();
                break;
            case R.id.camera_switchCamera:
                switchoverCamera();
                break;
            case R.id.camera_flashlight:
                showFlashlight();
                break;
            case R.id.camera_closeFlashlight:
                setFlashlight(CloseFlashlight);
                break;
            case R.id.camera_freeFlashlight:
                setFlashlight(FreeFlashlight);
                break;
            case R.id.camera_openFlashlight:
                setFlashlight(OpenFlashlight);
                break;
            case R.id.camera_troopName:
                showTroopDialog();
                break;
            case R.id.camera_photo:
                if (troopList.isEmpty()) {
                    Passageway.jumpActivity(context, PhotoActivity.class);
                } else {
                    showSaveMeaage();
                }
                break;
        }
    }

    private void close() {
        if (troopList.isEmpty()) {
            finish();
        } else {
            showSaveMeaage();
        }
    }

    private void showSaveMeaage() {
        showMeaageDialog("请先保存已拍植物照片");
    }

    private void showMeaageDialog(String title) {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage(title);
        dialog.setCancelStyle("好的");
        dialog.setCancelListener(null);
        dialog.setCommitStyle("不保存");
        dialog.setCommitListener(new CallBackListener() {

            @Override
            public void callback() {
                for (CameraPicObj obj : troopList) {
                    FileUtil.deleteImage(obj);
                }
                finish();
            }
        });
    }

    private void showMaxDialog() {
        MessageDialog dialog = new MessageDialog(context);
        dialog.setMessage("一组最多9张");
        dialog.setCommitStyle("知道了");
        dialog.setCommitListener(null);
    }

    private void showTroopDialog() {
        if (!troopList.isEmpty()) {
            if (!SystemHandle.getIsGoneShow(context)) {
                InputDialog dialog = new InputDialog(context);
                dialog.setHint("苗木名称");
                dialog.showBox();
                dialog.setListener(new CallbackForString() {

                    @Override
                    public void callback(String result) {
                        setTroopName(result);
                    }

                });
            } else {
                setTroopName("");
            }
        } else {
            MessageDialog dialog = new MessageDialog(context);
            dialog.setMessage("请先拍照");
            dialog.setCancelStyle("好的");
            dialog.setCancelListener(null);
        }
    }

    private void setTroopName(String result) {
        long t = DateHandle.getTime();

        TroopObj troopObj = new TroopObj();
        troopObj.setMu_name(result);
        troopObj.setMu_createTime(t);
        troopObj.initMu_id();
        troopObj.setCameraPicObjList(troopList);
        troopObj.setUpload(true);

        try {
            dbHandler.save(troopObj);
            dbHandler.saveAll(troopList);
        } catch (DbException e) {
            e.printStackTrace();
        }
        troopList.removeAll(troopList);
        minPicBox.removeAllViews();
        setTroopSize();
        ShowMessage.showToast(context, "保存成功");
    }

    private void showFlashlight() {
        if (flashlightBox.getVisibility() != View.VISIBLE) {
            flashlightBox.setVisibility(View.VISIBLE);
        } else {
            flashlightBox.setVisibility(View.INVISIBLE);
        }
    }

    private void switchoverCamera() {
        CameraInterface.getInstance().doGetPicture(false,
                new CameraCallbackLintener() {

                    @Override
                    public void callback(byte[] data) {
                        Options o = new Options();
                        o.inSampleSize = 10;
                        Bitmap b = BitmapFactory.decodeByteArray(data, 0,
                                data.length, o);
                        if (isCameraFront) {
                            b = ImageUtil.getRotateBitmap(b, 90.0f);
                        } else {
                            b = ImageUtil.getRotateBitmap(b, 270.0f);
                            b = ImageUtil.getPostScaleBitmap(b);
                        }

                        surfaceScreen.setImageBitmap(b);
                        surfaceScreen.setVisibility(View.VISIBLE);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (CameraInterface.getInstance()
                                        .switchoverCamera(camOpenOverCallback)) {
                                    isCameraFront = true;
                                    Message.obtain(handler, View.VISIBLE)
                                            .sendToTarget();
                                } else {
                                    isCameraFront = false;
                                    Message.obtain(handler, View.INVISIBLE)
                                            .sendToTarget();
                                }
                            }
                        }).start();
                    }
                });
    }

    private void doTakePicture() {
        if (troopList.size() < 9) {
            takePicIcon.setClickable(false);
            CameraInterface.getInstance().doGetPicture(true,
                    new CameraCallbackLintener() {

                        @Override
                        public void callback(byte[] data) {
                            if (null != data) {
                                doTakePicture(data);
                            }
                            takePicIcon.setClickable(true);
                        }
                    });
        } else {
            showMaxDialog();
        }
    }

    private void doTakePicture(byte[] data) {
        long createTime = DateHandle.getTime();
        CameraPicObj obj = new CameraPicObj();
        obj.setId(createTime);
        obj.setCreateAt(createTime);
        obj.setSize(data.length);

//        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
//        float[] R = new float[9];
//        float[] values = new float[3];
//        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
//        sensorManager.getOrientation(R, values);
//        Log.i("Sensor", "s");
//        for (Sensor sensor : sensors) {
//            Log.i("Sensor", sensor.getName().toString());
//        }
//        Log.i("Sensor", Math.toDegrees(values[0]) + "");
//        Log.i("Sensor", Math.toDegrees(values[1]) + "");
//        Log.i("Sensor", Math.toDegrees(values[2]) + "");
//        Log.i("Sensor", "e");

        FileUtil.saveBitmap(data, CameraInterface.getInstance()
                .getPictureSize(), obj);

        troopList.add(obj);

        getPicAddress(obj);

        if (isGetOne) {
            try {
                if (isNotSave) {
                    obj.setShow_max_pic(false);
                }
                dbHandler.save(obj);
                Intent i = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(AddPicDialog.CAMERA_KEY, obj.getId());
                i.putExtras(bundle);
                setResult(AddPicDialog.CAMERA_IMAGE_REQUEST_CODE, i);
                finish();
            } catch (DbException e) {
                e.printStackTrace();
            }

        } else {
            addMinPicBox(obj);
            setTroopSize();
        }

    }

    private void addMinPicBox(final CameraPicObj obj) {

        final View spaceView = LineViewTool.getSpaceView(context, 5);
        final MinPicView pic = new MinPicView(context, obj);

        minPicBox.addView(spaceView);
        minPicBox.addView(pic);

        pic.setCallbackListener(new CallbackForBoolean() {

            @Override
            public void callback(boolean isDelete) {
                if (isDelete) {
                    minPicBox.removeView(pic);
                    minPicBox.removeView(spaceView);
                    troopList.remove(obj);
                    setTroopSize();
                } else {
                    seePic(obj);
                }
            }
        });
    }

    private void setTroopSize() {
        if (troopList.size() == 0) {
            troopSize.setVisibility(View.INVISIBLE);
        } else {
            troopSize.setVisibility(View.VISIBLE);
            troopSize.setText(String.valueOf(troopList.size()));
        }
    }

    private void seePic(CameraPicObj obj) {
//         String path = obj.getMediumFilePath();
//         ArrayList<String> picList = new ArrayList<String>();
//         picList.add(path);
        Bundle b = new Bundle();
        b.putStringArrayList("iamge_list", getImageList());
        b.putInt("position", getImagePosition(obj));
        Passageway.jumpActivity(context, ImageListAcitvity.class, b);
    }

    private int getImagePosition(CameraPicObj obj) {
        if (!troopList.contains(obj)) {
            return 0;
        }
        int i = 0;
        for (CameraPicObj cameraPicObj : troopList) {
            if (cameraPicObj.equals(obj)) {
                return i;
            }
            i += 1;
        }
        return 0;
    }

    public ArrayList<String> getImageList() {
        ArrayList<String> list = new ArrayList<String>();
        for (CameraPicObj obj : troopList) {
            list.add(obj.getMediumFilePath());
        }
        return list;
    }

    private void getPicAddress(final CameraPicObj obj) {
        MapHandler.getPicAddress(context, new MapListener() {

            @Override
            public void callback(BDLocation location) {
                obj.setMu_latitude(location.getLatitude());
                obj.setMu_longitude(location.getLongitude());
                obj.setMu_zb(location.getAddrStr());
            }
        });
    }

    private void setonTouch() {
        surfaceView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isCameraFront) {
                    return true;
                }

                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        setFocusOnTouch(event);
                        CameraInterface.getInstance().setFocusOnTouch(event,
                                new AutoFocusCallback() {

                                    @Override
                                    public void onAutoFocus(boolean b, Camera camera) {
                                        Log.e("", b + "");
                                    }
                                });
                        break;
                }
                return true;
            }

        });

    }

    private void setFocusOnTouch(MotionEvent event) {
        focus.setVisibility(View.VISIBLE);
        int left = (int) event.getRawX() - (focus.getWidth() / 2);
        int top = (int) event.getY() - (focus.getHeight() / 2);
        int right = 0;
        int bottom = 0;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) focus
                .getLayoutParams();
        params.setMargins(left, top, right, bottom);
        focus.setLayoutParams(params);
        Log.i("@@@@@@", left + ", " + top + ", " + right + ", " + bottom);
        runTimer();
    }

    private void runTimer() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEP_TIME);
                    Message.obtain(handler, SLEEP_TIME).sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initActivity() {
        troopList = new ArrayList<CameraPicObj>();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            isGetOne = b.getBoolean(GET_ONR_KEY);
            isNotSave = b.getBoolean(NOT_SAVE_KEY);
            if (isGetOne) {
                troopIcon.setVisibility(View.INVISIBLE);
                photoIcon.setVisibility(View.INVISIBLE);
            }
        }
        NOW_Flashlight = CloseFlashlight;
        doOpenCamera();

        dbHandler = DBHandler.getDbUtils(context);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorManager.registerListener(myListener, aSensor,
                SensorManager.SENSOR_DELAY_NORMAL);  //为传感器注册监听器
        sensorManager.registerListener(myListener, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);  //为传感器注册监听器
    }

    private void doOpenCamera() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                CameraInterface.getInstance().doOpenCamera(camOpenOverCallback);
                isCameraFront = true;
            }
        }).start();
    }

    private void setFlashlight(int i) {
        flashlightBox.setVisibility(View.INVISIBLE);
        NOW_Flashlight = i;
        switch (i) {
            case CloseFlashlight:
                CameraInterface.getInstance().setFlashMode(
                        Parameters.FLASH_MODE_OFF);
                flashlight.setImageResource(R.drawable.flashlight_close_icon);
                break;
            case FreeFlashlight:
                CameraInterface.getInstance().setFlashMode(
                        Parameters.FLASH_MODE_AUTO);
                flashlight.setImageResource(R.drawable.flashlight_free_icon);
                break;
            case OpenFlashlight:
                CameraInterface.getInstance()
                        .setFlashMode(Parameters.FLASH_MODE_ON);
                flashlight.setImageResource(R.drawable.flashlight_open_icon);
                break;
        }
    }

    private CamOpenOverCallback camOpenOverCallback = new CamOpenOverCallback() {

        @Override
        public void cameraHasOpened(int orientation) {
            Message.obtain(handler, orientation).sendToTarget();
        }
    };

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case View.VISIBLE:
                    flashlight.setVisibility(View.VISIBLE);
                    break;
                case View.INVISIBLE:
                    flashlight.setVisibility(View.INVISIBLE);
                    break;
                case SLEEP_TIME:
                    focus.setVisibility(View.GONE);
                    break;
                case CameraInterface.rotate_1:
                case CameraInterface.rotate_2:
                    int w = surfaceView.getWidth();
                    int h = surfaceView.getHeight();
                    if (w < h) {
                        int p = w;
                        w = h;
                        h = p;
                    }
                    previewRate = DisplayUtil.getScreenRate(w, h);
                    SurfaceHolder holder = surfaceView.getSurfaceHolder();
                    CameraInterface.getInstance().doStartPreview(holder,
                            previewRate);
                    surfaceScreen.setVisibility(View.GONE);
                    setFlashlight(NOW_Flashlight);
                    break;
            }
        }

    };

    final SensorEventListener myListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent sensorEvent) {

            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticFieldValues = sensorEvent.values;
            }
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerValues = sensorEvent.values;
            }
            calculateOrientation();
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
        SensorManager.getOrientation(R, values);

        // 要经过一次数据格式的转换，转换为度
        values[0] = (float) Math.toDegrees(values[0]);
        values[1] = (float) Math.toDegrees(values[1]);
        values[2] = (float) Math.toDegrees(values[2]);

        Log.i("Sensor", " Z : " + values[0] + "");
        Log.i("Sensor", " X : " + values[1] + "");
        Log.i("Sensor", " Y : " + values[2] + "");

    }
}
