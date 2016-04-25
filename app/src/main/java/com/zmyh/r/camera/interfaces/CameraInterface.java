package com.zmyh.r.camera.interfaces;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.zmyh.r.camera.util.CamParaUtil;
import com.zmyh.r.camera.util.ImageUtil;
import com.zmyh.r.handler.WinTool;

public class CameraInterface {

    public final static int rotate_1 = 90;
    public final static int rotate_2 = 270;

    private static final String TAG = "yanzi";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private static CameraInterface mCameraInterface;

    private Size previewSize;
    private Size pictureSize;

    private int cameraPosition = 1;

    public interface CamOpenOverCallback {
        public void cameraHasOpened(int orientation);
    }

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    /**
     * 打开Camera
     *
     * @param callback
     */
    public void doOpenCamera(CamOpenOverCallback callback) {
        Log.i(TAG, "Camera open....");
        mCamera = Camera.open();
        Log.i(TAG, "Camera open over....");
        cameraPosition = 1;
        callback.cameraHasOpened(rotate_1);

    }

    /**
     * 切换Camera
     *
     * @param callback
     */
    public boolean switchoverCamera(CamOpenOverCallback callback) {
        int cameraCount = 0;
        CameraInfo cameraInfo = new CameraInfo();
        cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
            if (cameraPosition == 1) { // 现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
                    doStopCamera();
                    mCamera = Camera.open(i);// 打开当前选中的摄像头
                    cameraPosition = 0;
                    callback.cameraHasOpened(rotate_1);
                    return false;
                }
            } else {// 现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {// 代表摄像头的方位，CAMERA_FACING_FRONT前置
                    doStopCamera();
                    mCamera = Camera.open(i);// 打开当前选中的摄像头
                    cameraPosition = 1;
                    callback.cameraHasOpened(rotate_1);
                    return true;
                }
            }
        }
        return cameraPosition == 1;
    }

    public Size getPictureSize() {
        return pictureSize;
    }

    /**
     * 开启预览
     *
     * @param holder
     * @param previewRate
     */
    public void doStartPreview(SurfaceHolder holder, float previewRate,Context context) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {

            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);// 设置拍照后存储的图片格式
            CamParaUtil.getInstance().printSupportPictureSize(mParams);
            CamParaUtil.getInstance().printSupportPreviewSize(mParams);
            // 设置PreviewSize和PictureSize
            previewSize = CamParaUtil.getInstance().getPropPreviewSize(
                    mParams.getSupportedPreviewSizes(), previewRate, 1024);
//            mParams.setPreviewSize(previewSize.width, previewSize.height);// 预览图片的大小
            mParams.setPreviewSize(WinTool.getWinHeight(context), WinTool.getWinWidth(context));// 预览图片的大小



            pictureSize = CamParaUtil.getInstance().getPropPictureSize(
                    mParams.getSupportedPictureSizes(), previewRate, 1024);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);// 储存图片的大小

            if (cameraPosition == 1) {
                mCamera.setDisplayOrientation(90);
            } else {
                mCamera.setDisplayOrientation(270);
            }

            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);

            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();// 开启预览
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters(); // 重新get一次
            Log.i(TAG,
                    "最终设置:PreviewSize--With = "
                            + mParams.getPreviewSize().width + "Height = "
                            + mParams.getPreviewSize().height);
            Log.i(TAG,
                    "最终设置:PictureSize--With = "
                            + mParams.getPictureSize().width + "Height = "
                            + mParams.getPictureSize().height);
        }
    }

    /**
     * 开启预览
     *
     * @param holder
     * @param previewRate
     * @param previewSize
     * @param pictureSize
     */
    public void doStartPreview(SurfaceHolder holder, float previewRate,
                               int width, int height, int orientation) {
        Log.i(TAG, "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);// 设置拍照后存储的图片格式
            // CamParaUtil.getInstance().printSupportPictureSize(mParams);
            // CamParaUtil.getInstance().printSupportPreviewSize(mParams);
            // 设置PreviewSize和PictureSize
            mParams.setPictureSize(width, height);// 储存图片的大小
            mParams.setPreviewSize(width, height);// 预览图片的大小

            mCamera.setDisplayOrientation(orientation);

            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);

            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();// 开启预览
            } catch (Exception e) {
                e.printStackTrace();
            }
            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters(); // 重新get一次
            Log.i(TAG,
                    "最终设置:PreviewSize--With = "
                            + mParams.getPreviewSize().width + "Height = "
                            + mParams.getPreviewSize().height);
            Log.i(TAG,
                    "最终设置:PictureSize--With = "
                            + mParams.getPictureSize().width + "Height = "
                            + mParams.getPictureSize().height);
        }
    }

    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mPreviwRate = -1f;
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 拍照
     */
    public void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    /**
     * 获取图片
     */
    public void doGetPicture(boolean isShutter,
                             final CameraCallbackLintener callback) {
        ShutterCallback s = null;
        if (isPreviewing && (mCamera != null)) {
            if (isShutter) {
                s = mShutterCallback;
            }
            mCamera.takePicture(s, null, new PictureCallback() {

                @Override
                public void onPictureTaken(byte[] data, Camera arg1) {
                    // Bitmap b = null;
                    if (null != data && callback != null) {
                        // b = BitmapFactory.decodeByteArray(data, 0,
                        // data.length);
                        // if (cameraPosition == 0) {
                        // b = ImageUtil.getPostScaleBitmap(b);
                        // b = ImageUtil.getRotateBitmap(b, 180.0f);
                        // }
                        // b = ImageUtil.getRotateBitmap(b, 90.0f);
                        callback.callback(data);
                        mCamera.startPreview();
                        isPreviewing = true;
                    }
                }
            });
        }
    }

    /**
     * 设置闪光灯
     *
     * @param flashModeTorch
     */
    public void setFlashMode(String flashModeTorch) {
        mParams = mCamera.getParameters();
        mParams.setFlashMode(flashModeTorch);
        mCamera.setParameters(mParams);
        mCamera.startPreview();
        // ***********************
        mParams = mCamera.getParameters();
    }

    /**
     * 对焦
     */
    public void setFocusMode() {
        mParams = mCamera.getParameters();
        mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1连续对焦
        mCamera.setParameters(mParams);
        mCamera.startPreview();
        // ***********************
        mCamera.cancelAutoFocus();
        mParams = mCamera.getParameters();
    }

    public synchronized void setFocusOnTouch(MotionEvent event,
                                             AutoFocusCallback autoFocusCallback) {
        try {
            Rect focusRect = calculateTapArea(event.getRawX(), event.getRawY(),
                    1f);
            Rect meteringRect = calculateTapArea(event.getRawX(),
                    event.getRawY(), 1.5f);

            mParams = mCamera.getParameters();
            mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

            if (mParams.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                focusAreas.add(new Camera.Area(focusRect, 1000));
                mParams.setFocusAreas(focusAreas);
            }

            if (mParams.getMaxNumMeteringAreas() > 0) {
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(meteringRect, 1000));
                mParams.setMeteringAreas(meteringAreas);
            }
            mCamera.setParameters(mParams);
            mCamera.autoFocus(autoFocusCallback);
            mParams = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
            autoFocusCallback.onAutoFocus(false, mCamera);
        }
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        float focusAreaSize = 300;
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

        int centerX = (int) (x / getResolution().width * 2000 - 1000);
        int centerY = (int) (y / getResolution().height * 2000 - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    public Camera.Size getResolution() {
        Camera.Parameters params = mCamera.getParameters();
        Camera.Size s = params.getPreviewSize();
        return s;
    }

    /* 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量 */
    ShutterCallback mShutterCallback = new ShutterCallback()
            // 快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
    {
        public void onShutter() {
            // TODO Auto-generated method stub
            Log.i(TAG, "myShutterCallback:onShutter...");
        }
    };
    PictureCallback mRawCallback = new PictureCallback()
            // 拍摄的未压缩原数据的回调,可以为null
    {

        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myRawCallback:onPictureTaken...");

        }
    };
    PictureCallback mJpegPictureCallback = new PictureCallback()
            // 对jpeg图像数据的回调,最重要的一个回调
    {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Log.i(TAG, "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);// data是字节数据，将其解析成位图
                mCamera.stopPreview();
                isPreviewing = false;
            }
            // 保存图片到sdcard
            if (null != b) {
                // 设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation",
                // 90)失效。
                // 图片竟然不能旋转了，故这里要旋转下
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                // FileUtil.saveBitmap(rotaBitmap);
            }
            // 再次进入预览
            mCamera.startPreview();
            isPreviewing = true;
        }
    };

}